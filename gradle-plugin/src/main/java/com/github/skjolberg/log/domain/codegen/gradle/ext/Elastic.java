package com.github.skjolberg.log.domain.codegen.gradle.ext;

import java.io.File;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class Elastic {

	private Property<File> outputDirectory;

    @javax.inject.Inject
    public Elastic(ObjectFactory objectFactory) {
    	outputDirectory = objectFactory.property(File.class);
    }

	public Property<File> getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(Property<File> outputDirectory) {
		this.outputDirectory.set(outputDirectory);
	}
    
}
