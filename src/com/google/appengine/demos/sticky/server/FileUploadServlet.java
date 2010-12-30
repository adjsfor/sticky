package com.google.appengine.demos.sticky.server;

import java.io.IOException;
import java.io.InputStream;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.Blob;

public class FileUploadServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (ServletFileUpload.isMultipartContent(request)) {
            
            // FileItemFactory itemFactory = new DiskFileItemFactory();
            
            // ServletFileUpload upload = new ServletFileUpload(itemFactory);
            ServletFileUpload upload = new ServletFileUpload();
            
            try {
                FileItemIterator iterator = upload.getItemIterator(request);
                FileItemStream stream = iterator.next();
                InputStream is = stream.openStream();
                
                Blob blob = new Blob(IOUtils.toByteArray(is));
                Store.Photo photo = new Store.Photo();
                photo.setImage(blob);
                PersistenceManager pm = PMF.get().getPersistenceManager();
                pm.makePersistent(photo);
                photo.setHashCode(photo.getKey().hashCode());
                
                pm.close();
                
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("text/html");
                System.out.println("HASH: " + photo.getHashCode());
                response.getWriter().write(""+photo.getHashCode());
                response.flushBuffer();
                
            } catch (FileUploadException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occured while uploading the file: " + e.getMessage());
            }
        } else
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                "This request content type is not supported.");
    }
}
