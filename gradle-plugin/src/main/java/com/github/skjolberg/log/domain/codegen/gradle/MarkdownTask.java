package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.Action;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.api.tasks.incremental.InputFileDetails;

import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.MarkdownGenerator;

public class MarkdownTask extends FilesTask {

	public static final String DEFAULT_DESTINATION_RESOURCE_DIR = "/generatedSources/src/main/resources";
			
	protected ConfigurableFileCollection definitions;
	
	protected Markdown markdown;
	protected Logback logback;
	protected StackDriver stackDriver;

    @TaskAction
    public void generate(IncrementalTaskInputs inputs) throws IOException {
    	if(markdown.isAction()) {
	    	if(!markdown.getGenerate()) {
	    		deleteOutputFiles(markdown.getExtension(), markdown.getOutputDirectory());
    		} else {
    			if(!inputs.isIncremental()) {
    	    		deleteOutputFiles(markdown.getExtension(), markdown.getOutputDirectory());
    			}

    	    	boolean logbackCodeGenerated = logback.getGenerate();
    	    	boolean stackDriverCodeGenerated = stackDriver.getGenerate();

	    		File destination = markdown.getOutputDirectory();
	    		if(!definitions.isEmpty()) {
	    			System.out.println("Generating markdown to " + destination.getAbsolutePath());
			    	if(!destination.exists()) {
			    		Files.createDirectories(destination.toPath());
		    		}
	    		}

	    		inputs.outOfDate(new Action<InputFileDetails>() {
					@Override
					public void execute(InputFileDetails details) {
						try {
				    		Path output = getOutputFile(destination, details.getFile(), markdown.getExtension());
					    	
				    		com.github.skjolber.log.domain.model.Domain result = DomainFactory.parse(Files.newBufferedReader(details.getFile().toPath(), StandardCharsets.UTF_8));
			    			MarkdownGenerator.generate(result, output, logbackCodeGenerated, stackDriverCodeGenerated);
						} catch(Exception e) {
							throw new RuntimeException(e);
						}
					}

				});
	    		
	    		inputs.removed(new Action<InputFileDetails>() {
					@Override
					public void execute(InputFileDetails details) {
						try {
				    		Path output = getOutputFile(destination, details.getFile(), markdown.getExtension());

				    		if(Files.exists(output)) {
				    			Files.delete(output);
				    		}
						} catch(Exception e) {
							throw new RuntimeException(e);
						}
					}
				});

    		}
    	}
   	}

    @InputFiles
	public ConfigurableFileCollection getDefinitions() {
		return definitions;
	}

	public void setDefinitions(ConfigurableFileCollection definitions) {
		this.definitions = definitions;
	}

	@Nested
	@Optional
	public Markdown getMarkdown() {
		return markdown;
	}

	public void setMarkdown(Markdown markdown) {
		this.markdown = markdown;
	}

	@Nested
	@Optional
	public Logback getLogback() {
		return logback;
	}

	public void setLogback(Logback logback) {
		this.logback = logback;
	}

	@Optional
	@Nested
	public void setStackDriver(StackDriver stackDriver) {
		this.stackDriver = stackDriver;
	}
	
	public StackDriver getStackDriver() {
		return stackDriver;
	}
    
}
