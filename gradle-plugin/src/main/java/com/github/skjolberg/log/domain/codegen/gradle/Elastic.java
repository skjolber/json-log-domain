package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public class Elastic extends Actionable {

	final Property<String> extension;

	@javax.inject.Inject
	public Elastic(ObjectFactory objectFactory) {
		super(objectFactory.property(Boolean.class), objectFactory.property(File.class));
		
		extension = objectFactory.property(String.class);
	}
	
	@Input
	public String getExtension() {
		return extension.get();
	}
	
	public void setExtension(String ext) {
		this.extension.set(ext);
	}



}
