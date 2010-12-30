/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.appengine.demos.sticky.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.demos.sticky.client.model.Author;
import com.google.appengine.demos.sticky.client.model.Comment;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.appengine.demos.sticky.client.model.Service;
import com.google.appengine.demos.sticky.client.model.Surface;
import com.google.appengine.demos.sticky.client.model.Transformation;
import com.google.appengine.demos.sticky.server.Store.Photo;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side RPC endpoint for {@link Service}.
 * 
 * 
 */
@SuppressWarnings("serial")
public class ServiceImpl extends RemoteServiceServlet implements Service {
    
    private static final int TIMESTAMP_PADDING = 1000 * 60;
    
    private static Date convertTimestampToDate(String timetamp) {
        // To ensure we don't miss any events due to clock differences, we will
        // expand the time window by 1 minute.
        return new Date(Long.parseLong(timetamp, 16) - TIMESTAMP_PADDING);
    }
    
    private static String createTimestamp() {
        // The client should never need to do math on the timestamp and
        // returning a
        // long value into GWT just complicates the client side code since GWT
        // will
        // load code to emulate longs. To simplify things, we return a hex
        // encoded
        // string.
        return Long.toString(System.currentTimeMillis(), 16);
    }
    
    private static Note[] getNotesSinceTimestamp(Note[] notes, String timestamp) {
        if (timestamp == null) {
            return notes;
        } else {
            // Get the actual date + padding represented by the timestamp.
            final Date since = convertTimestampToDate(timestamp);
            final List<Note> newNotes = new ArrayList<Note>(notes.length);
            // Return only those notes that were updated after since.
            for (Note note : notes) {
                if (note.getLastUpdatedAt().after(since)) {
                    newNotes.add(note);
                }
            }
            return newNotes.toArray(new Note[newNotes.size()]);
        }
    }
    
    private static String getSurfaceKey(Store.Note note) {
        // Use the fact that surface and note have parent/child keys to very
        // quickly
        // determine the surface key for a particular note.
        return KeyFactory.keyToString(note.getKey().getParent());
    }
    
    private static Note[] toClientNotes(Collection<Store.Note> notes) {
        final Note[] clients = new Note[notes.size()];
        int i = 0;
        for (Store.Note n : notes) {
            
            final List<Comment> comments = new ArrayList<Comment>();
            for (Store.Comment c : n.getComments()) {
                comments.add(new Comment(KeyFactory.keyToString(n.getKey()), c.getUser(), c.getText()));
            }
            System.out.println("IN toClientNotes: " + n.getHashCode());
            clients[i++] = new Note(KeyFactory.keyToString(n.getKey()), n.getX(), n.getY(), n.getWidth(),
                n.getHeight(), comments, n.getLastUpdatedAt(), n.getAuthorName(), n.getAuthorEmail(), n.getHashCode());
        }
        return clients;
    }
    
    private static Surface toClientSurface(Store.Surface surface) {
        final List<String> names = surface.getAuthorNames();
        return new Surface(KeyFactory.keyToString(surface.getKey()), surface.getTitle(), names.toArray(new String[names
            .size()]), surface.getNotes().size(), surface.getLastUpdatedAt());
    }
    
    /**
     * A convenient way to get the current user and throw an exception if the
     * user isn't logged in.
     * 
     * @param userService
     *            the user service to use
     * @return the current user
     * @throws AccessDeniedException
     */
    private static User tryGetCurrentUser(UserService userService) throws AccessDeniedException {
        if (!userService.isUserLoggedIn()) {
            throw new Service.AccessDeniedException();
        }
        return userService.getCurrentUser();
    }
    
    /**
     * A reference to the data store.
     */
    private final Store store = new Store("transactions-optional");
    
    /**
     * A reference to a cache service.
     */
    private final Cache cache = new Cache(MemcacheServiceFactory.getMemcacheService());
    
