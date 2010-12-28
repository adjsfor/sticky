package com.google.appengine.demos.sticky.client;

import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
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

	private Note note;
    
    private Model model;
	
	public PhotoView(Model model, Note note) {
		this.model = model;
		this.note = note;
		
		final FormPanel form = new FormPanel();
		form.setAction("/uploadHandler");
		
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		
		form.setWidget(this);
		
		final FileUpload fileUpload = new FileUpload();
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
					} else
						Window.alert("Upload successful!");
			}
		});
		
		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				
				
			}
		});
		
		add(submitButton);
		
	}
	
}
