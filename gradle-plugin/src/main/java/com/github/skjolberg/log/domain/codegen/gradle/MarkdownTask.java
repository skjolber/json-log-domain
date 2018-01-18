package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.MarkdownGenerator;

public class MarkdownTask extends DefaultTask {

	public static final String DEFAULT_DESTINATION_RESOURCE_DIR = "/generatedSources/src/main/resources";
			
	protected ConfigurableFileCollection definitions;
	
	protected Markdown markdown;
	protected Logback logback;
	protected Elastic elastic;

    @TaskAction
    public void generate() throws IOException {
    	if(markdown.isAction() && markdown.getGenerate()) {
	    		
    		File destination = markdown.getOutputDirectory(new File(getProject().getBuildDir() + DEFAULT_DESTINATION_RESOURCE_DIR));

	    	System.out.println("Generating markdown to " + destination.getAbsolutePath());

	    	Set<File> files = definitions.getFiles();
	    	for(File file : files) {
	    		com.github.skjolber.log.domain.model.Domain result = DomainFactory.parse(Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8));
	
        		Path output = destination.toPath().resolve(result.getName() + ".md");
		    	
		    	if(!Files.exists(output.getParent())) Files.createDirectories(output.getParent());

		    	boolean logbackCodeGenerated = markdown.getGenerate();
		    	boolean stackDriverCodeGenerated = markdown.getGenerate();
		    	
    			MarkdownGenerator.generate(result, output, logbackCodeGenerated, stackDriverCodeGenerated);
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
	@Optional
	public Markdown getMarkdown() {
		return markdown;
	}

	public void setMarkdown(Markdown markdown) {
		this.markdown = markdown;
	}

    @Input
	@Optional
	public Logback getLogback() {
		return logback;
	}

	public void setLogback(Logback logback) {
		this.logback = logback;
	}

    @Input
	@Optional
	public Elastic getElastic() {
		return elastic;
	}

	public void setElastic(Elastic elastic) {
		this.elastic = elastic;
	}


    
}
