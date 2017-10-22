package com.github.skjolber.log.domain.example.elastic;

import java.io.IOException;

import org.json.JSONObject;
import org.junit.Test;

/**
 * This example uses multiple mappings files locally, normally there would also be mappings available on the classpath which then would be included too.
 * 
 */

public class MappingsGeneratorTest {

	@Test
	public void testScanAndGenerate() throws IOException {
		MappingsGenerator generator = new MappingsGenerator();
		
		String messageType = "myMessageType";
		
		JSONObject mapping = generator.createCompositeMapping(messageType);
		
		System.out.println(mapping);
	}
}
