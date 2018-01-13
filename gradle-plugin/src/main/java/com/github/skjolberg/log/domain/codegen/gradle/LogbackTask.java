package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;

import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.ElasticGenerator;
import com.github.skjolber.log.domain.codegen.MarkdownGenerator;
import com.github.skjolber.log.domain.codegen.logstash.LogbackGenerator;
import com.github.skjolber.log.domain.codegen.stackdriver.StackDriverGenerator;

public class LogbackTask extends AbstractTask {
	
	protected Logback logback;

    @TaskAction
    public void generate() throws IOException {
    	if(logback.isAction() && logback.getGenerate()) {
    		File destination = logback.getOutputDirectory(new File(getProject().getBuildDir() + DEFAULT_DESTINATION_DIR));
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

    @Input
	@Optional
	public Logback getLogback() {
		return logback;
	}

	public void setLogback(Logback logback) {
		this.logback = logback;
	}

    
}
