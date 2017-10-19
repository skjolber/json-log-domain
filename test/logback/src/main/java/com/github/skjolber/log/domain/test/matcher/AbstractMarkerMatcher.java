package com.github.skjolber.log.domain.test.matcher;

import java.io.Serializable;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.slf4j.Marker;

import com.github.skjolber.log.domain.utils.DomainMarker;

import ch.qos.logback.classic.spi.ILoggingEvent;

public abstract class AbstractMarkerMatcher<T> extends BaseMatcher<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected DomainMarker marker;
	
	public AbstractMarkerMatcher(DomainMarker marker) {
		this.marker = marker;
	}

	public boolean matches(ILoggingEvent event) {
		Marker marker = event.getMarker();
		if(marker != null) {
			return matches(marker);
		}
		
		return false;
	}

	public abstract boolean matches(Marker marker);

	public void describeTo(Description description) {
		StringBuilder builder = new StringBuilder();
		marker.writeToString(builder);
		description.appendText(builder.toString());
	}

	@SuppressWarnings("unchecked")
	public boolean matches(Object actual) {
		if (actual instanceof ILoggingEvent) {
			return matches((ILoggingEvent) actual);
		}

		return false;
	}

}
