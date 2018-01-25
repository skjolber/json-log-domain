package com.google.cloud.logging;

/**
 * Class for public constructors in {@linkplain LogEntry}.
 * 
 */

public class DefaultLogEntry extends LogEntry {

	private static final long serialVersionUID = 1L;

	public DefaultLogEntry(Builder builder) {
		super(builder);
	}

	public static class DefaultBuilder extends Builder {

		public DefaultBuilder(LogEntry entry) {
			super(entry);
		}

		public DefaultBuilder(Payload<?> payload) {
			super(payload);
		}
	}
}
