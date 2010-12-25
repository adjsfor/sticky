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
    
    private final FlexTable commentTable = new FlexTable();
    
    private final TextBox commentBox = new TextBox();
    
    private List<Comment> comments = new ArrayList<Comment>();
    
    public CommentView(Model model, Note note) {
        this.model = model;
        this.note = note;
        
        this.getElement().setClassName("comment");
        
        this.commentTable.setTitle("Comments");
        this.commentTable.getElement().setClassName("comment-table");
        this.commentBox.getElement().setClassName("comment-box");
        this.commentBox.addKeyPressHandler(this);
        
        this.add(commentTable);
        this.add(commentBox);
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
            final String text = this.commentBox.getText();
            this.commentBox.setText("");
            
            final Comment comment = new Comment(this.note.getKey(), this.model.getCurrentAuthor().getName(), text);
            this.addComment(comment);
            this.comments.add(comment);
            this.model.updateNoteContent(this.note, this.comments.toArray(new Comment[0]));
        }
    }
    
    @Override
    public void onCommentAdded(Comment comment) {
        if (this.note.getKey().equals(comment.getNote())) {
            this.addComment(comment);
        }
    }
    
    private void addComment(Comment comment) {
        int count = this.commentTable.getRowCount();
        this.commentTable.setText(count, 0, Author.getShortName(comment.getAuthor()) + ": ");
        this.commentTable.setText(count, 1, comment.getText());
    }
    
//    public void onUpdate(Note note) {
//        this.commentTable.removeAllRows();
//        if (note.getComments() != null) {
//            for (Comment c : note.getComments()) {
//                this.addComment(c);
//            }
//        }
//    }

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
    
}