    public AddAuthorToSurfaceResult addAuthorToSurface(final String surfaceKey, final String email)
        throws AccessDeniedException {
        final User user = tryGetCurrentUser(UserServiceFactory.getUserService());
        final Store.Api api = store.getApi();
        try {
            final Key key = KeyFactory.stringToKey(surfaceKey);
            
            final Store.Author me = api.getOrCreateNewAuthor(user);
            
            // Find an author with the given email address. If we can't find it,
            // we'll
            // return null to the client to indicate that the author does not
            // exist
            final Store.Author author = api.tryGetAuthor(email);
            if (author == null) {
                return null;
            }
            
            // Verify that author has access to the surface that is being
            // changed.
            if (!me.hasSurface(key)) {
                throw new Service.AccessDeniedException();
            }
            
            final Store.Surface surface = api.getSurface(key);
            // If the author already belongs to the surface, we return success
            // without
            // making any changes to the store.
            if (!author.hasSurface(key)) {
                
                cache.deleteSurfaceKeys(author.getEmail());
                cache.deleteSurface(surface.getKey());
                
                // Add the surface key to the author object. Since we'll be
                // updating an
                // object, carry out the operation in a transaction.
                final Transaction txA = api.begin();
                author.addSurface(surface);
                api.saveAuthor(author);
                txA.commit();
                
                // Add the author name to the surface.
                final Transaction txB = api.begin();
                surface.addAuthorName(author.getName());
                api.saveSurface(surface);
                txB.commit();
            }
            return new AddAuthorToSurfaceResult(author.getName(), surface.getLastUpdatedAt());
            
        } finally {
            api.close();
        }
    }
    
    public Date changeNoteContent(final String noteKey, final List<Comment> comments) throws AccessDeniedException {
        final Store.Api api = store.getApi();
        try {
            // Convert the string version of the key to an actual key.
            final Key key = KeyFactory.stringToKey(noteKey);
            
            // Start a transaction for the Note we're updating.
            final Transaction tx = api.begin();
            final Store.Note note = api.getNote(key);
            
            List<Store.Comment> sComments = new ArrayList<Store.Comment>();
            for (Comment c : comments) {
                sComments.add(new Store.Comment(c.getAuthor(), c.getText()));
            }
            
            note.setComments(sComments);
            
            final Date result = api.saveNote(note).getLastUpdatedAt();
            tx.commit();
            
            // Invalidate the notes cache for the surface that owns this Note.
            cache.deleteNotes(getSurfaceKey(note));
            return result;
        } finally {
            api.close();
        }
    }
    
    @Override
    public Date changeNotePhoto(String noteKey, int hash, Transformation transformation) throws AccessDeniedException {
        final User user = tryGetCurrentUser(UserServiceFactory.getUserService());
        final Store.Api api = store.getApi();
        try {
            // Convert the string version of the key to an actual key.
            final Key key = KeyFactory.stringToKey(noteKey);
            final Store.Author me = api.getOrCreateNewAuthor(user);
            // Start a transaction for the Note we're updating.
//            final Transaction tx = api.begin();
            final Store.Note note = api.getNote(key);
            
            if (!note.isOwnedBy(me)) {
                throw new Service.AccessDeniedException();
            }

            
            System.out.println("SeviceImpl: " + hash);
            note.setHashCode(hash);
            
            //TODO Transformation
            if(transformation.compareTo(Transformation.NONE) != 0 && hash != 0){
                final Store.Photo photo = api.getPhoto(hash);
            	this.transform(photo, transformation);
            	api.savePhoto(photo);
            }
            
            
            final Date result = api.saveNote(note).getLastUpdatedAt();
//            tx.commit();
            
            // Invalidate the notes cache for the surface that owns this Note.
            cache.deleteNotes(getSurfaceKey(note));
            return result;
        } finally {
            api.close();
        }
    }
    
