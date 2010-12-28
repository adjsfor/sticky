package com.google.appengine.demos.sticky.client;

import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PhotoView extends VerticalPanel {

	private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "fileupload";
	
	private Note note;
    
    private Model model;
	
	public PhotoView(Model model, Note note) {
		this.model = model;
		this.note = note;
		
		// Add upload form only to owner notes.
		if(note.getAuthorName().equals("You")) {
		
			final FormPanel form = new FormPanel();
			form.setAction(UPLOAD_ACTION_URL);
			
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			
			form.setWidget(this);
			
			final FileUpload fileUpload = new FileUpload();
			fileUpload.setName("uploadFormElement");
			add(fileUpload);
			
			Button submitButton = new Button("Submit");
			submitButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					form.submit();
				}
			});
			
			form.addSubmitHandler(new SubmitHandler() {
				
				@Override
				public void onSubmit(SubmitEvent event) {
						String filename = fileUpload.getFilename();
						// TODO: Allow only img files
						if(filename.length() == 0) {
							Window.alert("No File is selected.");
							event.cancel();
						}
				}
			});
			
			form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
				
				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) {
					Window.alert("test");
					Window.alert(event.getResults());					
					Window.alert("test2");
				}
			});
			
			add(submitButton);
		}
	}
	
}
