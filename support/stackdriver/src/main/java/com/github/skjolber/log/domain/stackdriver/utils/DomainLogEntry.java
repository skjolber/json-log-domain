package com.github.skjolber.log.domain.stackdriver.utils;

import com.google.cloud.logging.DefaultLogEntry;

public class DomainLogEntry extends DefaultLogEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new builder for {@code LogEntry} objects given the entry payload.
	 * @return Returns a builder for {@code LogEntry} objects 
	 */

	public static DomainLogEntryBuilder newBuilder() {
		return new DomainLogEntryBuilder();
	}

	/**
	 * Returns a builder for {@code LogEntry} objects given the entry payload.
	 * @param payload the payload 
	 * @return Returns a builder for {@code LogEntry} objects 
	 */

	public static DomainLogEntryBuilder newBuilder(DomainPayload payload) {
		return new DomainLogEntryBuilder().setPayload(payload);
	}

	public DomainLogEntry(Builder builder) {
		super(builder);
	}


}
