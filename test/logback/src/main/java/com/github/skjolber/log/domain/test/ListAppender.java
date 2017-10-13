package com.github.skjolber.log.domain.test;

import com.github.skjolber.log.domain.utils.JsonMdcJsonProvider;

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
		
		JsonMdcJsonProvider.captureDomainMdc(e);
		
		super.append(e);
	}
	
}
