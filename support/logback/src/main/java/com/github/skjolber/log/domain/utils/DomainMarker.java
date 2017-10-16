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

    protected final String qualifier;
    protected DomainMarker parent;

    public DomainMarker(String qualifier) {
        super(LogstashMarker.MARKER_NAME_PREFIX + qualifier);

        this.qualifier = qualifier;
    }
    
    public String getQualifier() {
		return qualifier;
	}

	@Override
	public synchronized void add(Marker reference) {
		// note: this looks at the name passed to the superclass constructor
		if(reference instanceof LogstashMarker) {
			super.add(reference);
		} else {
			throw new IllegalArgumentException("Expected marker instance of " + LogstashMarker.class.getName());
		}
	}

	public void writeHeadTo(JsonGenerator generator) throws IOException {
	  	// check if there is MDC JSON data
		if(qualifier != null && !qualifier.isEmpty()) {
			// subtree
		    generator.writeFieldName(qualifier);
		    generator.writeStartObject();
		}
	}
	
	public void writeTailTo(JsonGenerator generator) throws IOException {
	  	if(qualifier != null && !qualifier.isEmpty()) {
	  		generator.writeEndObject();
	  	}
	}

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

	public DomainMarker getParent() {
		return parent;
	}
	
	@Override
	public void prepareForDeferredProcessing() {
		// do nothing
	}

}
