package com.github.skjolberg.slf4j.codegen.maven;

public class Java {

	private Boolean logback;
	private Boolean stackDriver;
	
	public Boolean getLogback() {
		return logback;
	}
	public void setLogback(Boolean logback) {
		this.logback = logback;
	}
	public Boolean getStackDriver() {
		return stackDriver;
	}
	public void setStackDriver(Boolean stackDriver) {
		this.stackDriver = stackDriver;
	}
	
	@Override
	public String toString() {
		return "Java [logback=" + logback + ", stackDriver=" + stackDriver + "]";
	}
	
	
}
