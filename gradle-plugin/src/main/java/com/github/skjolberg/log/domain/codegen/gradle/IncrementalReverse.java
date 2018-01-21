package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;

public class IncrementalReverse {

	final ConfigurableFileCollection definitions;

    @javax.inject.Inject
    public IncrementalReverse(Project project) {
    	definitions = project.files();
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

}
