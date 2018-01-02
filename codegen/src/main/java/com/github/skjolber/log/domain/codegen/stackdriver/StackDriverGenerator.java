package com.github.skjolber.log.domain.codegen.stackdriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.skjolber.log.domain.codegen.java.JavaGenerator;
import com.github.skjolber.log.domain.model.Domain;
import com.squareup.javapoet.JavaFile;

public class StackDriverGenerator extends JavaGenerator {
	
	public void generate(Domain ontology, Path outputDirectory) throws IOException {
		JavaFile tags = new TagGenerator().tag(ontology);
		JavaFile marker = PayloadGenerator.marker(ontology);
		JavaFile builder = PayloadGenerator.markerBuilder(ontology);
		JavaFile mdc = MdcGenerator.statement(ontology);

		Files.createDirectories(outputDirectory);

		for(JavaFile file : new JavaFile[]{tags, marker, builder, mdc}) {
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
