package com.github.skjolber.log.domain.stackdriver.utils;

import com.google.cloud.logging.DefaultLogEntry;
import com.google.cloud.logging.Payload;

public class DomainLogEntry extends DefaultLogEntry {
	
	/**
	* Returns a builder for {@code LogEntry} objects given the entry payload.
	*/
	
	public static DomainLogEntryBuilder newBuilder(Payload<?> payload) {
		return new DomainLogEntryBuilder(payload);
	}

	/**
	* Returns a builder for {@code LogEntry} objects given the entry payload.
	*/
	
	public static DomainLogEntryBuilder newBuilder() {
		return new DomainLogEntryBuilder();
	}

	public DomainLogEntry(Builder builder) {
		super(builder);
	}


}
