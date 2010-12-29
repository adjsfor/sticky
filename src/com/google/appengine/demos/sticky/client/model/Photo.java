package com.google.appengine.demos.sticky.client.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Photo implements Serializable {

	private String key;
	
	private byte[] data;
	
	public Photo() {}
	
	public String getKey() {
		return key;
	}
	
	public byte[] getBytes(){
		return this.data;
	}
}
