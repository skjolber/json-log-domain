package com.github.skjolber.log.domain.utils;

import ch.qos.logback.classic.Level;

/**
 * Wrapper for slf4j Logger that enables a builder pattern and JSON layout
 */
public abstract class AbstractDomainLogger<T extends AbstractDomainLogStatement> {

	protected org.slf4j.Logger slf4jLogger;

	public AbstractDomainLogger(org.slf4j.Logger slf4jLogger) {
		this.slf4jLogger = slf4jLogger;
	}

	public T trace() {
		return createLogStatement(Level.TRACE_INT);
	}

	public T debug() {
		return createLogStatement(Level.DEBUG_INT);
	}
	
	public T info() {
		return createLogStatement(Level.INFO_INT);
	}

	public T warn() {
		return createLogStatement(Level.WARN_INT);
	}
	
	public T error() {
		return createLogStatement(Level.ERROR_INT);
	}

	protected abstract T createLogStatement(int level);


}
