package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;

import org.gradle.api.DefaultTask;

public class FilesTask extends DefaultTask {

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
}
