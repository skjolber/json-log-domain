package com.github.skjolber.log.domain.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Marker;

import com.fasterxml.jackson.core.JsonGenerator;

import ch.qos.logback.core.spi.DeferredProcessingAware;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.LogstashMarker;

public abstract class DomainMarker extends LogstashMarker implements StructuredArgument, DeferredProcessingAware, AutoCloseable, Closeable {

	private static final long serialVersionUID = 1L;

	protected static final Long asLong(Object object) {
		if(object instanceof Long) {
			return (Long)object;
		}
		if(object instanceof String) {
			return Long.parseLong((String)object);
		}
		throw new IllegalArgumentException("Expected " + Long.class.getName() + " or " + String.class.getName());
	}

	protected static final Integer asInteger(Object object) {
		if(object instanceof Integer) {
			return (Integer)object;
		}
		if(object instanceof String) {
			return Integer.parseInt((String)object);
		}
		throw new IllegalArgumentException("Expected " + Integer.class.getName() + " or " + String.class.getName());
	}

	protected static final Double asDouble(Object object) {
		if(object instanceof Double) {
			return (Double)object;
		}
		if(object instanceof String) {
			return Double.parseDouble((String)object);
		}
		throw new IllegalArgumentException("Expected " + Double.class.getName() + " or " + String.class.getName());
	}
	
	protected static final Float asFloat(Object object) {
		if(object instanceof Float) {
			return (Float)object;
		}
		if(object instanceof String) {
			return Float.parseFloat((String)object);
		}
		throw new IllegalArgumentException("Expected " + Float.class.getName() + " or " + String.class.getName());
	}

	protected final String qualifier;
	protected DomainMarker parent;

	public DomainMarker(String name, String qualifier) {
		super(LogstashMarker.MARKER_NAME_PREFIX + name);

		this.qualifier = qualifier;
	}

	public String getQualifier() {
		return qualifier;
	}

	@Override
	public void add(Marker reference) {
		if(reference instanceof LogstashMarker) {

			// limit to one level to make things a bit less complicated
			if(reference.hasReferences()) {
				throw new IllegalArgumentException("Please do not nest markers in more than one level");
			}
			super.add(reference);
		} else {
			throw new IllegalArgumentException("Expected marker instance of " + LogstashMarker.class.getName());
		}
	}

	public void writeHeadTo(JsonGenerator generator) throws IOException {
		// check if there is MDC JSON data
		// subtree
		generator.writeFieldName(qualifier);
		generator.writeStartObject();
	}

	public void writeTailTo(JsonGenerator generator) throws IOException {
		generator.writeEndObject();
	}

	@SuppressWarnings("resource")
	public void pushContext() {
		// actual operation on this instance delegated to subclass
		if(hasReferences()) {
			Iterator<Marker> iterator = iterator();
			while(iterator.hasNext()) {
				Marker next = iterator.next();

				if(next instanceof DomainMarker) {
					DomainMarker domainMarker = (DomainMarker)next;
					domainMarker.pushContext();
				}
			}
		}
	}

	@SuppressWarnings("resource")
	public void popContext() {
		// actual operation on this instance delegated to subclass
		if(hasReferences()) {
			Iterator<Marker> iterator = iterator();
			while(iterator.hasNext()) {
				Marker next = iterator.next();

				if(next instanceof DomainMarker) {
					DomainMarker domainMarker = (DomainMarker)next;
					domainMarker.popContext();
				}
			}
		}		
	}

	@Override
	public void close() {
		popContext();
	}

	public DomainMarker getParent() { // public for testing
		return parent;
	}

	@Override
	public void prepareForDeferredProcessing() {
		// do nothing
	}

	public abstract boolean equalTo(Marker marker);

	/**
	 * Write the equivalent of toString() to a buffer. Subclasses are expected to override this method.
	 * 
	 */

	public void writeToString(StringBuilder builder) {
		if(hasReferences()) {
			Iterator<Marker> iterator = iterator();
			while(iterator.hasNext()) {
				Marker marker = iterator.next();
				builder.append(" ");
				if(marker instanceof DomainMarker) {
					((DomainMarker)marker).writeToString(builder);
				} else {
					builder.append(marker.toString());
				}
			}
		}
	}

	public String toString() {
		StringBuilder builder = new StringBuilder(1024);
		writeToString(builder);
		return builder.toString();
	}
	
	public abstract void setKey(String key, Object value);
	
	public abstract boolean definesKey(String key);
	
	public abstract void parseAndSetKey(String key, Object value);
}
