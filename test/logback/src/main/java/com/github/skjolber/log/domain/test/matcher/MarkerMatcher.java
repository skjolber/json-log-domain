package com.github.skjolber.log.domain.test.matcher;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.slf4j.Marker;

import com.github.skjolber.log.domain.test.LogbackJUnitRule;
import com.github.skjolber.log.domain.utils.DeferredMdcMarker;
import com.github.skjolber.log.domain.utils.DomainMarker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class MarkerMatcher<T> extends BaseMatcher<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String loggerName;
	protected Matcher<DomainMarker> matcher;
	protected Level level;
	protected boolean initalized; // ensure builder pattern completes, if in use.
	protected DomainMarker marker;
	
	public MarkerMatcher() {
	}
	
	public MarkerMatcher(String loggerName, DomainMarker marker, Matcher<DomainMarker> matcher, Level level) {
		this.loggerName = loggerName;
		this.marker = marker;
		this.matcher = matcher;
		this.level = level;
		
		this.initalized = true;
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
		Marker marker = event.getMarker();
		if (marker == null) {
			return false;
		}
		
		if(matches(marker)) {
			return true;
		}
		
    	if(marker.hasReferences()) {
	    	Iterator<Marker> iterator = marker.iterator();
	    	while(iterator.hasNext()) {
	    		Marker next = iterator.next();
	    		if(matches(next)) {
	    			return true;
	    		}
	    	}
    	}

		return false;
	}

	public boolean matches(Marker marker) {
		if (marker instanceof DomainMarker) {
			if(this.marker.equalTo(marker)) {
				return true;
			}
		} else if(marker instanceof DeferredMdcMarker) {
			DeferredMdcMarker deferredMdcMarker = (DeferredMdcMarker)marker;
			
			for(DomainMarker mdcMarker : deferredMdcMarker.getMarkers()) {
				if(matches(mdcMarker)) {
					return true;
				}
			}
		}
		return false;
	}

	public void describeTo(Description description) {
		StringBuilder builder = new StringBuilder();
		builder.append("marker(\"");
		/*
		if (qualifier != null) {
			builder.append(qualifier);
			builder.append(".");
			builder.append(key);
		} else {
			builder.append(key);
		}
		*/
		builder.append("\"");
		description.appendText(builder.toString());
		builder.append(")");
	}

	public boolean matches(Object actual) {
		if(!initalized) {
			init();
			initalized = true;
		}
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

	public MarkerMatcher loggerName(String loggerName) {
		this.loggerName = loggerName;
		return this;
	}

	public MarkerMatcher marker(DomainMarker marker) {
		this.marker = marker;
		return this;
	}

	public MarkerMatcher matcher(Matcher matcher) {
		this.matcher = matcher;
		return this;
	}

	public MarkerMatcher level(Level level) {
		this.level = level;
		return this;
	}
	
	protected void init() {
		
	}

	public void setMarker(DomainMarker marker) {
		this.marker = marker;
	}
}
