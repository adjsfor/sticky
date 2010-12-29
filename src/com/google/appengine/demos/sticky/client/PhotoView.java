package com.google.appengine.demos.sticky.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class PhotoView extends VerticalPanel {
    
    private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL() + "fileupload";
    
    private static final Logger LOG = Logger.getLogger(PhotoView.class.toString());
    
    private Note note;
    
    private Model model;
    
    private FormPanel form = new FormPanel();
    
    private FileUpload fu =  new FileUpload();
    
    public PhotoView(Model model, Note note) {
        this.model = model;
        this.note = note;
        
      
    }
    
    public Widget getFileUploaderWidget() {
        // Add upload form only to owner notes.
        if (note.getAuthorName().equals("You")) {
            this.form.setAction(UPLOAD_ACTION_URL);
            this.form.setEncoding(FormPanel.ENCODING_MULTIPART);
            this.form.setMethod(FormPanel.METHOD_POST);
            
            VerticalPanel holder = new VerticalPanel();
            
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
                    LOG.log(Level.SEVERE, "test");
                    LOG.log(Level.SEVERE, event.getResults());
                    LOG.log(Level.SEVERE, "test2");
                    Window.alert(event.getResults());
                    model.updateNoteImage(note, event.getResults());
                }
            });
            
        }
        
        return form;
    }
    
}
