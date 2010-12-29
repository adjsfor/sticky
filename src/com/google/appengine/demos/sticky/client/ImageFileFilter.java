package com.google.appengine.demos.sticky.client;


public class ImageFileFilter{
    
    private static final String PNG = ".png";
    
    private static final String JPG = ".jpg";
    
    private static final String JPEG = ".jpeg";
    
    private static final String GIF = ".gif";
    
    private static final String[] FORMATS = {PNG, JPG, JPEG, GIF};
    
    public boolean accept(final String file) {
        for (String f : FORMATS) {
            if (file.toLowerCase().endsWith(f)) {
                return true;
            }
        }
        
        return false;
    }
    
}
