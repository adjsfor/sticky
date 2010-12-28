package com.google.appengine.demos.sticky.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadServlet  extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("test");
		if(ServletFileUpload.isMultipartContent(request)) {
			
			FileItemFactory itemFactory = new DiskFileItemFactory();
			
			ServletFileUpload upload = new ServletFileUpload(itemFactory);
//			System.out.println("test2");
//			response.setStatus(HttpServletResponse.SC_CREATED);
//			response.getWriter().print("File uploaded successfully.3");
//			response.flushBuffer();
			try {
				List<FileItem> items = upload.parseRequest(request);
				//System.out.println(items.size());
				for(FileItem item : items) {
					System.out.println("test3");
					response.setStatus(HttpServletResponse.SC_CREATED);
					response.getWriter().print("File uploaded successfully.2");
					response.flushBuffer();
					System.out.println("test4");
					if(!item.isFormField()) 
						break;
					System.out.println("test5");
					
					String filename = item.getName();
					System.out.println(filename);
					
					response.setStatus(HttpServletResponse.SC_CREATED);
					response.getWriter().print("File uploaded successfully.");
					response.flushBuffer();
					
				}
			} catch (FileUploadException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occured while uploading the file: " + e.getMessage());
			}
		} else
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "This request content type is not supported.");
		
		
	}
}
