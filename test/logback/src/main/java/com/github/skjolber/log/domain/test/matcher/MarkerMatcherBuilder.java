package com.github.skjolber.log.domain.test.matcher;

import com.github.skjolber.log.domain.utils.DomainMarker;

public class MarkerMatcherBuilder {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> LogbackJUnitRuleMatcher<T> contains(DomainMarker marker) {
		return new LogbackJUnitRuleMatcher(new ContainsMarkerMatcher<>(marker));
	}

}
