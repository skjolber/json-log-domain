package com.github.skjolber.log.domain.test.matcher;

import org.hamcrest.Matcher;

import com.github.skjolber.log.domain.utils.DomainTag;

import ch.qos.logback.classic.Level;

public class GenericMarkerMatcherBuilder {

	public static <T> GenericMarkerMatcher<T> cls(Class<?> cls) {
		GenericMarkerMatcher<T> builder = new GenericMarkerMatcher<T>();
		return builder.loggerName(cls.getName());
	}

	public static <T> GenericMarkerMatcher<T> loggerName(String name) {
		GenericMarkerMatcher<T> builder = new GenericMarkerMatcher<T>();
		return builder.loggerName(name);
	}

	public static <T> GenericMarkerMatcher<T> qualifier(String qualifier) {
		GenericMarkerMatcher<T> builder = new GenericMarkerMatcher<T>();
		return builder.qualifier(qualifier);
	}

	public static <T> GenericMarkerMatcher<T> key(String key) {
		GenericMarkerMatcher<T> builder = new GenericMarkerMatcher<T>();
		return builder.key(key);
	}

	public static <T> GenericMarkerMatcher<T> matcher(Matcher<T> matcher) {
		GenericMarkerMatcher<T> builder = new GenericMarkerMatcher<T>();
		return builder.matcher(matcher);
	}

	public static <T> GenericMarkerMatcher<T> level(Level level) {
		GenericMarkerMatcher<T> builder = new GenericMarkerMatcher<T>();
		return builder.level(level);
	}	

	public static <T> GenericMarkerMatcher<T> tags(DomainTag ... tags) {
		GenericMarkerMatcher<T> builder = new GenericMarkerMatcher<T>();
		return builder.tags(tags);
	}	

}
