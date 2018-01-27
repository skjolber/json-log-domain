package com.github.skjolber.log.domain.utils;

import java.io.IOException;

import java.util.Iterator;

import org.slf4j.Marker;

import com.fasterxml.jackson.core.JsonGenerator;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.LogstashMarker;
/**
 * 
 * Marker list for capturing MDC context. 
 *
 */

public class MdcListMarker extends LogstashMarker implements StructuredArgument {

	private static final long serialVersionUID = 1L;

	public static final String MARKER_NAME = LogstashMarker.MARKER_NAME_PREFIX + "DEFERRED_MAP_FIELDS";

	public MdcListMarker() {
		super(MARKER_NAME);
	}

	@Override
	public void writeTo(JsonGenerator generator) throws IOException {
		// the framework take care of writing the references
	}

	public void writeToString(StringBuilder builder) {
		if(hasReferences()) {
			Iterator<Marker> iterator = iterator();
			while(iterator.hasNext()) {
				Marker next = iterator.next();
	
				if(next instanceof DomainMarker) {
					@SuppressWarnings("resource")
					DomainMarker reference = (DomainMarker)next;
					reference.writeToString(builder);
					builder.append(' ');
				} else {
					builder.append(next.toString());
					builder.append(' ');
				}
			}
			builder.setLength(builder.length() - 1);
		}		
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder(1024);
		writeToString(builder);
		return builder.toString();
	}

}