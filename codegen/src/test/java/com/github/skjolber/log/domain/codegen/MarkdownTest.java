package com.github.skjolber.log.domain.codegen;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileReader;

import org.junit.Test;

import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.MarkdownGenerator;
import com.github.skjolber.log.domain.model.Domain;

public class MarkdownTest {
	
	@Test
	public void generateSources() throws Exception {
	    Domain domain = DomainFactory.parse(new FileReader(new File("src/test/resources/yaml/network.yaml")));
	    
		String generate = MarkdownGenerator.generate(domain, true, true);
		
		assertThat(generate, containsString("hostname"));
		
		System.out.println(generate);
	}

}