    public void transform(Store.Photo photo, Transformation transformation) {
        System.out.println("Transforming picture: " + transformation);
                ImagesService imagesService = ImagesServiceFactory
                                .getImagesService();
                Image oldImage = ImagesServiceFactory.makeImage(photo.getImage()
                                .getBytes());
                Image newImage;
                Transform transform;
                byte[] newImageData = null;
                //PersistenceManager pm = PMF.get().getPersistenceManager();
                switch (transformation) {
                case CROP:
                        //TODO Crop
//                      double leftX = 0.;
//                      double topY = 0.;
//                      double rightX = 0.;
//                      double bottomY = 0.;
//                      transform = ImagesServiceFactory.makeCrop(leftX, topY, rightX,
//                                      bottomY);
//                      newImage = imagesService.applyTransform(transform, oldImage);
//                      newImageData = newImage.getImageData();
//                      this.setImage(new Blob(newImageData));
//                      pm.makePersistent(this);
//                      pm.close();
                        break;
                case LUCKY: 
                        transform = ImagesServiceFactory.makeImFeelingLucky();
                        newImage = imagesService.applyTransform(transform, oldImage);
                        newImageData = newImage.getImageData();
                        photo.setImage(new Blob(newImageData));
                case FLIP_H:
                        transform = ImagesServiceFactory.makeHorizontalFlip();
                        newImage = imagesService.applyTransform(transform, oldImage);
                        newImageData = newImage.getImageData();
                        photo.setImage(new Blob(newImageData));
//                        pm.makePersistent(photo);
//                        pm.close();
                        break;
                case FLIP_V:
                        transform = ImagesServiceFactory.makeVerticalFlip();
                        newImage = imagesService.applyTransform(transform, oldImage);
                        newImageData = newImage.getImageData();
                        photo.setImage(new Blob(newImageData));
//                        pm.makePersistent(photo);
//                        pm.close();
                        break;
                case ROT_C:
                        transform = ImagesServiceFactory.makeRotate(90);
                        newImage = imagesService.applyTransform(transform, oldImage);
                        newImageData = newImage.getImageData();
                        photo.setImage(new Blob(newImageData));
//                        pm.makePersistent(photo);
//                        pm.close();
                        break;
                case ROT_CC:
                        transform = ImagesServiceFactory.makeRotate(-90);
                        newImage = imagesService.applyTransform(transform, oldImage);
                        newImageData = newImage.getImageData();
                        photo.setImage(new Blob(newImageData));
//                        pm.makePersistent(photo);
//                        pm.close();
                        break;
                case NONE:
                        break;
                default:
                        break;
                }
        }
    
    public Date changeNotePosition(final String noteKey, final int x, final int y, final int width, final int height)
        throws AccessDeniedException {
        final User user = tryGetCurrentUser(UserServiceFactory.getUserService());
        final Store.Api api = store.getApi();
        try {
            // Convert the string version of the key into an actual key.
            final Key key = KeyFactory.stringToKey(noteKey);
            final Store.Author me = api.getOrCreateNewAuthor(user);
            
            // Start a transaction for the Note we're updating.
            final Transaction tx = api.begin();
            final Store.Note note = api.getNote(key);
            // Verify that the author owns the Note.
            if (!note.isOwnedBy(me)) {
                throw new Service.AccessDeniedException();
            }
            note.setX(x);
            note.setY(y);
            note.setWidth(width);
            note.setHeight(height);
            final Date result = api.saveNote(note).getLastUpdatedAt();
            tx.commit();
            
            // Invalidate the notes cache for the surface that owns this Note.
            cache.deleteNotes(getSurfaceKey(note));
            return result;
        } finally {
            api.close();
        }
    }
    
    public CreateObjectResult createNote(final String surfaceKey, final int x, final int y, final int width,
        final int height) throws AccessDeniedException {
        final User user = tryGetCurrentUser(UserServiceFactory.getUserService());
        final Store.Api api = store.getApi();
        try {
            // Convert the string version of the key to the actual Key.
            final Key key = KeyFactory.stringToKey(surfaceKey);
            final Store.Author me = api.getOrCreateNewAuthor(user);
            
            // Verify that the author is actually a member of the surface.
            if (!me.hasSurface(key)) {
                throw new Service.AccessDeniedException();
            }
            
            // Start a transaction for the surface update.
            final Transaction tx = api.begin();
            final Store.Surface surface = api.getSurface(key);
            final Store.Note note = new Store.Note(me, x, y, width, height);
            surface.getNotes().add(note);
            api.saveSurface(surface);
            
            final CreateObjectResult result = new CreateObjectResult(KeyFactory.keyToString(note.getKey()), note
                .getLastUpdatedAt());
            tx.commit();
            
            // Invalidate the cache for the surface.
            cache.deleteNotes(surfaceKey);
            return result;
        } finally {
            api.close();
        }
    }
    
