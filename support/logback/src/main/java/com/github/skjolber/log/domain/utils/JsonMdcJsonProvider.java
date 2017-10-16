package com.github.skjolber.log.domain.utils;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Marker;

import com.fasterxml.jackson.core.JsonGenerator;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
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

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
    	// write only mdc data from domains which have no marker within the current event
    	
		Marker marker = event.getMarker();
		if(marker == null) {
			writeMdc(generator);
			return;
		} else if(!marker.hasReferences()) {
			if(marker instanceof DeferredMdcMarker) {
				((DeferredMdcMarker)marker).writeTo(generator);
			} else if(marker instanceof DomainMarker) {
				DomainMarker domainMarker = (DomainMarker)marker;
				
				domainMarker.writeTo(generator);
				for (DomainMdc<? extends DomainMarker> abstractMdc : DomainMdc.getMdcs()) {
					if(!Objects.equals(abstractMdc.getQualifier(), domainMarker)) {
						DomainMarker mdcMarker = abstractMdc.get();
						if(mdcMarker != null) {
							mdcMarker.writeTo(generator);
						}
					}
				}
			} else {
				writeMdc(generator);
			}
			return;
		}

    	
		DeferredMdcMarker deferredMarker = null;
		Set<String> filter = new HashSet<>();

		if(marker instanceof DomainMarker) {
			DomainMarker domainMarker = (DomainMarker)marker;
			filter.add(domainMarker.getQualifier());
		} else if(marker instanceof DeferredMdcMarker) {
			((DeferredMdcMarker)marker).writeTo(generator);
			return;
		}
			
		Iterator<Marker> iterator = marker.iterator();
		while(iterator.hasNext()) {
			Marker next = iterator.next();
			
			if(next instanceof DomainMarker) {
				DomainMarker domainMarker = (DomainMarker)next;
				filter.add(domainMarker.getQualifier());
			} else if(marker instanceof DeferredMdcMarker) {
				((DeferredMdcMarker)marker).writeTo(generator);
				return;
			}
		}
		
		for (DomainMdc<? extends DomainMarker> abstractMdc : DomainMdc.getMdcs()) { // list of possible MDCs
			if(!filter.contains(abstractMdc.getQualifier())) {
				DomainMarker domainMarker = abstractMdc.get();
				if(domainMarker != null) {
					domainMarker.writeTo(generator);
				}
			}
		}
    }

	private void writeMdc(JsonGenerator generator) throws IOException {
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

    public static void captureContext(ILoggingEvent event) {
		// add a holder for domains which have no marker within the current event
		Marker marker = event.getMarker();
		
		if(marker == null) {
			// insert mdc-logging
			// kind of a hack, but best possible solution given constraints in logger/loggerfactory
			if(event instanceof LoggingEvent) {
				List<DomainMdc<? extends DomainMarker>> mdcs = DomainMdc.getMdcs(); // list of possible MDCs
				List<DomainMarker> deferredMarkers = new ArrayList<>(mdcs.size());
				for (DomainMdc<? extends DomainMarker> abstractMdc : mdcs) {
					DomainMarker domainMarker = abstractMdc.get();
					if(domainMarker != null) {
						deferredMarkers.add(domainMarker);
					}
				}

				LoggingEvent loggingEvent = (LoggingEvent)event;
				loggingEvent.setMarker(new DeferredMdcMarker(deferredMarkers));
			} else {
				throw new IllegalArgumentException("Event cannot be prepared for deferred processing: " + event.getClass().getName() + ", please use events of type " + LoggingEvent.class.getName());
			}
		} else {
			// add mdc for domains which have no marker within the event
			Set<String> filter = new HashSet<>();

			if(marker instanceof DomainMarker) {
				DomainMarker domainMarker = (DomainMarker)marker;
				domainMarker.prepareForDeferredProcessing();
				filter.add(domainMarker.getQualifier());
			}
			
			if(marker.hasReferences()) {
				Iterator<Marker> iterator = marker.iterator();
				while(iterator.hasNext()) {
					Marker next = iterator.next();
					
					if(next instanceof DomainMarker) {
						DomainMarker domainMarker = (DomainMarker)next;
						domainMarker.prepareForDeferredProcessing();
						filter.add(domainMarker.getQualifier());
					}
				}
			}
			
			List<DomainMdc<? extends DomainMarker>> mdcs = DomainMdc.getMdcs(); // list of possible MDCs
			List<DomainMarker> deferredMarkers = new ArrayList<>(mdcs.size());
			for (DomainMdc<? extends DomainMarker> abstractMdc : mdcs) {
				if(!filter.contains(abstractMdc.getQualifier())) {
					DomainMarker domainMarker = abstractMdc.get();
					if(domainMarker != null) {
						deferredMarkers.add(domainMarker);
					}
				}
			}
			marker.add(new DeferredMdcMarker(deferredMarkers));

		}
    }

    
    @Override
    public void setFieldNames(LogstashFieldNames fieldNames) {
        // setFieldName(fieldNames.getMdc());
    }
}