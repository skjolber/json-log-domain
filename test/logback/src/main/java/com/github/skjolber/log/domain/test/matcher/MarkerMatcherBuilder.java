package com.github.skjolber.log.domain.test.matcher;

import com.github.skjolber.log.domain.utils.DomainMarker;

public class MarkerMatcherBuilder {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> MarkerMatcher<T> matcher(DomainMarker marker) {
		MarkerMatcher matcher = new MarkerMatcher(marker);
		return matcher;
	}

}
