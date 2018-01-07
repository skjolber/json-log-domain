package com.github.skjolberg.log.domain.codegen.gradle.ext;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public class Markdown {

	final Property<File> outputDirectory;
	final Property<Boolean> logback;
	final Property<Boolean> stackDriver;
	
    @javax.inject.Inject
    public Markdown(ObjectFactory objectFactory) {
    	outputDirectory = objectFactory.property(File.class);
    	logback = objectFactory.property(Boolean.class);
    	stackDriver = objectFactory.property(Boolean.class);
    }
    
	public Property<File> getOutputDirectory() {
		return outputDirectory;
	}
	public void setOutputDirectory(Property<File> outputDirectory) {
		this.outputDirectory.set(outputDirectory);
	}
    @Input
	public Property<Boolean> getLogback() {
		return logback;
	}
	public void setLogback(Property<Boolean> logback) {
		this.logback.set(logback);
	}
    @Input
	public Property<Boolean> getStackDriver() {
		return stackDriver;
	}
	public void setStackDriver(Property<Boolean> stackDriver) {
		this.stackDriver.set(stackDriver);
	}
	
	
	
}
