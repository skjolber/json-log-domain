package com.github.skjolber.log.domain.codegen;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Test;

import com.github.skjolber.log.domain.codegen.logstash.LogbackGenerator;
import com.github.skjolber.log.domain.codegen.stackdriver.StackDriverGenerator;
public class JavaTest {
	
	@Test
	public void generateLogbackSources() throws Exception {
	    Path directory = Paths.get("target/generated-sources/log/");

	    LogbackGenerator generator = new LogbackGenerator();
	    generator.generate(Paths.get("src/test/resources/yaml/agresso.yaml"), directory);
	    generator.generate(Paths.get("src/test/resources/yaml/global.yaml"), directory);
	    generator.generate(Paths.get("src/test/resources/yaml/network.yaml"), directory);
		
		// verify that the generated classes can be loaded
		for(String domain : new String[]{"agresso", "global", "network"}) {
			File network = new File(String.format("target/generated-sources/log/com/example/%s/", domain));
			File[] results = network.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".java");
				}
			});

			List<String> files = new ArrayList<>();
			for(File result : results) {
				files.add(result.getAbsolutePath());
			}

		    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		    int status = compiler.run(null, null, null, files.toArray(new String[files.size()]));
		    if(status != 0) {
		    	throw new IllegalArgumentException();
		    }			
		}
	}
	
	@Test
	public void generateStackDriverSources() throws Exception {
	    Path directory = Paths.get("target/generated-sources/log/");

	    StackDriverGenerator generator = new StackDriverGenerator();
	    generator.generate(Paths.get("src/test/resources/yaml/agresso.yaml"), directory);
	    generator.generate(Paths.get("src/test/resources/yaml/global.yaml"), directory);
	    generator.generate(Paths.get("src/test/resources/yaml/network.yaml"), directory);
		
		// verify that the generated classes can be loaded
		for(String domain : new String[]{"agresso", "global", "network"}) {
			File network = new File(String.format("target/generated-sources/log/com/example/%s/", domain));
			File[] results = network.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".java");
				}
			});

			List<String> files = new ArrayList<>();
			for(File result : results) {
				files.add(result.getAbsolutePath());
			}

		    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		    int status = compiler.run(null, null, null, files.toArray(new String[files.size()]));
		    if(status != 0) {
		    	throw new IllegalArgumentException();
		    }			
		}
	}

}
