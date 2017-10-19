package com.github.skjolber.log.domain.codegen.example;

import static com.example.global.GlobalMarkerBuilder.system;
import static com.example.global.GlobalTag.LINUX;
import static com.example.language.LanguageMarkerBuilder.name;
import static com.example.language.LanguageTag.JIT;
import static com.example.network.NetworkMarkerBuilder.host;
import static com.github.skjolber.log.domain.test.matcher.GenericMarkerMatcherBuilder.key;
import static com.github.skjolber.log.domain.test.matcher.GenericMarkerMatcherBuilder.qualifier;
import static com.github.skjolber.log.domain.test.matcher.GenericMarkerMatcherBuilder.tags;
import static com.github.skjolber.log.domain.test.matcher.MdcMatcher.mdc;
import static com.github.skjolber.log.domain.test.matcher.MessageMatcher.message;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.MDC;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.global.GlobalTag;
import com.example.language.LanguageLogger;
import com.example.language.LanguageTag;
import com.github.skjolber.log.domain.test.LogbackJUnitRule;
import com.github.skjolber.log.domain.test.matcher.MarkerMatcherBuilder;
import com.github.skjolber.log.domain.utils.DomainMdc;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class LoggingTest {

	private static Logger logger = LoggerFactory.getLogger(LoggingTest.class);

	public LogbackJUnitRule rule = LogbackJUnitRule.newInstance(LoggingTest.class);

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void singleDomain() {
		logger.info(system("fedora").tags(LINUX), "Hello world");
		
		assertThat(rule, key("system").value("fedora"));
		
		// single tag from global domain
		assertThat(rule, tags(LINUX));
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

		assertThat(rule, qualifier("language").key("name").value("java"));
		assertThat(rule, qualifier("network").key("host").value("127.0.0.1"));
		assertThat(rule, key("system").value("fedora"));

		// multiple tags, from a domain
		assertThat(rule, qualifier("language").tags(LanguageTag.JIT, LanguageTag.BYTECODE));

		// MDC
		assertThat(rule, mdc("uname", "magnus"));

	}

	@Test
	public void multipleDomains2() {
		LanguageLogger l = new LanguageLogger(logger);

		l.info().name("java").version(1.7).tags(JIT)
				.and(host("127.0.0.1").port(8080))
				.and(system("fedora").tags(LINUX))
				.message("Hello world");
		
		assertThat(rule, message("Hello world"));

		assertThat(rule, qualifier("language").key("name").value("java"));
		assertThat(rule, qualifier("network").key("host").value("127.0.0.1"));
		assertThat(rule, key("system").value("fedora"));
	}

	@Test
	public void exceptionTag() {
		exception.expect(AssertionError.class);
		
		logger.info(system("fedora").tags(LINUX), "Hello world");

		assertThat(rule, tags(GlobalTag.WINDOWS));
	}

	@Test
	public void exceptionValue() {
		exception.expect(AssertionError.class);
		
		logger.info(system("fedora").tags(LINUX), "Hello world");

		assertThat(rule, key("system").value("ubuntu"));
	}

	@Test
	public void exceptionWrongLogger() {
		exception.expect(AssertionError.class);
		
		Logger logger = LoggerFactory.getLogger(GlobalTag.class);
		logger.info(system("fedora").tags(LINUX), "Hello world");

		assertThat(rule, key("system").value("fedora"));
	}

	@Test
	public void multipleDomainsMDC() throws IOException {
		Closeable mdc = DomainMdc.mdc(host("localhost"));
		try {
			logger.info(system("fedora").tags(LINUX), "Hello world");
			
			assertThat(rule, key("system").value("fedora"));
			assertThat(rule, qualifier("network").key("host").value("localhost"));

			assertThat(rule, is(MarkerMatcherBuilder.matcher(system("fedora").tags(LINUX))));

			// single tag from global domain
			assertThat(rule, tags(LINUX));
		} finally {
			mdc.close();
		}
	}
	
	
}
