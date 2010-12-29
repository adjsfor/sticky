package com.google.appengine.demos.sticky.server;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;

public class PhotoRetrieverServlet extends HttpServlet {
    
    private static final long serialVersionUID = -1342651769522383605L;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String param = request.getParameter("hash");
        System.out.println("RECEIVING KEY: " + param);
        PersistenceManager pm = PMF.get().getPersistenceManager();

        Query query = pm.newQuery(Store.Photo.class);
        query.setFilter("hashCode == paramName");
        query.declareParameters("int paramName");
        Store.Photo photo = ((List<Store.Photo>) query.execute(Integer.parseInt(param))).get(0);
        if (photo != null) {
            System.out.println("PHOTO: " + photo.getHashCode() + " " + photo.getImage().toString());
        }
        
        Blob blob = photo.getImage();
        pm.close();
        
        response.setContentType("image/jpeg");
        response.getOutputStream().write(blob.getBytes());
        
    }
}
