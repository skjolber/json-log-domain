package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.model.ObjectFactory;

public class Logback extends Actionable {

	@javax.inject.Inject
	public Logback(ObjectFactory objectFactory) {
		super(objectFactory.property(Boolean.class), objectFactory.property(File.class));
	}

}
