package com.github.skjolber.log.domain.test;

import com.github.skjolber.log.domain.utils.configuration.JsonMdcJsonProvider;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ListAppender extends ch.qos.logback.core.read.ListAppender<ILoggingEvent> {

	@Override
	protected void append(ILoggingEvent e) {
		// capture MDC properties
		JsonMdcJsonProvider.captureContext(e);
		
		super.append(e);
	}
	
}
