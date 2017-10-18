package com.github.skjolber.log.domain.test.matcher;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import com.github.skjolber.log.domain.test.LogbackJUnitRule;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class MdcMatcher<T> extends BaseMatcher<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static <T> Matcher<T> mdc(Class<?> cls, String key, String value) {
		return mdc(cls, key, value, null);
	}
	
	public static <T> Matcher<T> mdc(String key, String value) {
		return mdc(key, new IsEqual<String>(value));
	}

	public static <T> Matcher<T> mdc(String key, String value, Level level) {
		return mdc(key, new IsEqual<String>(value), level);
	}

	public static <T> Matcher<T> mdc(Class<?> cls, String key, String value, Level level) {
		return new MdcMatcher<T>(cls.getName(), key, new IsEqual<String>(value), level);
	}

	public static <T> Matcher<T> mdc(String key, Matcher<String> matcher) {
		return new MdcMatcher<T>(null, key, matcher, null);
	}

	public static <T> Matcher<T> mdc(String key, Matcher<String> matcher, Level level) {
		return new MdcMatcher<T>(null, key, matcher, level);
	}
	
	protected final String loggerName;
    protected final String key;
    protected final Matcher<String> matcher;
    protected final Level level;
    
    public MdcMatcher(String loggerName, String key, Matcher<String> matcher, Level level) {
    	this.loggerName = loggerName;
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
		Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
		String value = mdcPropertyMap.get(key);
		if(value != null) {
			if(matcher.matches(value)) {
				return true;
			}
		}
		return false;
	}

    public void describeTo(Description description) {
        description.appendText("mdc(\"" + key + "\")");
    }
    
	@SuppressWarnings("unchecked")
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
