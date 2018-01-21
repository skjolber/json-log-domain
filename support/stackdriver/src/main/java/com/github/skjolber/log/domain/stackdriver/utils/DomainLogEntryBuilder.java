package com.github.skjolber.log.domain.stackdriver.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Marker;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.DefaultLogEntry.DefaultBuilder;
import com.google.cloud.logging.Payload.JsonPayload;
import com.google.cloud.logging.HttpRequest;
import com.google.cloud.logging.Operation;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.google.cloud.logging.SourceLocation;

public class DomainLogEntryBuilder extends DefaultBuilder {

	private DomainPayload domainPayload;
	
	public DomainLogEntryBuilder() {
		this((Payload)null);
	}

	public DomainLogEntryBuilder(Payload<?> payload) {
		super(payload);
	}

	public DomainLogEntryBuilder(DomainLogEntry entry) {
		super(entry);
	}

	/**
	 * Sets the name of the log to which this log entry belongs. The log name must be less than 512
	 * characters long and can only include the following characters: upper and lower case
	 * alphanumeric characters: {@code [A-Za-z0-9]}; and punctuation characters: {@code _-./}. The
	 * forward-slash ({@code /}) characters in the log name must be URL-encoded. Examples:
	 * {@code syslog}, {@code library.googleapis.com%2Fbook_log}.
	 */
	public DomainLogEntryBuilder setLogName(String logName) {
		super.setLogName(logName);
		return this;
	}


	/**
	 * Sets the monitored resource associated with this log entry. Example: a log entry that reports
	 * a database error would be associated with the monitored resource designating the particular
	 * database that reported the error.
	 */
	public DomainLogEntryBuilder setResource(MonitoredResource resource) {
		super.setResource(resource);
		return this;
	}


	/**
	 * Sets the time at which the event described by the log entry occurred, in milliseconds. If
	 * omitted, the Logging service will use the time at which the log entry is received.
	 */
	public DomainLogEntryBuilder setTimestamp(long timestamp) {
		super.setTimestamp(timestamp);
		return this;
	}


	/**
	 * Sets the time the log entry was received by Stackdriver Logging.
	 */
	public DomainLogEntryBuilder setReceiveTimestamp(long receiveTimestamp) {
		super.setReceiveTimestamp(receiveTimestamp);
		return this;
	}


	/**
	 * Sets the severity of the log entry. If not set, {@link Severity#DEFAULT} is used.
	 */
	public DomainLogEntryBuilder setSeverity(Severity severity) {
		super.setSeverity(severity);
		return this;
	}


	/**
	 * Sets a unique ID for the log entry. If you provide this field, the Logging service considers
	 * other log entries in the same log with the same ID as duplicates which can be removed. If
	 * omitted, the Logging service will generate a unique ID for this log entry.
	 */
	public DomainLogEntryBuilder setInsertId(String insertId) {
		super.setInsertId(insertId);
		return this;
	}


	/**
	 * Sets information about the HTTP request associated with this log entry, if applicable.
	 */
	public DomainLogEntryBuilder setHttpRequest(HttpRequest httpRequest) {
		super.setHttpRequest(httpRequest);
		return this;
	}


	/**
	 * Sets an optional set of user-defined (key, value) data that provides additional information
	 * about the log entry.
	 */
	public DomainLogEntryBuilder setLabels(Map<String, String> labels) {
		super.setLabels(labels);
		return this;
	}


	/**
	 * Adds a label to the log entry's labels. Labels are user-defined (key, value) data that
	 * provides additional information about the log entry.
	 */
	public DomainLogEntryBuilder addLabel(String key, String value) {
		super.addLabel(key, value);
		return this;
	}


	/**
	 * Clears all the labels of the log entry. Labels are user-defined (key, value) data that
	 * provides additional information about the log entry.
	 */
	public DomainLogEntryBuilder clearLabels() {
		super.clearLabels();
		return this;
	}


	/**
	 * Sets information about an operation associated with the log entry, if applicable.
	 */
	public DomainLogEntryBuilder setOperation(Operation operation) {
		super.setOperation(operation);
		return this;
	}


	/**
	 * Sets the resource name of the trace associated with the log entry, if any. If it contains a
	 * relative resource name, the name is assumed to be relative to `//tracing.googleapis.com`.
	 */
	public DomainLogEntryBuilder setTrace(String trace) {
		super.setTrace(trace);
		return this;
	}


	/**
	 * Sets the source code location information associated with the log entry if any.
	 */
	public DomainLogEntryBuilder setSourceLocation(SourceLocation sourceLocation) {
		super.setSourceLocation(sourceLocation);
		return this;
	}


	/**
	 * Sets the payload for this log entry. The log entry payload can be provided as an UTF-8 string
	 * (see {@link Payload.StringPayload}), a JSON object (see {@link Payload.JsonPayload}, or
	 * a protobuf object (see {@link Payload.ProtoPayload}).
	 *
	 * @see <a href="https://cloud.google.com/logging/docs/view/logs_index">Log Entries and Logs</a>
	 */
	public DomainLogEntryBuilder setPayload(Payload payload) {
		super.setPayload(payload);
		return this;
	}

	public DomainLogEntryBuilder setPayload(DomainPayload payload) {
		this.domainPayload = payload;
		return this;
	}

	/**
	 * Creates a {@code LogEntry} object for this builder.
	 */
	public DomainLogEntry build() {
		// add mdc for domains which have no marker within the event
		if(domainPayload != null) {
			@SuppressWarnings("rawtypes")
			Set<Class> filter = new HashSet<>();
	
			Map<String, Object> map = new HashMap<>();
			
			domainPayload.build(map);
			filter.add(domainPayload.getClass());
			
			if(domainPayload.hasReferences()) {
				for (DomainPayload reference : domainPayload.getRefereces()) {
					filter.add(reference.getClass());
					
					reference.build(map);
				}
			}
			
			for (DomainPayloadMdc<? extends DomainPayload> abstractMdc : DomainPayloadMdc.getMdcs()) {
				if(filter.contains(abstractMdc.getType())) {
					continue;
				}
				DomainPayload domainMarker = abstractMdc.get();
				if(domainMarker != null) {
					// copy values into map
					domainMarker.build(map);
				}
			}
			
			super.setPayload(JsonPayload.of(map));
		}
		
		return new DomainLogEntry(this);
	}
 
}
