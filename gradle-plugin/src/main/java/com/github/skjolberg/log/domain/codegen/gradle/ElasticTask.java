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

public class ElasticTask extends DefaultTask {

	public static final String DEFAULT_DESTINATION_RESOURCE_DIR = "/generatedSources/src/main/resources";
			
	protected ConfigurableFileCollection definitions;
	
	protected Elastic elastic;

    @TaskAction
    public void generate() throws IOException {
    	if(elastic.isAction() && elastic.getGenerate()) {
    		File destination = elastic.getOutputDirectory(new File(getProject().getBuildDir() + DEFAULT_DESTINATION_RESOURCE_DIR));

	    	System.out.println("Generating Elastic configuration to " + destination.getAbsolutePath() + "..");

	    	Set<File> files = definitions.getFiles();
	    	for(File file : files) {
	    		com.github.skjolber.log.domain.model.Domain result = DomainFactory.parse(Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8));

        		Path output = destination.toPath().resolve(result.getName() + ".mapping.json");
		    	
		    	if(!Files.exists(output.getParent())) Files.createDirectories(output.getParent());

    			ElasticGenerator.generate(result, output);
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
	public Elastic getElastic() {
		return elastic;
	}

	public void setElastic(Elastic elastic) {
		this.elastic = elastic;
	}
}
