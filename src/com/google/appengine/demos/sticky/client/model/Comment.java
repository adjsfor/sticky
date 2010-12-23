package com.google.appengine.demos.sticky.client.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Comment implements Serializable {
    
    private String author;
    
    private String text;
    
    private String key;
    
    private String note;
    
    private Date createdAt;
    
    public Comment(String note, String author, String text, Date createdAt) {
        this.note = note;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
}
