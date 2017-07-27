package com.github.skjolber.log.domain.test.matcher;

import org.hamcrest.Matcher;

import com.github.skjolber.log.domain.utils.DomainTag;

import ch.qos.logback.classic.Level;

public class MarkerBuilder {

	public static <T> MarkerMatcher<T> cls(Class<?> cls) {
		MarkerMatcher<T> builder = new MarkerMatcher<T>();
		return builder.loggerName(cls.getName());
	}

	public static <T> MarkerMatcher<T> loggerName(String name) {
		MarkerMatcher<T> builder = new MarkerMatcher<T>();
		return builder.loggerName(name);
	}

	public static <T> MarkerMatcher<T> qualifier(String qualifier) {
		MarkerMatcher<T> builder = new MarkerMatcher<T>();
		return builder.qualifier(qualifier);
	}

	public static <T> MarkerMatcher<T> key(String key) {
		MarkerMatcher<T> builder = new MarkerMatcher<T>();
		return builder.key(key);
	}

	public static <T> MarkerMatcher<T> matcher(Matcher<T> matcher) {
		MarkerMatcher<T> builder = new MarkerMatcher<T>();
		return builder.matcher(matcher);
	}

	public static <T> MarkerMatcher<T> level(Level level) {
		MarkerMatcher<T> builder = new MarkerMatcher<T>();
		return builder.level(level);
	}	

	public static <T> MarkerMatcher<T> tags(DomainTag ... tags) {
		MarkerMatcher<T> builder = new MarkerMatcher<T>();
		return builder.tags(tags);
	}	

}
