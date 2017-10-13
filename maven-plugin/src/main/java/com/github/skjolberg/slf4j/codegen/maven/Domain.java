package com.github.skjolberg.slf4j.codegen.maven;

import org.apache.maven.plugins.annotations.Parameter;

public class Domain {

	@Parameter(property = "path")
	private String path;
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "Domain [path=" + path + "]";
	}
	
	
}
