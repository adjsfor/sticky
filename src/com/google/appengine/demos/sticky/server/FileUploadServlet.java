package com.google.appengine.demos.sticky.server;

import java.io.ByteArrayOutputStream;
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
                System.out.println("test");
                    System.out.println("test2");
                    FileItemStream stream = iterator.next();
                    InputStream is = stream.openStream();
                    
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    int len;
                    byte[] buffer = new byte[8192];
                    
                    while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    
//                    int maxFileSize = 10 * 1024 * 2;
//                    if (os.size() > maxFileSize) {
//                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//                            "File exceeds maximum file length.");
//                        return;
//                    }
                    
                    Blob blob = new Blob(IOUtils.toByteArray(is));
                    Store.Photo photo = new Store.Photo();
                    photo.setImage(blob);
                    photo.setName("Name"); //We have to generate a name or take the file name or something..

                    PersistenceManager pm = PMF.get().getPersistenceManager();
                    pm.makePersistent(photo);
                    pm.close();
                    
                    System.out.println(stream.getFieldName());
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("text/html");
                response.getWriter().write("File uploaded successfully.");
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
