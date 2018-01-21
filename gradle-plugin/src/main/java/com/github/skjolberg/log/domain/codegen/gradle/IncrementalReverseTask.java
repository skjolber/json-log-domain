package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class IncrementalReverseTask extends DefaultTask {

	ConfigurableFileCollection definitions;
    
	public void setDefinitions(ConfigurableFileCollection definitions) {
		this.definitions.setFrom(definitions);
	}

    @InputFiles
    public ConfigurableFileCollection getDefinitions() {
		return definitions;
	}
    
    @TaskAction
    public void execute(IncrementalTaskInputs inputs) {
    	System.out.println(inputs.isIncremental() ? "CHANGED inputs considered out of date" : "ALL inputs considered out of date");
    }
}
