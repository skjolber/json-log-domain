package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import com.github.skjolberg.log.domain.codegen.gradle.ext.Elastic;
import com.github.skjolberg.log.domain.codegen.gradle.ext.Logback;
import com.github.skjolberg.log.domain.codegen.gradle.ext.Markdown;
import com.github.skjolberg.log.domain.codegen.gradle.ext.StackDriver;

// https://docs.gradle.org/4.4.1/userguide/custom_plugins.html#sec:getting_input_from_the_build
public class LoggingPluginExtension {
	
	final ConfigurableFileCollection definitions;
	
	final Markdown markdown;
	final Logback logback;
	final Elastic elastic;
	final StackDriver stackDriver;
    
	final Property<String> version;

    @javax.inject.Inject
    public LoggingPluginExtension(Project project) {
        // Create a Person instance
    	markdown = project.getObjects().newInstance(Markdown.class);
    	logback = project.getObjects().newInstance(Logback.class);
    	elastic = project.getObjects().newInstance(Elastic.class);
    	stackDriver = project.getObjects().newInstance(StackDriver.class);
    	definitions = project.files();
    	version = project.getObjects().property(String.class);
    }

    void markdown(Action<? super Markdown> action) {
        action.execute(markdown);
    }
    
    void logback(Action<? super Logback> action) {
        action.execute(logback);
    }

    void elastic(Action<? super Elastic> action) {
        action.execute(elastic);
    }

    void stackDriver(Action<? super StackDriver> action) {
        action.execute(stackDriver);
    }
    
    void definitions(Action<? super ConfigurableFileCollection> action) {
        action.execute(definitions);
    }
    
    public void setDefinitions(ConfigurableFileCollection definitions) {
		this.definitions.setFrom(definitions);
	}

	public ConfigurableFileCollection getDefinitions() {
		return definitions;
	}

	public Markdown getMarkdown() {
		return markdown;
	}

	public Logback getLogback() {
		return logback;
	}

	public Elastic getElastic() {
		return elastic;
	}

	public StackDriver getStackDriver() {
		return stackDriver;
	}
	
	@Input
	public Property<String> getVersion() {
		return version;
	}

}