package com.github.skjolber.log.domain.test;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.DeferredProcessingAware;

public class ListAppender extends ch.qos.logback.core.read.ListAppender<ILoggingEvent> {

	@Override
	protected void append(ILoggingEvent e) {
		// capture MDC properties
		if(e instanceof DeferredProcessingAware) {
			DeferredProcessingAware deferredProcessingAware = (DeferredProcessingAware)e;
			e.prepareForDeferredProcessing();
		}
		
		super.append(e);
	}
	
}
