package com.github.skjolber.log.domain.test.matcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.slf4j.Marker;

import com.github.skjolber.log.domain.test.LogbackJUnitRule;
import com.github.skjolber.log.domain.utils.DeferredMdcMarker;
import com.github.skjolber.log.domain.utils.DomainMarker;
import com.github.skjolber.log.domain.utils.DomainMdc;
import com.github.skjolber.log.domain.utils.DomainTag;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import net.logstash.logback.marker.LogstashMarker;

public class MarkerMatcher<T> extends BaseMatcher<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String loggerName;
	protected String qualifier;
	protected String key;
	protected Matcher<T> matcher;
	protected Level level;
	protected List<DomainTag> tags = new ArrayList<>(); 
	protected boolean initalized; // ensure builder pattern completes, if in use.

	public MarkerMatcher() {
	}
	
	public MarkerMatcher(String loggerName, String key, Matcher<T> matcher, Level level) {
		this(loggerName, null, key, matcher, level);
	}

	public MarkerMatcher(String loggerName, String qualifier, String key, Matcher<T> matcher, Level level) {
		this.loggerName = loggerName;
		this.qualifier = qualifier;
		this.key = key;
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
		if (marker instanceof DomainMarker) {
			DomainMarker domainMarker = (DomainMarker) marker;

			DomainMarker qualifiedMarker = find(qualifier, domainMarker);
			if (qualifiedMarker != null) {
				if (matcher.matches(qualifiedMarker.get(key))) {
					return true;
				}
			}
		}
		
		if(marker.hasReferences()) { // search for 
			Iterator<Marker> iterator = marker.iterator();
			while(iterator.hasNext()) {
				Marker next = iterator.next();
				
				if(next.getClass() == marker.getClass()) {
					throw new IllegalArgumentException("Multiple instances of " + next.getClass() + " within same log statement not supported");
				}
				
				if(next instanceof DeferredMdcMarker) {
					DeferredMdcMarker deferred = (DeferredMdcMarker)next;
					// see whether we can find the right value here
					
					List<DomainMdc> mdc = deferred.getMdc();
					for(int i = mdc.size() - 1; i >= 0; i--) {
						DomainMdc domainMdc = mdc.get(i);
							
						Object value = find(domainMdc);
						
						if(value != null) {
							return matcher.matches(value);
						}
					}
				}
			}
		}
		
		
		return false;
	}

	private Object find(DomainMdc domainMdc) {
		
		LogstashMarker delegate = domainMdc.getDelegate();
		
		DomainMarker qualifiedMarker = find(qualifier, delegate);
		if (qualifiedMarker != null) {
			return qualifiedMarker.get(key);
		}
		
		return null;
	}
	
    /**
     * Search chained {@linkplain DomainMarker}s for the correct qualifier.
     * 
     * @param qualifier
     * @return a marker matching the qualifier, or null if no such exists.
     */
    
    public static DomainMarker find(String qualifier, Marker marker) {
    	if(marker instanceof DomainMarker) {
    		DomainMarker domainMarker = (DomainMarker)marker;
	    	if(Objects.equals(qualifier, domainMarker.getQualifier())) {
	    		return domainMarker;
	    	}
    	}
    	
    	Iterator<Marker> iterator = marker.iterator();
    	while(iterator.hasNext()) {
    		Marker next = iterator.next();
    		if(next instanceof DomainMarker) {
    			DomainMarker domainMarker = (DomainMarker)next;
        		if(Objects.equals(qualifier, domainMarker.getQualifier())) {
            		return domainMarker;
            	}
    		}
    	}
    	
    	return null;
    }

	public void describeTo(Description description) {
		StringBuilder builder = new StringBuilder();
		builder.append("marker(\"");
		if (qualifier != null) {
			builder.append(qualifier);
			builder.append(".");
			builder.append(key);
		} else {
			builder.append(key);
		}
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

	public MarkerMatcher<T> loggerName(String loggerName) {
		this.loggerName = loggerName;
		return this;
	}

	public MarkerMatcher<T> qualifier(String qualifier) {
		this.qualifier = qualifier;
		return this;
	}

	public MarkerMatcher<T> tag(DomainTag tag) {
		this.tags.add(tag);
		return this;
	}

	public MarkerMatcher<T> tags(DomainTag ... tags) {
		for(DomainTag tag : tags) {
			this.tags.add(tag);
		}
		return this;
	}

	public MarkerMatcher<T> key(String key) {
		this.key = key;
		return this;
	}

	public MarkerMatcher<T> matcher(Matcher matcher) {
		this.matcher = matcher;
		return this;
	}

	public MarkerMatcher<T> value(T value) {
		this.matcher = new IsEqual(value);
		return this;
	}

	public MarkerMatcher<T> level(Level level) {
		this.level = level;
		return this;
	}
	
	protected void init() {
		if(!tags.isEmpty()) {
			if(key == null) {
				key = "tags";
			}
			if(matcher == null) {
				matcher = (Matcher<T>) new TagMatcher(tags);
			}
		}
		if(key == null) {
			throw new IllegalArgumentException("Expected key");
		}
	}

}
