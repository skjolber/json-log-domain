package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Destroys;
import org.gradle.api.tasks.TaskAction;

public class CleanTask extends DefaultTask {

	Property<File> outputDirectory;
	String defaultValue;
	
	@TaskAction
    public void clean() throws IOException {
		File destination = outputDirectory.getOrElse(new File(getProject().getBuildDir() + defaultValue));

    	System.out.println("Clean source " + destination.getAbsolutePath());
    	
    	if(destination.exists()) {
	    	// recursive delete
	    	Files.walkFileTree(destination.toPath(), new SimpleFileVisitor<Path>() {
	    		 
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}
	 
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc)
						throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});    	
    	}
    }
	
	@Destroys
	public Property<File> getOutputDirectory() {
		return outputDirectory;
	}	
}
