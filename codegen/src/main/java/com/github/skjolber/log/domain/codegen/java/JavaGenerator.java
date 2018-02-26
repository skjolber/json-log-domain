package com.github.skjolber.log.domain.codegen.java;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;

import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.model.Domain;
import com.squareup.javapoet.JavaFile;

public abstract class JavaGenerator {

	public void generate(Path file, Path outputDirectory) throws IOException {
		Domain ontology = DomainFactory.parse(Files.newBufferedReader(file, StandardCharsets.UTF_8));

		generate(ontology, outputDirectory);
	}
	
	public abstract void generate(Domain ontology, Path outputDirectory) throws IOException;

	/**
	 * Do not overwrite files if there is no changes.
	 */

	/**
	 * Do not overwrite files if there is no changes.
	 * 
	 * @param file file to check
	 * @param outputDirectory output directory
	 * @return true if file does not exist or is not equal to the file parameter.
	 * @throws IOException if io exception occored.
	 */
	
	protected boolean changed(JavaFile file, Path outputDirectory) throws IOException {
		if (!file.packageName.isEmpty()) {
			for (String packageComponent : file.packageName.split("\\.")) {
				outputDirectory = outputDirectory.resolve(packageComponent);
			}
			if(Files.notExists(outputDirectory) || !Files.isDirectory(outputDirectory)) {
				return true;
			}
		}

		Path outputPath = outputDirectory.resolve(file.typeSpec.name + ".java");
		if(Files.notExists(outputPath) || !Files.isRegularFile(outputPath)) {
			return true;
		}

		String previous = IOUtils.toString(Files.newInputStream(outputPath), StandardCharsets.UTF_8);
		StringWriter writer = new StringWriter();
		file.writeTo(writer);
		return !previous.equals(writer.toString());
	}
}
