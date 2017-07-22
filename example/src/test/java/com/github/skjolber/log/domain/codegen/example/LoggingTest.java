package com.github.skjolber.log.domain.codegen.example;

import static com.example.global.GlobalMarkerBuilder.system;
import static com.example.global.GlobalTag.LINUX;
import static com.example.language.LanguageMarkerBuilder.name;
import static com.example.language.LanguageTag.JIT;
import static com.example.network.NetworkMarkerBuilder.host;
import static com.github.skjolber.log.domain.test.matcher.DomainMarkerMatcher.marker;
import static com.github.skjolber.log.domain.test.matcher.TagMatcher.*;
import static com.github.skjolber.log.domain.test.matcher.MessageMatcher.message;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.print.DocFlavor.BYTE_ARRAY;

import org.apache.log4j.MDC;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.language.LanguageLogger;
import com.example.language.LanguageTag;
import com.github.skjolber.log.domain.test.LogbackJUnitRule;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class LoggingTest {

	private static Logger logger = LoggerFactory.getLogger(LoggingTest.class);

	public LogbackJUnitRule rule = LogbackJUnitRule.newInstance(LoggingTest.class);

	@Test
	public void singleDomain() {
		logger.info(system("fedora").tags(LINUX), "Hello world");
		
		assertThat(rule, marker("system", is("fedora")));
		
		// single tag from global domain
		assertThat(rule, marker("tags", tags(LINUX)));
	}
	
	@Test
	public void multipleDomains1() {
		MDC.put("uname", "magnus");
		logger.info(name("java").version(1.7).tags(LanguageTag.JIT, LanguageTag.BYTECODE)
				.and(host("127.0.0.1").port(8080))
				.and(system("fedora").tags(LINUX)),
				"Hello world");
		
		List<ILoggingEvent> capture = rule.capture();
		assertThat(capture.size(), is(1));

		assertThat(rule, message("Hello world"));

		assertThat(rule, marker("language", "name", is("java")));
		assertThat(rule, marker("network", "host", is("127.0.0.1")));
		assertThat(rule, marker("system", is("fedora")));

		// multiple tags, from a domain
		assertThat(rule, marker("language", "tags", tags(LanguageTag.JIT, LanguageTag.BYTECODE)));

	}

	@Test
	public void multipleDomains2() {
		LanguageLogger l = new LanguageLogger(logger);

		l.info().name("java").version(1.7).tags(JIT)
				.and(host("127.0.0.1").port(8080))
				.and(system("fedora").tags(LINUX))
				.message("Hello world");
		
		assertThat(rule, message("Hello world"));

		assertThat(rule, marker("language", "name", is("java")));
		assertThat(rule, marker("network", "host", is("127.0.0.1")));
		assertThat(rule, marker("system", is("fedora")));
		
	}


}
