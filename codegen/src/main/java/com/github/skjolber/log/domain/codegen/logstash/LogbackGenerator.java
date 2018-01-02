package com.github.skjolber.log.domain.codegen.logstash;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.skjolber.log.domain.codegen.java.JavaGenerator;
import com.github.skjolber.log.domain.model.Domain;
import com.squareup.javapoet.JavaFile;

public class LogbackGenerator extends JavaGenerator {

	public void generate(Domain ontology, Path outputDirectory) throws IOException {
		JavaFile tags = new TagGenerator().tag(ontology);
		JavaFile marker = MarkerGenerator.marker(ontology);
		JavaFile builder = MarkerGenerator.markerBuilder(ontology);
		JavaFile statement = LoggerGenerator.statement(ontology);
		JavaFile logger = LoggerGenerator.logger(ontology);
		JavaFile mdc = MdcGenerator.statement(ontology);

		Files.createDirectories(outputDirectory);

		for(JavaFile file : new JavaFile[]{tags, marker, builder, statement, logger, mdc}) {
			if(file != null) {
				if(changed(file, outputDirectory)) {
					file.writeTo(outputDirectory);
				} else {
					// do not write this file
				}
			}
		}
	}
	
}
