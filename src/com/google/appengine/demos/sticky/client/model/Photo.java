package com.google.appengine.demos.sticky.client.model;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Photo implements Serializable {

	private String key;
	
	public Photo() {}
	
	public String getKey() {
		return key;
	}
	
	
}
