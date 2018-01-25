package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;

// https://docs.gradle.org/4.4.1/userguide/custom_plugins.html#sec:getting_input_from_the_build
public class JsonLogDomainPluginExtension {

	final ConfigurableFileCollection definitions;

	final Markdown markdown;
	final Logback logback;
	final Elastic elastic;
	final StackDriver stackDriver;

	final Property<String> version;

	@javax.inject.Inject
	public JsonLogDomainPluginExtension(Project project) {
		// Create a Person instance
		markdown = project.getObjects().newInstance(Markdown.class);
		markdown.setOutputDirectory(new File(project.getBuildDir() + MarkdownTask.DEFAULT_DESTINATION_DIR));
		markdown.setExtension(".md");

		logback = project.getObjects().newInstance(Logback.class);
		logback.setOutputDirectory(new File(project.getBuildDir() + LogbackTask.DEFAULT_DESTINATION_DIR));

		elastic = project.getObjects().newInstance(Elastic.class);
		elastic.setOutputDirectory(new File(project.getBuildDir() + ElasticTask.DEFAULT_DESTINATION_DIR));
		elastic.setExtension(".mapping.json");

		stackDriver = project.getObjects().newInstance(StackDriver.class);
		stackDriver.setOutputDirectory(new File(project.getBuildDir() + StackDriverTask.DEFAULT_DESTINATION_DIR));

		definitions = project.files();
		version = project.getObjects().property(String.class);
	}

	void markdown(Action<? super Markdown> action) {
		action.execute(markdown);
		markdown.setAction(true);
	}

	void logback(Action<? super Logback> action) {
		action.execute(logback);
		logback.setAction(true);
	}

	void elastic(Action<? super Elastic> action) {
		action.execute(elastic);
		elastic.setAction(true);
	}

	void stackDriver(Action<? super StackDriver> action) {
		action.execute(stackDriver);
		stackDriver.setAction(true);
	}

	void definitions(Action<? super ConfigurableFileCollection> action) {
		action.execute(definitions);
	}

	public void setDefinitions(ConfigurableFileCollection definitions) {
		this.definitions.setFrom(definitions);
	}

	@InputFiles
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