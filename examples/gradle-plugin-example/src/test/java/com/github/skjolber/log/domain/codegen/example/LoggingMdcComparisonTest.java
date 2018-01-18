package com.github.skjolber.log.domain.codegen.example;

import static com.example.global.GlobalMarkerBuilder.system;
import static com.example.global.GlobalTag.LINUX;
import static com.example.language.LanguageMarkerBuilder.name;
import static com.example.network.NetworkMarkerBuilder.host;

import org.apache.log4j.MDC;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.language.LanguageTag;
import com.github.skjolber.log.domain.test.LogbackJUnitRule;
import com.github.skjolber.log.domain.utils.DomainMarker;
import com.github.skjolber.log.domain.utils.DomainMdc;

/**
 * 
 * Example for comparing MDC approach to generated helpers
 * 
 */

public class LoggingMdcComparisonTest {

	private static Logger logger = LoggerFactory.getLogger(LoggingMdcComparisonTest.class);

	public LogbackJUnitRule rule = LogbackJUnitRule.newInstance(LoggingMdcComparisonTest.class);

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void singleMDC() {
		MDC.put("system", "fedora");
		MDC.put("tags", "linux");
		try {
			logger.info("Hello world");
		} finally {
			MDC.remove("system");
			MDC.remove("tags");
		}
	}
	
	@Test
	public void multipleDomain() {		
		DomainMarker mdc = DomainMdc.mdc(system("fedora").tags(LINUX));
		try {
			logger.info("Hello world");
		} finally {
			mdc.close();
		}
	}
	
	@Test
	public void multipleDomainsMDC() {
		MDC.put("system", "fedora");
		MDC.put("tags", "linux");
		MDC.put("name", "java");
		MDC.put("version", "1.7");
		MDC.put("tags", "jit,bytecode");
		MDC.put("host", "127.0.0.1");
		MDC.put("port", "8080");
		try {
			logger.info("Hello world");
		} finally {
			MDC.remove("system");
			MDC.remove("tags");
			MDC.remove("name");
			MDC.remove("version");
			MDC.remove("tags");
			MDC.remove("host");
			MDC.remove("port");
		}
	}
	
	@Test
	public void multipleDomains1() {
		DomainMarker mdc = DomainMdc.mdc(name("java").version(1.7).tags(LanguageTag.JIT, LanguageTag.BYTECODE)
				.and(host("127.0.0.1").port(8080))
				.and(system("fedora").tags(LINUX)));
		try {
			logger.info("Hello world");
		} finally {
			mdc.close();
		}
	}

}
