package com.github.skjolberg.log.domain.codegen.gradle.ext;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public class Markdown extends Actionable {

	final Property<File> outputDirectory;
	final Property<Boolean> logback;
	final Property<Boolean> stackDriver;
	final Property<Boolean> enabled;
	
    @javax.inject.Inject
    public Markdown(ObjectFactory objectFactory) {
    	outputDirectory = objectFactory.property(File.class);
    	logback = objectFactory.property(Boolean.class);
    	stackDriver = objectFactory.property(Boolean.class);
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
