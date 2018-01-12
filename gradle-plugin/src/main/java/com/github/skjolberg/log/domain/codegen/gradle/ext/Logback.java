package com.github.skjolberg.log.domain.codegen.gradle.ext;

import java.io.File;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public class Logback {

	final Property<File> outputDirectory;
	final Property<Boolean> enabled;

	private boolean action = false;

	public void setAction(boolean action) {
		System.out.println("Set action on " + this);
		this.action = action;
	}
	public boolean isAction() {
		return action;
	}
	
    @javax.inject.Inject
    public Logback(ObjectFactory objectFactory) {
    	outputDirectory = objectFactory.property(File.class);
    	enabled = objectFactory.property(Boolean.class);
    	enabled.set(true);
    }
    
	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory.set(outputDirectory);
	}
	
	public File getOutputDirectory() {
		return outputDirectory.getOrNull();
	}

	public File getOutputDirectory(File file) {
		return outputDirectory.getOrElse(file);
	}

	public boolean getEnabled() {
		return enabled.get();
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}


}
