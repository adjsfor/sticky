package com.google.appengine.demos.sticky.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Comment implements Serializable {
    
    private String author;
    
    private String text;
    
    private String key;
    
    private String note;
    
    @SuppressWarnings("unused")
    private Comment() {
        super();
    }
    
    public Comment(String note, String author, String text) {
        this.note = note;
        this.author = author;
        this.text = text;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
}
