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
 * Adds JSON MDC fields to output. If a {@link DomainMarker} contains conflicting qualifiers, not logging is performed for those
 * qualifier here. The {@link DomainMarker} is itself responsible for including JSON MDC values when writing fields.
 *
 */

public class JsonMdcJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> implements FieldNamesAware<LogstashFieldNames> {

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
    	List<DomainMdcMarker> mdc = null;
		Set<String> filter = null;
    	
		Marker marker = event.getMarker();
		if(marker != null) {
			filter = new HashSet<>();

			if(marker instanceof DomainMarker) {
				DomainMarker domainMarker = (DomainMarker)marker;
				filter.add(domainMarker.getQualifier());
			} else if(marker instanceof DeferredMdcMarker) {
				DeferredMdcMarker deferred = (DeferredMdcMarker)marker;
				mdc = deferred.getMdc();
			}
			
			if(marker.hasReferences()) {
				Iterator<Marker> iterator = marker.iterator();
				while(iterator.hasNext()) {
					Marker next = iterator.next();
					
					if(next instanceof DomainMarker) {
						DomainMarker domainMarker = (DomainMarker)next;
						filter.add(domainMarker.getQualifier());
					} else if(marker instanceof DeferredMdcMarker) {
						DeferredMdcMarker deferred = (DeferredMdcMarker)marker;
						mdc = deferred.getMdc();
					}
				}
			}
		}
		
		if(mdc == null) {
			// we are not in a deferred state
			mdc = DomainMdc.copy();
		}
		
		if(mdc == null || mdc.isEmpty()) {
			return; // no mdc for this event
		}
		
		// compose map of resulting mdc values
		// filter out domains available in this marker - those will have to pull from mdc themselves to print 
		// the corresponding values
		Map<String, Map<String, Object>> mdcMap = new HashMap<>();
		for(DomainMdcMarker item : mdc) {
			
			if(filter == null || !filter.contains(item.getQualifier())) {
				Map<String, Object> map = mdcMap.get(item.getQualifier());
				if(map == null) {
					map = new HashMap<>();
					mdcMap.put(item.getQualifier(), map);
				}
			
				map.putAll(item.getMap());
			}
			
			if(item.hasReferences()) {
				Iterator<Marker> iterator = item.iterator();

				while(iterator.hasNext()) {
					Marker next = iterator.next();
	    			if(next instanceof DomainMarker) {
	    				DomainMarker reference = (DomainMarker)next;
	    				if(filter == null || !filter.contains(reference.getQualifier())) {
							Map<String, Object> referenceMap = mdcMap.get(reference.getQualifier());
							if(referenceMap == null) {
								referenceMap = new HashMap<>();
								mdcMap.put(item.getQualifier(), referenceMap);
							}
							
							referenceMap.putAll(item.getMap());			    				
	    				}
	    			}
				}
			}
		}
		
		// write resulting map
		for (Entry<String, Map<String, Object>> mdcMapEntry : mdcMap.entrySet()) {
			writeMap(generator, mdcMapEntry.getKey(), mdcMapEntry.getValue());
		}

    }

    protected static void writeMap(JsonGenerator generator, String qualifier, Map<String, Object> map) throws IOException {
    	if(map == null || map.isEmpty()) {
    		return;
    	}
    	if(qualifier != null && !qualifier.isEmpty()) {
    		// subtree
	        generator.writeFieldName(qualifier);
	        generator.writeObject(map);
	    } else {
	    	// root
	    	for (Map.Entry<?, ?> entry : map.entrySet()) {
                generator.writeFieldName(String.valueOf(entry.getKey()));
                generator.writeObject(entry.getValue());
            }	    	
	    }
    }
    
    @Override
    public void prepareForDeferredProcessing(ILoggingEvent event) {
		// copy mdc list, even if empty, share the same instance for all markers
    	
		List<DomainMdcMarker> mdc = DomainMdc.get();
		if(mdc == null) {
			mdc = Collections.emptyList();
		} else {
			mdc = new ArrayList<>(mdc);
		}
			
		Marker marker = event.getMarker();
		if(marker == null) {
			// insert mdc-logging
			// kind of a hack, but best possible solution given constraints in logger/loggerfactory
			if(event instanceof LoggingEvent) {
				LoggingEvent loggingEvent = (LoggingEvent)event;
				loggingEvent.setMarker(new DeferredMdcMarker(mdc));
			} else {
				throw new IllegalArgumentException("Event cannot be prepared for deferred processing: " + event.getClass().getName());
			}					
		} else {
			if(marker instanceof DomainMarker) {
				DomainMarker domainMarker = (DomainMarker)marker;
				domainMarker.setMdc(mdc);
			}
			
			if(marker.hasReferences()) {
				Iterator<Marker> iterator = marker.iterator();
				while(iterator.hasNext()) {
					Marker next = iterator.next();
					
					if(next instanceof DomainMarker) {
						DomainMarker domainMarker = (DomainMarker)next;
						domainMarker.setMdc(mdc);
					}
				}
			}
			
			marker.add(new DeferredMdcMarker(mdc));
		}
		
    	super.prepareForDeferredProcessing(event);
    }
    
    @Override
    public void setFieldNames(LogstashFieldNames fieldNames) {
        // setFieldName(fieldNames.getMdc());
    }
}