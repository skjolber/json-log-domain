package com.github.skjolber.log.domain.stackdriver.utils;

import com.google.cloud.logging.DefaultLogEntry;

public class DomainLogEntry extends DefaultLogEntry {
	
	/**
	* Returns a builder for {@code LogEntry} objects given the entry payload.
	*/
	
	public static DomainLogEntryBuilder newBuilder() {
		return new DomainLogEntryBuilder();
	}

	/**
	* Returns a builder for {@code LogEntry} objects given the entry payload.
	*/
	
	public static DomainLogEntryBuilder newBuilder(DomainPayload payload) {
		return new DomainLogEntryBuilder().setPayload(payload);
	}
	
	public DomainLogEntry(Builder builder) {
		super(builder);
	}


}
