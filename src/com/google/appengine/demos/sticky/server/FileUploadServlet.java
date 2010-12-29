package com.google.appengine.demos.sticky.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadServlet  extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(ServletFileUpload.isMultipartContent(request)) {
			
			//FileItemFactory itemFactory = new DiskFileItemFactory();
			
			//ServletFileUpload upload = new ServletFileUpload(itemFactory);
			ServletFileUpload upload = new ServletFileUpload();
						
			try {
				FileItemIterator iterator = upload.getItemIterator(request);
				System.out.println("test");
				while (iterator.hasNext()) {
					System.out.println("test2");
					FileItemStream stream = iterator.next();
					InputStream is = stream.openStream();
					
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					int len;
					byte[] buffer = new byte[8192];
					
					while((len = is.read(buffer, 0, buffer.length)) != -1) {
						os.write(buffer, 0, len);
					}
					
					int maxFileSize = 10 * 1024 * 2;
					if(os.size() > maxFileSize) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "File exceeds maximum file length.");
						return;
					}
					
					System.out.println(stream.getFieldName());
				}
				response.setStatus(HttpServletResponse.SC_CREATED);
				response.getWriter().write("File uploaded successfully.");
				response.flushBuffer();
				//List<FileItem> items = upload.parseRequest(request);
				//System.out.println(items.size());
				//response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "This request content type is not supported.");
//				response.setStatus(HttpServletResponse.SC_CREATED);
//				response.getWriter().write("File uploaded successfully.2");
//				response.flushBuffer();
				/*
				for(FileItem item : items) {
					System.out.println("test3");
					response.setStatus(HttpServletResponse.SC_CREATED);
					response.getWriter().print("File uploaded successfully.2");
					response.flushBuffer();
					System.out.println("test4");
					if(item.isFormField()) 
						continue;
					System.out.println("test5");
					
					String filename = item.getName();
					System.out.println(filename);
					
					response.setStatus(HttpServletResponse.SC_CREATED);
					response.getWriter().print("File uploaded successfully.");
					response.flushBuffer();
					
				}
				*/
			} catch (FileUploadException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occured while uploading the file: " + e.getMessage());
			}
		} else
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "This request content type is not supported.");
	}
}
