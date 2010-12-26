package com.google.appengine.demos.sticky.client;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.demos.sticky.client.model.Author;
import com.google.appengine.demos.sticky.client.model.Comment;
import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.appengine.demos.sticky.client.model.Surface;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentView extends VerticalPanel implements KeyPressHandler, Model.DataObserver {
    
    private Note note;
    
    private Model model;
    
    private final FlexTable tblComments = new FlexTable();
    
    private final TextBox tfComment = new TextBox();
    
    private List<Comment> comments = new ArrayList<Comment>();
    
    public CommentView(Model model, Note note) {
        this.model = model;
        this.note = note;
        
        this.getElement().setClassName("comment");
        
        this.tblComments.setTitle("Comments");
        this.tblComments.getElement().setClassName("comment-table");
        this.tfComment.getElement().setClassName("comment-box");
        this.tfComment.addKeyPressHandler(this);
        
        this.add(tblComments);
        this.add(tfComment);
        if (note.getComments() != null) {
            for (Comment c : note.getComments()) {
                this.comments.add(c);
                this.addComment(c);
            }
        }
        this.model.addDataObserver(this);
    }
    
    @Override
    public void onKeyPress(KeyPressEvent event) {
        char keyCode = event.getCharCode();
        
        if (keyCode == KeyCodes.KEY_ENTER) {
            final String text = this.tfComment.getText();
            this.tfComment.setText("");
            
            final Comment comment = new Comment(this.note.getKey(), this.model.getCurrentAuthor().getName(), text);
            this.addComment(comment);
            this.comments.add(comment);
            this.model.updateNoteContent(this.note, this.comments);
        }
    }
    
    @Override
    public void onCommentAdded(Comment comment) {
        if (this.note.getKey().equals(comment.getNote())) {
            this.addComment(comment);
        }
    }
    
    @Override
    public void onNoteCreated(Note note) { }

    @Override
    public void onSurfaceCreated(Surface surface) { }

    @Override
    public void onSurfaceNotesReceived(Note[] notes) { }

    @Override
    public void onSurfaceSelected(Surface nowSelected, Surface wasSelected) { }

    @Override
    public void onSurfacesReceived(Surface[] surfaces) { }
    
    private void addComment(Comment comment) {
        int count = this.tblComments.getRowCount();
        this.tblComments.setText(count, 0, Author.getShortName(comment.getAuthor()) + ": ");
        this.tblComments.setText(count, 1, comment.getText());
    }
}
