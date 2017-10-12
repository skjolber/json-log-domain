package com.github.skjolber.log.domain.utils;

import java.io.Closeable;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

import net.logstash.logback.marker.LogstashMarker;

public abstract class DomainMdcMarker extends DomainMarker implements AutoCloseable, Closeable {

	public DomainMdcMarker(String qualifier) {
		super(qualifier);
		
		DomainMdc.add(this);
	}
	
	@Override
    public void writeTo(JsonGenerator generator) throws IOException {
		JsonMdcJsonProvider.writeMap(generator, qualifier, map);
	}

	@Override
	public void close() throws IOException {
		DomainMdc.remove(this);
	}
	
}
