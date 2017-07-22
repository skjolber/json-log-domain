package com.github.skjolber.log.domain.test.matcher;

import java.io.Serializable;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.slf4j.Marker;

import com.github.skjolber.log.domain.test.LogbackJUnitRule;
import com.github.skjolber.log.domain.utils.DomainMarker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class DomainMarkerMatcher<T> extends BaseMatcher<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static <T> Matcher<T> marker(String qualifier, String key, String value, Level level) {
		return marker(qualifier, key, new IsEqual(value), level);
	}

	public static <T> Matcher<T> marker(String qualifier, String key, Matcher<?> matcher) {
		return new DomainMarkerMatcher(null, qualifier, key, matcher, null);
	}

	public static <T> Matcher<T> marker(String key, Matcher<?> matcher) {
		return new DomainMarkerMatcher(null, key, matcher, null);
	}

	public static <T> Matcher<T> marker(Class<?> cls, String key, Matcher<?> matcher) {
		return new DomainMarkerMatcher(cls.getName(), null, key, matcher, null);
	}

	public static <T> Matcher<T> marker(String qualifier, String key, Matcher<?> matcher, Level level) {
		return new DomainMarkerMatcher(null, qualifier, key, matcher, level);
	}

	public static <T> Matcher<T> marker(Class<?> cls, String qualifier, String key, String value, Level level) {
		return marker(cls, qualifier, key, new IsEqual(value), level);
	}

	public static <T> Matcher<T> marker(Class<?> cls, String qualifier, String key, Matcher<?> matcher) {
		return new DomainMarkerMatcher(cls.getName(), qualifier, key, matcher, null);
	}

	public static <T> Matcher<T> marker(Class<?> cls, String qualifier, String key, Matcher<?> matcher, Level level) {
		return new DomainMarkerMatcher(cls.getName(), qualifier, key, matcher, level);
	}
	
	protected final String loggerName;
	protected final String qualifier;
	protected final String key;
    protected final Matcher<T> matcher;
    protected final Level level;

    public DomainMarkerMatcher(String loggerName, String key, Matcher<T> matcher, Level level) {
    	this(loggerName, null, key, matcher, level);
    }

    public DomainMarkerMatcher(String loggerName, String qualifier, String key, Matcher<T> matcher, Level level) {
    	this.loggerName = loggerName;
    	this.qualifier = qualifier;
        this.key = key;
        this.matcher = matcher;
        this.level = level;
    }

    protected boolean matches(List<ILoggingEvent> appender) {
		for(ILoggingEvent event : appender) {
			if(matches(event)) {
				return true;
			}
		}
		return false;
	}

	protected boolean matches(ILoggingEvent event) {
		if(loggerName != null) {
			if(!event.getLoggerName().equals(loggerName)) {
				return false;
			}
		}
		if(level != null) {
			if(!event.getLevel().isGreaterOrEqual(level)) {
				return false;
			}
		}
		Marker marker = event.getMarker();
		if(marker == null) {
			return false;
		}
		if(marker instanceof DomainMarker) {
			DomainMarker domainMarker = (DomainMarker)marker;
			
			DomainMarker qualifiedMarker = domainMarker.find(qualifier);
			if(qualifiedMarker != null) {
				if(matcher.matches(qualifiedMarker.get(key))) {
					return true;
				}
			}
		}
		return false;
	}

    public void describeTo(Description description) {
    	StringBuilder builder = new StringBuilder();
    	builder.append("matcher(\"");
    	if(qualifier != null) {
    		builder.append(qualifier);
    		builder.append(".");
    		builder.append(key);
    	} else {
    		builder.append(key);
    	}
    	builder.append("\")");
		description.appendText(builder.toString());
    }
    
	public boolean matches(Object actual) {
		if(actual instanceof LogbackJUnitRule) {
			LogbackJUnitRule rule = (LogbackJUnitRule)actual;
    		if(matches(rule.capture())) {
    			return true;
    		}
		}
		
    	if(actual instanceof ListAppender) {
    		ListAppender<ILoggingEvent> appender = (ListAppender<ILoggingEvent>)actual;
    		
    		if(matches(appender.list)) {
    			return true;
    		}
    	}
    	
    	if(actual instanceof List) {
    		return matches((List<ILoggingEvent>)actual);
    	}
    	
    	if(actual instanceof ILoggingEvent) {
    		return matches((ILoggingEvent)actual);
    	}
    	
    	return false;
    }
}
