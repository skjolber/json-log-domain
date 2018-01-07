package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class CleanTask extends DefaultTask {

    @TaskAction
    public void clean() throws IOException {
    	System.out.println("Clean source");
    }
}
