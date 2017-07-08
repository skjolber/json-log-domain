package com.github.skjolberg.slf4j.codegen.maven;

import java.io.File;

import org.apache.maven.plugins.annotations.Parameter;

public class Domain {

	@Parameter(property = "path")
	private File path;
	
	public File getPath() {
		return path;
	}
	
	public void setPath(File path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "Domain [path=" + path + "]";
	}
	
	
}
