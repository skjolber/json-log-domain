package com.github.skjolber.log.domain.utils.configuration;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class DomainAsyncAppender extends AsyncAppender {

	@Override
	protected void preprocess(ILoggingEvent eventObject) {
		JsonMdcJsonProvider.captureContext(eventObject);
		super.preprocess(eventObject);
	}
	
}
