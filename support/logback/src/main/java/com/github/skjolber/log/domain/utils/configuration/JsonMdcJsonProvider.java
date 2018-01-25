package com.github.skjolber.log.domain.utils.configuration;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Marker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.skjolber.log.domain.utils.DomainMarker;
import com.github.skjolber.log.domain.utils.DomainMdc;
import com.github.skjolber.log.domain.utils.MdcListMarker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.FieldNamesAware;
import net.logstash.logback.fieldnames.LogstashFieldNames;

/**
 * 
 * Adds JSON MDC fields to output. If a marker is present and contains overriding qualifiers, no logging is performed for those
 * qualifier here. The {@link DomainMarker} is itself responsible for including JSON MDC values when writing fields.
 *
 */

public class JsonMdcJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> implements FieldNamesAware<LogstashFieldNames> {

    @SuppressWarnings("resource")
	@Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
    	// write only mdc data from domains which have no marker within the current event
    	
		Marker marker = event.getMarker();
		if(marker == null) {
			writeFullContext(generator);
		} else if(marker instanceof MdcListMarker) {
			// all mdc data already captured
		} else if(!marker.hasReferences()) {
			if(marker instanceof DomainMarker) {
				writeContextForFilteredMarker(generator, marker);
			} else {
				writeFullContext(generator);
			}
		} else {
			writeContextForFilteredMarkerAndReferences(generator, marker);
		}
    }

	private void writeContextForFilteredMarkerAndReferences(JsonGenerator generator, Marker marker) throws IOException {
		// filter MDCs for the current marker plus all its references
		@SuppressWarnings("rawtypes")
		Set<Class> filter = new HashSet<>();

		if(marker instanceof DomainMarker) {
			filter.add(marker.getClass());
		}
			
		Iterator<Marker> iterator = marker.iterator();
		while(iterator.hasNext()) {
			Marker next = iterator.next();
			
			if(next instanceof DomainMarker) {
				filter.add(next.getClass());
			}
		}
		
		// write mdc context now
		for (DomainMdc<? extends DomainMarker> mdc : DomainMdc.getMdcs()) { // list of possible MDCs
			if(filter.contains(mdc.getType())) {
				// skip
				continue;
			}
			DomainMarker mdcMarker = mdc.get();
			if(mdcMarker != null) {
				mdcMarker.writeTo(generator);
			}
		}
	}

	private void writeContextForFilteredMarker(JsonGenerator generator, Marker marker) throws IOException {
		// filter MDCs for the current marker
		DomainMarker domainMarker = (DomainMarker)marker;
		for (DomainMdc<? extends DomainMarker> abstractMdc : DomainMdc.getMdcs()) {
			if(abstractMdc.supports(domainMarker.getClass())) {
				// skip
				continue;
			}
			DomainMarker mdcMarker = abstractMdc.get();
			if(mdcMarker != null) {
				mdcMarker.writeTo(generator);
			}
		}
	}

	private void writeFullContext(JsonGenerator generator) throws IOException {
		for (DomainMdc<? extends DomainMarker> abstractMdc : DomainMdc.getMdcs()) {
			DomainMarker domainMarker = abstractMdc.get();
			if(domainMarker != null) {
				domainMarker.writeTo(generator);
			}
		}
	}

    @Override
    public void prepareForDeferredProcessing(ILoggingEvent event) {
    	captureContext(event);
    	
    	super.prepareForDeferredProcessing(event);
    }

    @SuppressWarnings("resource")
	public static void captureContext(ILoggingEvent event) {
		// add from mdc those types which have no marker within the current event
		Marker marker = event.getMarker();
		
		if(marker == null) {
			// insert mdc-logging
			// kind of a hack, but best possible solution given constraints in logger/loggerfactory
			if(event instanceof LoggingEvent) {
				captureContextAsList(event);
			} else {
				throw new IllegalArgumentException("Event cannot be prepared for deferred processing: " + event.getClass().getName() + ", please use events of type " + LoggingEvent.class.getName());
			}
		} else {
			captureContextAsReferences(marker);			
		}
    }

	public static void captureContextAsReferences(Marker marker) {
		// add mdc as references for domains which have no marker within the event
		@SuppressWarnings("rawtypes")
		Set<Class> filter = new HashSet<>();

		if(marker instanceof DeferredProcessingAware) {
			DeferredProcessingAware aware = (DeferredProcessingAware)marker;
			aware.prepareForDeferredProcessing();
		}

		if(marker instanceof DomainMarker) {
			filter.add(marker.getClass());
		}
		
		if(marker.hasReferences()) {
			Iterator<Marker> iterator = marker.iterator();
			while(iterator.hasNext()) {
				Marker next = iterator.next();
				
				if(marker instanceof DeferredProcessingAware) {
					DeferredProcessingAware aware = (DeferredProcessingAware)marker;
					aware.prepareForDeferredProcessing();
				}
				
				if(next instanceof DomainMarker) {
					filter.add(next.getClass());
				}
			}
		}
		
		// add mdc context as references now
		for (DomainMdc<? extends DomainMarker> mdc : DomainMdc.getMdcs()) { // list of possible MDCs
			if(filter.contains(mdc.getType())) {
				continue;
			}
			DomainMarker domainMarker = mdc.get();
			if(domainMarker != null) {
				domainMarker.prepareForDeferredProcessing();
				marker.add(domainMarker);
			}
		}
	}

	public static void captureContextAsList(ILoggingEvent event) {
		MdcListMarker mdcListMarker = null;
		for (DomainMdc<? extends DomainMarker> abstractMdc : DomainMdc.getMdcs()) {
			DomainMarker domainMarker = abstractMdc.get();
			if(domainMarker != null) {
				domainMarker.prepareForDeferredProcessing();

				if(mdcListMarker == null) {
					mdcListMarker = new MdcListMarker();
				}
				mdcListMarker.add(domainMarker);
			}
		}

		if(mdcListMarker != null) {
			LoggingEvent loggingEvent = (LoggingEvent)event;
			loggingEvent.setMarker(mdcListMarker);
		}
	}

    
    @Override
    public void setFieldNames(LogstashFieldNames fieldNames) {
        // setFieldName(fieldNames.getMdc());
    }
}