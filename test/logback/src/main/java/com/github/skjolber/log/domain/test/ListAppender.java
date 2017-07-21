package com.github.skjolber.log.domain.test;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ListAppender extends ch.qos.logback.core.read.ListAppender<ILoggingEvent> {

	@Override
	protected void append(ILoggingEvent e) {
		// capture MDC properties
		e.getMDCPropertyMap();
		super.append(e);
	}
	
	
}
