package com.google.appengine.demos.sticky.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class CommentPanel extends VerticalPanel implements KeyPressHandler, Model.DataObserver {
    
    private Note note;
    private final FlexTable commentTable = new FlexTable();
    private final TextBox commentBox = new TextBox();
    
    private ArrayList<Comment> comments = new ArrayList<Comment>();
    
    private Model model;
    
    public CommentPanel(Model model, Note note) {
        this.model = model;
        this.note = note;
        add(commentTable);
        add(commentBox);
        commentTable.setTitle("Comments");
        this.getElement().setClassName("comment");
        commentBox.getElement().setClassName("comment-box");
        commentTable.getElement().setClassName("comment-table");
        commentBox.addKeyPressHandler(this);
        if (note.getComments() != null) {
            for (Comment c : note.getComments()) {
                comments.add(c);
                addComment(c);
            }
        }
        model.addDataObserver(this);
    }
    
    @Override
    public void onKeyPress(KeyPressEvent event) {
        char keyCode = event.getCharCode();
        
        Logger logger = Logger.getLogger("CommentPanel");
        logger.log(Level.SEVERE, "KeyCode: " + keyCode);
        
        if (keyCode == KeyCodes.KEY_ENTER) {
            String text = commentBox.getText();
            commentBox.setText("");
            
            Comment comment = new Comment(note.getKey(), model.getCurrentAuthor().getName(), text);
            addComment(comment);
            comments.add(comment);
            model.updateNoteContent(note, comments.toArray(new Comment[0]));
        }
    }
    
    @Override
    public void onCommentAdded(Comment comment) {
        if (note.getKey().equals(comment.getNote())) {
            addComment(comment);
        }
    }
    
    private void addComment(Comment comment) {
        int count = commentTable.getRowCount();
        commentTable.setText(count, 0, comment.getAuthor());
        commentTable.setText(count, 1, comment.getText());
    }
    
    @Override
    public void onNoteCreated(Note note) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onSurfaceCreated(Surface surface) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onSurfaceNotesReceived(Note[] notes) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onSurfaceSelected(Surface nowSelected, Surface wasSelected) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onSurfacesReceived(Surface[] surfaces) {
        // TODO Auto-generated method stub
        
    }
    
    public void onUpdate(Note note) {
        commentTable.removeAllRows();
        if (note.getComments() != null) {
            for (Comment c : note.getComments()) {
                addComment(c);
            }
        }
    }
    
}
