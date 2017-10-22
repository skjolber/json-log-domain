package com.github.skjolber.log.domain.example.elastic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

/*
 * Combine mappings within the classpath to a single mapping (message type). 
 */

public class MappingsGenerator {

	public JSONObject createCompositeMapping(String compositeName) throws IOException {
		
		ResourcesScanner resourceScanner = new ResourcesScanner();
		
		Reflections reflections = new Reflections(resourceScanner);
		
		Set<String> resources = reflections.getResources(Pattern.compile(".*\\.mapping\\.json"));
		
		JSONObject properties = new JSONObject();
		
		for(String resource : resources) {
			InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
			if(is == null) {
				throw new RuntimeException("Unable to read resource " + resource);
			}
			String json = IOUtils.toString(is, StandardCharsets.UTF_8);
			
			JSONObject part = new JSONObject(json);
			
			for(String key : part.keySet()) {
				properties.put(key, part.get(key));
			}
		}
		
		return new JSONObject().put("mappings", new JSONObject().put(compositeName, new JSONObject().put("properties", properties)));
	}


}
