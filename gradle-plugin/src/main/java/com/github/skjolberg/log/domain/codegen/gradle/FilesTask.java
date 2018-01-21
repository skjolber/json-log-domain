package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.InputFiles;

public class FilesTask extends DefaultTask {

	protected ConfigurableFileCollection definitions;

    @InputFiles
	public ConfigurableFileCollection getDefinitions() {
		return definitions;
	}

	public void setDefinitions(ConfigurableFileCollection definitions) {
		this.definitions = definitions;
	}
	
	protected void deleteOutputFiles(final String ext, File destination) throws IOException {
		File[] files = destination.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(ext);
			}
		});
    	for(File file : files) {
    		file.delete();
    	}
		
	}

	protected Path getOutputFile(File destination, File file, String ext) {
		String name = file.getName();
		
		Path output = destination.toPath().resolve(name.substring(name.lastIndexOf('.') + 1) + ext);
		return output;
	}
	
	protected void deleteOutputDirectory(File destination) throws IOException {
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
