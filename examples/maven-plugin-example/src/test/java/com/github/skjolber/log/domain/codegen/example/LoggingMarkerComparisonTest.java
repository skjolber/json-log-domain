package com.github.skjolber.log.domain.codegen.example;

import static com.example.global.GlobalMarkerBuilder.system;
import static com.example.global.GlobalTag.LINUX;
import static com.example.language.LanguageMarkerBuilder.name;
import static com.example.network.NetworkMarkerBuilder.host;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.language.LanguageTag;
import com.github.skjolber.log.domain.test.LogbackJUnitRule;

import net.logstash.logback.marker.Markers;

/**
 * 
 * Example for comparing Marker map approach to generated helpers
 * 
 */

public class LoggingMarkerComparisonTest {

	private static Logger logger = LoggerFactory.getLogger(LoggingMarkerComparisonTest.class);

	public LogbackJUnitRule rule = LogbackJUnitRule.newInstance(LoggingMarkerComparisonTest.class);

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void singleMarker() {
		Map<String, Object> map = new HashMap<>();
		map.put("system", "fedora");
		map.put("tags", "linux");
		logger.info(Markers.appendEntries(map), "Hello world");
	}
	
	@Test
	public void singleDomainMarker() {		
		logger.info(system("fedora").tags(LINUX), "Hello world");
	}
	
	@Test
	public void multiMarker() {
		Map<String, Object> map = new HashMap<>();
		map.put("system", "fedora");
		map.put("tags", "linux");
		
		Map<String, Object> language = new HashMap<>();
		language.put("name", "java");
		language.put("version", "1.7");
		language.put("tags", Arrays.asList("jit", "bytecode"));
		map.put("language", language);
		
		Map<String, Object> network = new HashMap<>();
		network.put("host", "127.0.0.1");
		network.put("port", "8080");
		map.put("network", network);
		logger.info(Markers.appendEntries(map), "Hello world");
	}
	
	@Test
	public void multiDomainMarker() {
		logger.info(name("java").version(1.7).tags(LanguageTag.JIT, LanguageTag.BYTECODE)
				.and(host("127.0.0.1").port(8080))
				.and(system("fedora").tags(LINUX)), "Hello world");
	}

}
