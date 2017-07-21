package com.github.skjolber.log.domain.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class LogbackJUnitRule extends org.junit.rules.ExternalResource {

	// https://github.com/dancerjohn/LibEx/blob/master/testlibex/src/main/java/org/libex/test/logging/log4j/Log4jCapturer.java
	public static LogbackJUnitRule newInstance(Class<?> ... classes) {
		LogbackJUnitRule rule = new LogbackJUnitRule();
		
		if(classes != null && classes.length > 0) {
			for (Class<?> cls : classes) {
				rule.add(cls);
			}
		} else {
			rule.add(Logger.ROOT_LOGGER_NAME);
		}
		
		return rule;
	}
	
	private static class Entry {
		private Logger logger;
		private ListAppender<ILoggingEvent> appender;
		
		public Entry(Logger logger, ListAppender<ILoggingEvent> appender) {
			this.logger = logger;
			this.appender = appender;
		}
	}
	
	private static Comparator<ILoggingEvent> comparator = new Comparator<ILoggingEvent>() {
		
		@Override
		public int compare(ILoggingEvent o1, ILoggingEvent o2) {
			return Long.compare(o1.getTimeStamp(), o2.getTimeStamp());
		}
	};

	private List<Entry> entries = new ArrayList<>();
	
	public LogbackJUnitRule add(Class<?> cls) {
		return add(cls.getName());
	}
	
	public LogbackJUnitRule add(String name) {
		ListAppender<ILoggingEvent> appender = new com.github.skjolber.log.domain.test.ListAppender();
		appender.setName(name);
		
		Logger logger = (Logger) LoggerFactory.getLogger(name);
		
		logger.addAppender(appender);
		
		entries.add(new Entry(logger, appender));
		
		appender.start();
		
		return this;
	}

	public List<ILoggingEvent> capture(Class<?> cls) {
		return capture(cls.getName());
	}

	public List<ILoggingEvent> capture(Class<?> cls, Level level) {
		return capture(cls.getName(), level);
	}

	public List<ILoggingEvent> capture() {
		if(entries.size() == 1) {
			return entries.get(0).appender.list;
		}
		List<ILoggingEvent> result = new ArrayList<ILoggingEvent>();
		for(Entry entry : entries) {
			result.addAll(entry.appender.list);
		}
		Collections.sort(result, comparator);
		return result;
	}

	public List<ILoggingEvent> capture(Level level) {
		if(entries.size() == 1) {
			return filterLevel(entries.get(0).appender.list, level);
		}
		List<ILoggingEvent> result = new ArrayList<ILoggingEvent>();
		for(Entry entry : entries) {
			result.addAll(filterLevel(entry.appender.list, level));
		}
		Collections.sort(result, comparator);
		return result;
	}	
	
	public List<ILoggingEvent> capture(String name, Level level) {
		return filterLevel(capture(name), level);
	}

	private  List<ILoggingEvent> filterLevel(List<ILoggingEvent> capture, Level level) {
		List<ILoggingEvent> result = new ArrayList<ILoggingEvent>(capture.size());
		for(int i = 0; i < capture.size(); i++) {
			if(capture.get(i).getLevel().isGreaterOrEqual(level)) {
				result.add(capture.get(i));
			}
		}
		return result;
	}
	
	public List<ILoggingEvent> capture(String name) {
		Logger logger = (Logger) LoggerFactory.getLogger(name);
		
		for(Entry entry : entries) {
			if(entry.logger == logger) {
				return entry.appender.list;
			}
		}
		
		throw new IllegalArgumentException("Unable to find logger for " + name);
	}
	
	@Override
	protected void after() {
		for(Entry entry : entries) {
			entry.logger.detachAppender(entry.appender);
			
			entry.appender.stop();
		}
		entries.clear();
	}
	
	@Override
	public String toString() {
		int count = 0;
		for(Entry entry : entries) {
			count += entry.appender.list.size();
		}
		
		return getClass().getSimpleName() + "[" + count + " captured statements]";
	}
}
