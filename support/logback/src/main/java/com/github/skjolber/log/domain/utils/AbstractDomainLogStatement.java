  package com.github.skjolber.log.domain.utils;

import org.slf4j.Logger;

import ch.qos.logback.classic.Level;

public abstract class AbstractDomainLogStatement<T extends DomainMarker> {

	protected Logger slf4jLogger;
	protected int level;
	protected T marker;
	
	public AbstractDomainLogStatement(org.slf4j.Logger slf4jLogger, int level, T marker) {
		this.slf4jLogger = slf4jLogger;
		this.level = level;
		this.marker = marker;
	}

	public void message(String msg, Throwable t) {
		switch(level) {
		case Level.TRACE_INT: {
			slf4jLogger.trace(marker, msg, t);
			break;
		}
		case Level.DEBUG_INT: {
			slf4jLogger.debug(marker, msg, t);
			break;
		}
		case Level.INFO_INT: {
			slf4jLogger.info(marker, msg, t);
			break;
		}
		case Level.WARN_INT: {
			slf4jLogger.warn(marker, msg, t);
			break;
		}
		case Level.ERROR_INT: {
			slf4jLogger.error(marker, msg, t);
			break;
		}
		}
	}
	
	public void message(String format, Object... arguments) {
		switch(level) {
		case Level.TRACE_INT: {
			slf4jLogger.trace(marker, format, arguments);
			break;
		}
		case Level.DEBUG_INT: {
			slf4jLogger.debug(marker, format, arguments);
			break;
		}
		case Level.INFO_INT: {
			slf4jLogger.info(marker, format, arguments);
			break;
		}
		case Level.WARN_INT: {
			slf4jLogger.warn(marker, format, arguments);
			break;
		}
		case Level.ERROR_INT: {
			slf4jLogger.error(marker, format, arguments);
			break;
		}
		}
	}
	
	public <D extends DomainMarker> AbstractDomainLogStatement<T> and(D other) {
		marker.and(other);
		return this; 
	}
}