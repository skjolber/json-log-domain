package com.github.skjolber.log.domain.codegen.example;

public class MyObject {

	private String myKey;
	private String myValue;
	
	public String getMyKey() {
		return myKey;
	}
	
	public String getMyValue() {
		return myValue;
	}
	
	public void setMyKey(String myKey) {
		this.myKey = myKey;
	}
	
	public void setMyValue(String myValue) {
		this.myValue = myValue;
	}

	public MyObject(String myKey, String myValue) {
		this.myKey = myKey;
		this.myValue = myValue;
	}
	
}
