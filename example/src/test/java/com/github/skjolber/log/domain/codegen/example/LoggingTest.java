package com.github.skjolber.log.domain.codegen.example;

import static com.example.language.LanguageMarkerBuilder.*;
import static com.example.network.NetworkMarkerBuilder.*;
import static com.example.global.GlobalMarkerBuilder.*;
import static com.example.language.LanguageTag.*;
import static com.example.global.GlobalTag.*;

import org.apache.log4j.MDC;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.language.LanguageLogger;

public class LoggingTest {

	private static Logger logger = LoggerFactory.getLogger(LoggingTest.class);

	@Test
	public void keys1() {
		MDC.put("uname", "magnus");
		logger.info(name("thomas").version(1.7).tags(JIT)
				.and(host("127.0.0.1").port(8080))
				.and(system("fedora").tags(LINUX)),
				"Hello world");
	}

	@Test
	public void keys2() {
		logger.info(system("fedora").tags(LINUX), "Hello world");
	}

	@Test
	public void keys3() {
		LanguageLogger l = new LanguageLogger(logger);

		l.info().name("thomas").version(1.7).tags(JIT)
				.and(host("127.0.0.1").port(8080))
				.and(system("fedora").tags(LINUX))
				.message("Hello world");
	}


}
