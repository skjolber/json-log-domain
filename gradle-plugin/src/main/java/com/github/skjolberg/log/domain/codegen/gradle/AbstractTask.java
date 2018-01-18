package com.github.skjolberg.log.domain.codegen.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;

public class AbstractTask extends DefaultTask {

	protected ConfigurableFileCollection definitions;

	public ConfigurableFileCollection getDefinitions() {
		return definitions;
	}

	public void setDefinitions(ConfigurableFileCollection definitions) {
		this.definitions = definitions;
	}
}
