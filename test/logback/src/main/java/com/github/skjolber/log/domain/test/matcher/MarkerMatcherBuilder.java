package com.github.skjolber.log.domain.test.matcher;

import com.github.skjolber.log.domain.utils.DomainMarker;

public class MarkerMatcherBuilder {

	/**
	 * Check that all markers contained within the argument is present in a single log statement.
	 * 
	 * @param marker the marker to match
	 * @param <T> any type
	 * @return newly created matcher
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> AbstractMatcher<T> contains(DomainMarker marker) {
		return new AbstractMatcher(new ContainsMarkerMatcher<>(marker));
	}

	/**
	 * 
	 * Check that all markers contained within the argument is present as MDC in a single log statement.
	 * 
	 * @param marker the marker to match
	 * @param <T> any type
	 * @return newly created matcher
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> AbstractMatcher<T> containsMdc(DomainMarker marker) {
		return new AbstractMatcher(new ContainsMdcMarkerMatcher<>(marker));
	}
}
