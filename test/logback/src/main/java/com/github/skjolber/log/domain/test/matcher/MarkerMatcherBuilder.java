package com.github.skjolber.log.domain.test.matcher;

import com.github.skjolber.log.domain.utils.DomainMarker;

public class MarkerMatcherBuilder {

	public static <T> MarkerMatcher<T>  matcher(DomainMarker marker) {
		MarkerMatcher<T> matcher = new MarkerMatcher<T>();
		matcher.setMarker(marker);
		return matcher;
	}

}
