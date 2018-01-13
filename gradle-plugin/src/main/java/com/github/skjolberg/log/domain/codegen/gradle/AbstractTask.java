package com.github.skjolberg.log.domain.codegen.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;

public class AbstractTask extends DefaultTask {

	public static final String DEFAULT_DESTINATION_DIR = "/generatedSources/src/main/java";
			
	protected ConfigurableFileCollection definitions;

	public ConfigurableFileCollection getDefinitions() {
		return definitions;
	}

	public void setDefinitions(ConfigurableFileCollection definitions) {
		this.definitions = definitions;
	}
}
