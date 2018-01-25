package com.github.skjolberg.log.domain.codegen.gradle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PluginProjectTest {

	@Rule 
	public TemporaryFolder testProjectDir = new TemporaryFolder();

	private File buildFile;

	@Before
	public void setup() throws IOException {
		buildFile = testProjectDir.newFile("build.gradle");

		Writer writer = new OutputStreamWriter(new FileOutputStream(buildFile), StandardCharsets.UTF_8);
		try {
			writer.write("plugins { id 'java' \n id 'com.github.skjolber.json-log-domain'\n }\n");
		} finally {
			writer.close();
		}

		File resources = new File(buildFile.getParentFile() + "/src/main/resources/");

		FileUtils.copyDirectory(new File("./src/functTest/resources/"), resources);
	}

	@Test
	public void testBuildLogback() throws Exception {

		Writer writer = new OutputStreamWriter(new FileOutputStream(buildFile, true), StandardCharsets.UTF_8);
		try {
			writer.write("jsonLogDomain { definitions = files('src/main/resources/network.yaml') \n logback {} }\n");
		} finally {
			writer.close();
		}

		// https://gradle.github.io/gradle-script-kotlin-docs/userguide/custom_plugins.html
		BuildResult result = GradleRunner.create()
				.withProjectDir(testProjectDir.getRoot())
				.withArguments("generateLogbackJavaHelpers")
				.withPluginClasspath()
				.build();

		assertTrue(result.getOutput().contains("Generating"));
		assertEquals(result.task(":generateLogbackJavaHelpers").getOutcome(), TaskOutcome.SUCCESS);
	}

	@Test
	public void testBuildMarkdown() throws Exception {

		Writer writer = new OutputStreamWriter(new FileOutputStream(buildFile, true), StandardCharsets.UTF_8);
		try {
			writer.write("jsonLogDomain { definitions = files('src/main/resources/network.yaml') \n markdown {} }\n");
		} finally {
			writer.close();
		}

		// https://gradle.github.io/gradle-script-kotlin-docs/userguide/custom_plugins.html
		BuildResult result = GradleRunner.create()
				.withProjectDir(testProjectDir.getRoot())
				.withArguments("generateMarkdownDocumentation")
				.withPluginClasspath()
				.build();

		assertTrue(result.getOutput().contains("Generating"));
		assertEquals(result.task(":generateMarkdownDocumentation").getOutcome(), TaskOutcome.SUCCESS);
	}

	@Test
	public void testBuildStackdriver() throws Exception {

		Writer writer = new OutputStreamWriter(new FileOutputStream(buildFile, true), StandardCharsets.UTF_8);
		try {
			writer.write("jsonLogDomain { definitions = files('src/main/resources/network.yaml') \n stackDriver{} }\n");
		} finally {
			writer.close();
		}

		// https://gradle.github.io/gradle-script-kotlin-docs/userguide/custom_plugins.html
		BuildResult result = GradleRunner.create()
				.withProjectDir(testProjectDir.getRoot())
				.withArguments("generateStackDriverJavaHelpers")
				.withPluginClasspath()
				.build();

		assertTrue(result.getOutput().contains("Generating"));
		assertEquals(result.task(":generateStackDriverJavaHelpers").getOutcome(), TaskOutcome.SUCCESS);
	}

	@Test
	public void testBuildElastic() throws Exception {

		Writer writer = new OutputStreamWriter(new FileOutputStream(buildFile, true), StandardCharsets.UTF_8);
		try {
			writer.write("jsonLogDomain { definitions = files('src/main/resources/network.yaml') \n elastic {} }\n");
		} finally {
			writer.close();
		}

		// https://gradle.github.io/gradle-script-kotlin-docs/userguide/custom_plugins.html
		BuildResult result = GradleRunner.create()
				.withProjectDir(testProjectDir.getRoot())
				.withArguments("generateElasticConfiguration")
				.withPluginClasspath()
				.build();

		assertTrue(result.getOutput().contains("Generating"));
		assertEquals(result.task(":generateElasticConfiguration").getOutcome(), TaskOutcome.SUCCESS);
	}	
}
