package com.github.skjolber.log.domain.test.matcher;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

public class GenericMarkerMatcher<T> extends BaseMatcher<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String loggerName;
	protected String qualifier;
	protected String key;
	protected Matcher<T> matcher;
	protected Level level;
	protected List<DomainTag> tags = new ArrayList<>(); 
	protected boolean initalized; // ensure builder pattern completes, if in use.

	public GenericMarkerMatcher() {
	}
	
	public GenericMarkerMatcher(String loggerName, String key, Matcher<T> matcher, Level level) {
		this(loggerName, null, key, matcher, level);
	}

	public GenericMarkerMatcher(String loggerName, String qualifier, String key, Matcher<T> matcher, Level level) {
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
		
		if(key != null) {
			if(qualifier != null) {
				DomainMarker qualifiedMarker = find(qualifier, marker);
				if (qualifiedMarker != null) {
					Object value = getter(qualifiedMarker, key);
					
					return matcher.matches(value);
				}
			} else {
				
				if(matchMarker(marker)) {
					return true;
				}
				if(marker.hasReferences()) {
					Iterator<Marker> iterator = marker.iterator();
					while(iterator.hasNext()) {
						if(matchMarker(iterator.next())) {
							return true;
						}
					}
				}
				
			}
		}
		
		return false;
	}

	private boolean matchMarker(Marker marker) {
		if (marker instanceof DomainMarker) {
			Object value = getter((DomainMarker) marker, key);
			if(value != null && matcher.matches(value)) {
				return true;
			}
			
		} else if(marker instanceof DeferredMdcMarker) {
			DeferredMdcMarker deferredMdcMarker = (DeferredMdcMarker)marker;
			
			for(DomainMarker mdcMarker : deferredMdcMarker.getMarkers()) {
				Object value = getter((DomainMarker) mdcMarker, key);
				if(value != null && matcher.matches(value)) {
					return true;
				}
			}
		}
		return false;
	}

	private Object getterLocal(DomainMarker marker, String name) {
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(marker.getClass()).getPropertyDescriptors()) {
				if (name.equals(pd.getName())) {
					return pd.getReadMethod().invoke(marker);
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Object getter(DomainMarker marker, String name) {
		DomainMarker current = marker;
		do {
			Object value = getterLocal(current, name);
			if(value != null) {
				return value;
			}
			current = current.getParent();
		} while(current != null);
		
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
    	} else if(marker instanceof DeferredMdcMarker) {
			DeferredMdcMarker deferredMdcMarker = (DeferredMdcMarker)marker;
			
			for (DomainMarker domainMarker : deferredMdcMarker.getMarkers()) {
				DomainMarker found = find(qualifier, domainMarker);
				if(found != null) {
					return found;
				}
			}
    	}
    	
    	if(marker.hasReferences()) {
	    	Iterator<Marker> iterator = marker.iterator();
	    	while(iterator.hasNext()) {
	    		Marker next = iterator.next();
	    		if(next instanceof DomainMarker) {
	    			DomainMarker domainMarker = find(qualifier, (DomainMarker)next);
	        		if(domainMarker != null) {
	            		return domainMarker;
	            	}
	        	} else if(next instanceof DeferredMdcMarker) {
	    			DeferredMdcMarker deferredMdcMarker = (DeferredMdcMarker)next;
	    			
	    			for (DomainMarker domainMarker : deferredMdcMarker.getMarkers()) {
	    				DomainMarker found = find(qualifier, domainMarker);
	    				if(found != null) {
	    					return found;
	    				}
	    			}
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

	public GenericMarkerMatcher<T> loggerName(String loggerName) {
		this.loggerName = loggerName;
		return this;
	}

	public GenericMarkerMatcher<T> qualifier(String qualifier) {
		this.qualifier = qualifier;
		return this;
	}

	public GenericMarkerMatcher<T> tag(DomainTag tag) {
		this.tags.add(tag);
		return this;
	}

	public GenericMarkerMatcher<T> tags(DomainTag ... tags) {
		for(DomainTag tag : tags) {
			this.tags.add(tag);
		}
		this.key = "tags";
		
		return this;
	}

	public GenericMarkerMatcher<T> key(String key) {
		this.key = key;
		return this;
	}

	public GenericMarkerMatcher<T> matcher(Matcher matcher) {
		this.matcher = matcher;
		return this;
	}

	public GenericMarkerMatcher<T> value(T value) {
		this.matcher = new IsEqual(value);
		return this;
	}

	public GenericMarkerMatcher<T> level(Level level) {
		this.level = level;
		return this;
	}
	
	protected void init() {
		if(!tags.isEmpty()) {
			if(matcher == null) {
				matcher = (Matcher<T>) new TagMatcher(tags);
			}
		} else if(key == null) {
			throw new IllegalArgumentException("Expected key");
		}
	}

}
