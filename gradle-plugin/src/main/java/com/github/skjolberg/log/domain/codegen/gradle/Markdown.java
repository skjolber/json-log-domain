package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class Markdown extends Actionable {

	final Property<Boolean> logback;
	final Property<Boolean> stackDriver;
	
    @javax.inject.Inject
    public Markdown(ObjectFactory objectFactory) {
    	super(objectFactory.property(Boolean.class), objectFactory.property(File.class));
    	logback = objectFactory.property(Boolean.class);
    	stackDriver = objectFactory.property(Boolean.class);
    }
	
}
