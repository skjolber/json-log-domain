package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.TaskAction;

public class CleanTask extends DefaultTask {

	Property<File> outputDirectory;
	String defaultValue;
	
	@TaskAction
    public void clean() throws IOException {
		File destination = outputDirectory.getOrElse(new File(getProject().getBuildDir() + defaultValue));

    	System.out.println("Clean source " + destination.getAbsolutePath());
    }
}
