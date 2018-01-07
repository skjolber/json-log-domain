package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;

import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.logstash.LogbackGenerator;
import com.github.skjolberg.log.domain.codegen.gradle.ext.Elastic;
import com.github.skjolberg.log.domain.codegen.gradle.ext.Logback;
import com.github.skjolberg.log.domain.codegen.gradle.ext.Markdown;
import com.github.skjolberg.log.domain.codegen.gradle.ext.StackDriver;

public class JsonLogDomainTask extends DefaultTask {

	public static final String DEFAULT_DESTINATION_DIR = "/generatedsources/src/main/java";
			
	protected ConfigurableFileCollection definitions;
	
	protected Markdown markdown;
	protected Logback logback;
	protected Elastic elastic;
	protected StackDriver stackDriver;

    @TaskAction
    public void generate() throws IOException {
    	System.out.println("Logging task generating");
    	
    	Set<File> files = definitions.getFiles();
    	for(File file : files) {
        	if(logback != null) {
        		
        		File destination = logback.getOutputDirectory().getOrElse(new File(getProject().getBuildDir() + DEFAULT_DESTINATION_DIR));
        		
        		Path javaOutput = destination.toPath();
		    	
		    	if(!Files.exists(javaOutput)) Files.createDirectories(javaOutput);

        		com.github.skjolber.log.domain.model.Domain result = DomainFactory.parse(Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8));

    			new LogbackGenerator().generate(result, javaOutput);
    			
    			System.out.println("Generating Logback Java output to " + javaOutput.toAbsolutePath());

    			SourceSetContainer sourceSets = (SourceSetContainer) getProject().getProperties().get("sourceSets");

    			sourceSets.getByName("main").getJava().getSrcDirs().add(javaOutput.toFile());
        	}
    	}
    }

	public ConfigurableFileCollection getDefinitions() {
		return definitions;
	}

	public void setDefinitions(ConfigurableFileCollection definitions) {
		this.definitions = definitions;
	}

    @Input
	public Markdown getMarkdown() {
		return markdown;
	}

	public void setMarkdown(Markdown markdown) {
		this.markdown = markdown;
	}

    @Input
	public Logback getLogback() {
		return logback;
	}

	public void setLogback(Logback logback) {
		this.logback = logback;
	}

    @Input
	public Elastic getElastic() {
		return elastic;
	}

	public void setElastic(Elastic elastic) {
		this.elastic = elastic;
	}

    @Input
	public StackDriver getStackDriver() {
		return stackDriver;
	}

	public void setStackDriver(StackDriver stackDriver) {
		this.stackDriver = stackDriver;
	}
    
}
