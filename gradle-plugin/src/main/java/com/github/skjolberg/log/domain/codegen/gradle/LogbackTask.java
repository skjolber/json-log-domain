package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.logstash.LogbackGenerator;

public class LogbackTask extends AbstractTask {
	
	public static final String DEFAULT_DESTINATION_DIR = "/generatedSources/src/main/java";

	protected Logback logback;

    @TaskAction
    public void generate(IncrementalTaskInputs inputs) throws IOException {
    	if(logback.isAction() && logback.getGenerate()) {
    		File destination = logback.getOutputDirectory();
    		Path javaOutput = destination.toPath();
	    	if(!Files.exists(javaOutput)) Files.createDirectories(javaOutput);

			System.out.println("Generating Logback Java helpers to " + javaOutput.toAbsolutePath());

	    	Set<File> files = definitions.getFiles();
	    	for(File file : files) {
	    		com.github.skjolber.log.domain.model.Domain result = DomainFactory.parse(Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8));

    			new LogbackGenerator().generate(result, javaOutput);
    			
    			SourceSetContainer sourceSets = (SourceSetContainer) getProject().getProperties().get("sourceSets");

    			sourceSets.getByName("main").getJava().getSrcDirs().add(javaOutput.toFile());
        	}
        	


    	}
    }

    @Nested
    @Optional
	public Logback getLogback() {
		return logback;
	}

	public void setLogback(Logback logback) {
		this.logback = logback;
	}
    
}
