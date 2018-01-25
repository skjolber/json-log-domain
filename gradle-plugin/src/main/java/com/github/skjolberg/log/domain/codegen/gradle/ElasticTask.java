package com.github.skjolberg.log.domain.codegen.gradle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.api.tasks.incremental.InputFileDetails;

import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.ElasticGenerator;

public class ElasticTask extends FilesTask {

	public static final String DEFAULT_DESTINATION_DIR = "/generatedSources/src/main/resources";

	protected Elastic elastic;

	@TaskAction
	public void generate(IncrementalTaskInputs inputs) throws IOException {
		if(elastic.isAction()) {

			if(!elastic.getGenerate()) {
				deleteOutputFiles(elastic.getExtension(), elastic.getOutputDirectory());
			} else {
				if(!inputs.isIncremental()) {
					deleteOutputFiles(elastic.getExtension(), elastic.getOutputDirectory());
				}

				File destination = elastic.getOutputDirectory();
				if(!definitions.isEmpty()) {
					System.out.println("Generating Elastic configuration to " + destination.getAbsolutePath());
					if(!destination.exists()) {
						Files.createDirectories(destination.toPath());
					}
				}

				inputs.outOfDate(new Action<InputFileDetails>() {
					@Override
					public void execute(InputFileDetails details) {
						try {
							Path output = getOutputFile(destination, details.getFile(), elastic.getExtension());

							com.github.skjolber.log.domain.model.Domain result = DomainFactory.parse(Files.newBufferedReader(details.getFile().toPath(), StandardCharsets.UTF_8));

							ElasticGenerator.generate(result, output);
						} catch(Exception e) {
							throw new RuntimeException(e);
						}
					}

				});	    		


				inputs.removed(new Action<InputFileDetails>() {
					@Override
					public void execute(InputFileDetails details) {
						try {
							Path output = getOutputFile(destination, details.getFile(), elastic.getExtension());

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

	@Nested
	@Optional
	public Elastic getElastic() {
		return elastic;
	}

	public void setElastic(Elastic elastic) {
		this.elastic = elastic;
	}
}
