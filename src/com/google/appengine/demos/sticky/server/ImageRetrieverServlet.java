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

public class ImageRetrieverServlet  extends HttpServlet {

    private static final long serialVersionUID = -1342651769522383605L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        PersistenceManager pm = PMF.get().getPersistenceManager();

        Query query = pm.newQuery(Store.Photo.class, "name == nameParam");
        query.declareParameters("String nameParam");

        List<Store.Photo> images = (List<Store.Photo>) query.execute(request
                        .getParameter("name"));
        Blob image = images.get(0).getImage();
        pm.close();

        response.setContentType("image/jpeg");
        response.getOutputStream().write(image.getBytes());
        
    }
}
