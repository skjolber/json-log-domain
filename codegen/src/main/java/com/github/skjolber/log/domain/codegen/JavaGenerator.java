package com.github.skjolber.log.domain.codegen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;

import com.squareup.javapoet.JavaFile;

public class JavaGenerator {

	public static void generate(File file, File outputDirectory) throws IOException {
		Domain ontology = DomainFactory.parse(new FileReader(file));

		generate(ontology, outputDirectory);
	}

	public static void generate(Domain ontology, File outputDirectory) throws IOException {
		JavaFile tags = TagGenerator.tag(ontology);
		JavaFile marker = MarkerGenerator.marker(ontology);
		JavaFile builder = MarkerGenerator.markerBuilder(ontology);
		JavaFile statement = LoggerGenerator.statement(ontology);
		JavaFile logger = LoggerGenerator.logger(ontology);

		if(!outputDirectory.exists()) {
			if(!outputDirectory.mkdirs()) {
				throw new IOException();
			}
		}

		for(JavaFile file : new JavaFile[]{tags, marker, builder, statement, logger}) {
			if(changed(file, outputDirectory.toPath())) {
				file.writeTo(outputDirectory);
			} else {
				// do not write this file
			}
		}
	}
	
	/**
	 * Do not overwrite files if there is no changes.
	 */

	private static boolean changed(JavaFile tags, Path outputDirectory) throws IOException {
		if (!tags.packageName.isEmpty()) {
			for (String packageComponent : tags.packageName.split("\\.")) {
				outputDirectory = outputDirectory.resolve(packageComponent);
			}
			if(Files.notExists(outputDirectory) || !Files.isDirectory(outputDirectory)) {
				return true;
			}
		}

		Path outputPath = outputDirectory.resolve(tags.typeSpec.name + ".java");
		if(Files.notExists(outputPath) || !Files.isRegularFile(outputPath)) {
			return true;
		}

		String previous = IOUtils.toString(Files.newInputStream(outputPath), StandardCharsets.UTF_8);
		StringWriter writer = new StringWriter();
		tags.writeTo(writer);
		return !previous.equals(writer.toString());
	}

}
