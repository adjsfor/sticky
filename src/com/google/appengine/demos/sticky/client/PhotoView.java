package com.google.appengine.demos.sticky.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.demos.sticky.client.model.Comment;
import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.appengine.demos.sticky.client.model.Surface;
import com.google.appengine.demos.sticky.client.model.Transformation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class PhotoView extends VerticalPanel implements Model.DataObserver {
    
    private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "fileupload";
    
    private static final String PHOTO_ACTION_URL = GWT.getModuleBaseURL() + "photos";
    
    private static final Logger LOG = Logger.getLogger(PhotoView.class.toString());
    
    private Note note;
    
    private Model model;
    
    private FormPanel form = new FormPanel();
    
    private FileUpload fu =  new FileUpload();
    
    private Image image = new Image();

    private VerticalPanel holder;
    
    private Callback callback;
    
    public PhotoView(Model model, Note note, Callback callback) {
        this.model = model;
        this.note = note;
        this.callback = callback;
    }
    
    public Widget getFileUploaderWidget() {
        // Add upload form only to owner notes.
        if (note.getAuthorName().equals("You")) {
            this.form.setAction(UPLOAD_ACTION_URL);
            this.form.setEncoding(FormPanel.ENCODING_MULTIPART);
            this.form.setMethod(FormPanel.METHOD_POST);
            
            holder = new VerticalPanel();
            
            fu.setName("uploadFormElement");
            
            Button submitButton = new Button("Submit");
            submitButton.addClickHandler(new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    form.submit();
                }
            });
            holder.add(fu);
            holder.add(submitButton);
            
            form.add(holder);
            form.addSubmitHandler(new SubmitHandler() {
                
                @Override
                public void onSubmit(SubmitEvent event) {
                    final ImageFileFilter filter = new ImageFileFilter();
                    String filename = fu.getFilename();
                    if (filename.length() == 0 || !filter.accept(filename)) {
                        Window.alert("No File is selected or file is not supported");
                        event.cancel();
                    } else {
                        GWT.log("Filename: " + filename);
                    }
                }
            });
            
            form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
                
                @Override
                public void onSubmitComplete(SubmitCompleteEvent event) {
                    LOG.log(Level.SEVERE, "KEY: " + event.getResults());
                    
                    try {
                        int hash = Integer.parseInt(event.getResults());
                        model.updateNoteImage(note, hash, Transformation.NONE);
                        callback.callback();
                    } catch (NumberFormatException e) {
                        //do nothing
                    }
                }
            });
            
        }
        
        return form;
    }

    @Override
    public void onCommentAdded(Comment comment) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onNoteCreated(Note note) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSurfaceCreated(Surface surface) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSurfaceNotesReceived(Note[] notes) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSurfaceSelected(Surface nowSelected, Surface wasSelected) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSurfacesReceived(Surface[] surfaces) {
        // TODO Auto-generated method stub
        
    }

    public Image getPhoto() {
        LOG.log(Level.INFO, "" + note.getHashCode());
        if (this.note.getHashCode() != null && this.note.getHashCode() != 0) {
            LOG.log(Level.INFO, "rendering");
            if (holder != null) {
                holder.clear();
            }
            
            image = new Image(PHOTO_ACTION_URL + "?hash=" + this.note.getHashCode());
            image.setSize("15em", "15em");
            
            return image;
        } else {
            LOG.log(Level.INFO, "null, not rendering");
            return null;
        }
        
    }

}
