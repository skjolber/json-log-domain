package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public class Markdown extends Actionable {

	final Property<Boolean> logback;
	final Property<Boolean> stackDriver;
	final Property<String> extension;
	
    @javax.inject.Inject
    public Markdown(ObjectFactory objectFactory) {
    	super(objectFactory.property(Boolean.class), objectFactory.property(File.class));
    	logback = objectFactory.property(Boolean.class);
    	stackDriver = objectFactory.property(Boolean.class);
    	extension = objectFactory.property(String.class);
    }
	
	@Input
    public String getExtension() {
		return extension.get();
	}
	
	public void setExtension(String ext) {
		this.extension.set(ext);
	}
	
	@Input
    public boolean getStackDriver() {
		return stackDriver.get();
	}
	
	public void setStackDriver(boolean value) {
		this.stackDriver.set(value);
	}

	@Input
    public boolean getLogback() {
		return logback.get();
	}
	
	public void setLogback(boolean value) {
		this.logback.set(value);
	}

	
}
