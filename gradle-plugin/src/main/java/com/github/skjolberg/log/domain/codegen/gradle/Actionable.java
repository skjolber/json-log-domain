package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;

import io.grpc.Internal;

public class Actionable {

	private boolean action = false;
	final Property<Boolean> generate;
	final Property<File> outputDirectory;

	public Actionable(Property<Boolean> generate, Property<File> outputDirectory) {
		this.generate = generate;
    	this.generate.set(true);
    	this.outputDirectory = outputDirectory;
	}
	
	public void setAction(boolean action) {
		this.action = action;
	}
	
	@Internal
	public boolean isAction() {
		return action;
	}

	@Input
	public boolean getGenerate() {
		return generate.get();
	}
	
	public void setGenerate(boolean enabled) {
		this.generate.set(enabled);
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory.set(outputDirectory);
	}
	
	@OutputDirectory
	public File getOutputDirectory() {
		return outputDirectory.getOrNull();
	}

}
