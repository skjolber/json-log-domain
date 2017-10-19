package com.github.skjolber.log.domain.test.matcher;

import java.io.Serializable;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.github.skjolber.log.domain.test.LogbackJUnitRule;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class LogbackJUnitRuleMatcher<T> extends BaseMatcher<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String loggerName;
	protected Matcher<T> matcher;
	protected Level level;

	public LogbackJUnitRuleMatcher(Matcher<T> matcher) {
		this.matcher = matcher;
	}

	public LogbackJUnitRuleMatcher(String loggerName, Matcher<T> matcher, Level level) {
		this.loggerName = loggerName;
		this.matcher = matcher;
		this.level = level;
	}

	protected boolean matches(List<ILoggingEvent> appender) {
		for (ILoggingEvent event : appender) {
			if (matches(event)) {
				return true;
			}
		}
		return false;
	}

	protected boolean matches(ILoggingEvent event) {
		if (loggerName != null) {
			if (!event.getLoggerName().equals(loggerName)) {
				return false;
			}
		}
		if (level != null) {
			if (!event.getLevel().isGreaterOrEqual(level)) {
				return false;
			}
		}
		
		return matcher.matches(event);
	}

	public void describeTo(Description description) {
		matcher.describeTo(description);
	}

	@SuppressWarnings("unchecked")
	public boolean matches(Object actual) {
		if (actual instanceof LogbackJUnitRule) {
			LogbackJUnitRule rule = (LogbackJUnitRule) actual;
			if (matches(rule.capture())) {
				return true;
			}
		}

		if (actual instanceof ListAppender) {
			ListAppender<ILoggingEvent> appender = (ListAppender<ILoggingEvent>) actual;

			if (matches(appender.list)) {
				return true;
			}
		}

		if (actual instanceof List) {
			return matches((List<ILoggingEvent>) actual);
		}

		if (actual instanceof ILoggingEvent) {
			return matches((ILoggingEvent) actual);
		}

		return false;
	}

	public LogbackJUnitRuleMatcher<T>  loggerName(String loggerName) {
		this.loggerName = loggerName;
		return this;
	}

	public LogbackJUnitRuleMatcher<T> matcher(Matcher<T> matcher) {
		this.matcher = matcher;
		return this;
	}

	public LogbackJUnitRuleMatcher<T>  level(Level level) {
		this.level = level;
		return this;
	}

}
