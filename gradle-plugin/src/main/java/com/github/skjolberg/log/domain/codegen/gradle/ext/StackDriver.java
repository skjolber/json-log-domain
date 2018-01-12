package com.github.skjolberg.log.domain.codegen.gradle.ext;

import java.io.File;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public class StackDriver extends Actionable {

	final Property<File> outputDirectory;
	final Property<Boolean> enabled;

    @javax.inject.Inject
    public StackDriver(ObjectFactory objectFactory) {
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