    public CreateObjectResult createSurface(final String title) throws AccessDeniedException {
        final User user = tryGetCurrentUser(UserServiceFactory.getUserService());
        final Store.Api api = store.getApi();
        try {
            final Store.Author me = api.getOrCreateNewAuthor(user);
            
            final Store.Surface surface = new Store.Surface(title);
            surface.addAuthorName(me.getName());
            api.saveSurface(surface);
            
            final Transaction tx = api.begin();
            me.addSurface(surface);
            api.saveAuthor(me);
            tx.commit();
            
            // Invalidate the cached surface keys for this author.
            cache.deleteSurfaceKeys(me.getEmail());
            
            return new CreateObjectResult(KeyFactory.keyToString(surface.getKey()), surface.getLastUpdatedAt());
        } finally {
            api.close();
        }
    }
    
    public GetNotesResult getNotes(String surfaceKey, String since) throws AccessDeniedException {
        final User user = tryGetCurrentUser(UserServiceFactory.getUserService());
        return new Service.GetNotesResult(createTimestamp(), getNotes(user, surfaceKey, since));
    }
    
    public GetSurfacesResult getSurfaces(String timestamp) throws AccessDeniedException {
        final User user = tryGetCurrentUser(UserServiceFactory.getUserService());
        final Store.Api api = store.getApi();
        try {
            // getSurfaceKeys will return a cached entry if possible.
            final List<Key> keys = getSurfaceKeys(api, user);
            final Surface[] surfaces = new Surface[keys.size()];
            for (int i = 0, n = keys.size(); i < n; ++i) {
                // getSurface will return a cached entry if possible.
                surfaces[i] = getSurface(api, keys.get(i));
            }
            return new GetSurfacesResult(null, surfaces);
        } finally {
            api.close();
        }
    }
    
    public UserInfoResult getUserInfo() throws AccessDeniedException {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = tryGetCurrentUser(userService);
        final Store.Api api = store.getApi();
        try {
            final Key surfaceKey = getSurfaceKeys(api, user).get(0);
            final UserInfoResult result = new Service.UserInfoResult(new Author(user.getEmail(), user.getNickname()),
                getSurface(api, surfaceKey), userService.createLogoutURL(userService.createLoginURL("/")));
            return result;
        } finally {
            api.close();
        }
    }
    
    private Note[] getNotes(User user, String surfaceKey, String since) throws AccessDeniedException {
        final Store.Api api = store.getApi();
        try {
            // Attempt to load from cache.
            final Note[] fromCache = cache.getNotes(user, surfaceKey);
            if (fromCache != null) {
                return getNotesSinceTimestamp(fromCache, since);
            }
            
            // Cache lookup failed, query the data store.
            final Key key = KeyFactory.stringToKey(surfaceKey);
            final Store.Author me = api.getOrCreateNewAuthor(user);
            if (!me.hasSurface(key)) {
                throw new Service.AccessDeniedException();
            }
            final Store.Surface surface = api.getSurface(key);
            final Note[] notes = cache.putNotes(user, surfaceKey, toClientNotes(surface.getNotes()));
            return getNotesSinceTimestamp(notes, since);
        } finally {
            api.close();
        }
    }
    
    private Surface getSurface(Store.Api api, Key key) {
        // Attempt to load from cache.
        final Surface fromCache = cache.getSurface(key);
        if (fromCache != null) {
            return fromCache;
        }
        
        // Cache lookup failed, query the data store.
        return cache.putSurface(key, toClientSurface(api.getSurface(key)));
    }
    
    private List<Key> getSurfaceKeys(Store.Api api, User user) {
        final String email = user.getEmail();
        
        // Attempt to load from cache.
        final List<Key> fromCache = cache.getSurfaceKeys(email);
        if (fromCache != null) {
            return fromCache;
        }
        
        // Cache lookup failed, query the data store.
        final Store.Author author = api.getOrCreateNewAuthor(user);
        return cache.putSurfaceKeys(email, author.getSurfaceKeys());
    }
    
}
