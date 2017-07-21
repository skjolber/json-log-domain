package com.github.skjolber.log.domain.test.matcher;

import java.io.Serializable;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import com.github.skjolber.log.domain.test.LogbackJUnitRule;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class MessageMatcher extends BaseMatcher<String> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static <T> Matcher<T> message(Class<?> cls, Matcher<String> matcher, Level level) {
		return (Matcher<T>) new MessageMatcher(cls.getName(), matcher, level);
	}

	public static <T> Matcher<T> message(Class<?> cls, Matcher<String> matcher) {
		return (Matcher<T>) new MessageMatcher(cls.getName(), matcher, null);
	}

	public static <T> Matcher<T> message(Matcher<String> matcher) {
		return (Matcher<T>) new MessageMatcher(null, matcher, null);
	}

	public static <T> Matcher<T> message(String string) {
		return (Matcher<T>) new MessageMatcher(null, new IsEqual<String>(string), null);
	}

	protected final String loggerName;
    protected final Matcher<String> matcher;
    protected final Level level;

    public MessageMatcher(String loggerName, Matcher<String> matcher, Level level) {
    	this.loggerName = loggerName;
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
		
		if(matcher.matches(event.getMessage())) {
			return true;
		}
		return false;
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

    public void describeTo(Description description) {
    	StringBuilder builder = new StringBuilder();
    	builder.append("matcher(\"");
		builder.append(matcher.getClass().getSimpleName());
    	builder.append("\")");
		description.appendText(builder.toString());
    }
}
