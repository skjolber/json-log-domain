package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.model.ObjectFactory;

public class Elastic extends Actionable {

    @javax.inject.Inject
    public Elastic(ObjectFactory objectFactory) {
    	super(objectFactory.property(Boolean.class), objectFactory.property(File.class));
    }



}
