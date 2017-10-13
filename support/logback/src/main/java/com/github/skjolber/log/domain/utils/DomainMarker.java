package com.github.skjolber.log.domain.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Marker;

import com.fasterxml.jackson.core.JsonGenerator;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.LogstashMarker;

public class DomainMarker extends LogstashMarker implements StructuredArgument {
	
	private static final long serialVersionUID = 1L;

	public static final String MARKER_NAME = LogstashMarker.MARKER_NAME_PREFIX + "MAP_FIELDS";

    protected final Map<String, Object> map = new HashMap<>();
    protected final String qualifier;
    
    protected List<DomainMdc> mdc;

    public DomainMarker(String qualifier) {
        super(MARKER_NAME);

        this.qualifier = qualifier;
    }

    protected void captureDomainMdc() {
    	if(mdc == null) {
    		mdc = DomainMdc.copy();
    	}
    }
    
    public List<DomainMdc> getMdc() {
    	if(mdc == null) {
    		mdc = DomainMdc.copy();
    	}
		return mdc;
	}
    
    public void setMdc(List<DomainMdc> mdc) {
		this.mdc = mdc;
	}
    
    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
    	// check if there is MDC JSON data
    	
    	captureDomainMdc();
    	if(mdc == null || mdc.isEmpty()) {
    		JsonMdcJsonProvider.writeMap(generator, qualifier, map);
    	} else {
        	if(qualifier != null && !qualifier.isEmpty()) {
        		// subtree
    	        generator.writeFieldName(qualifier);
    	        generator.writeStartObject();
    	    }
    		
	        Map<String, Object> domainMdc = new HashMap<>();
			for(DomainMdc wrapper : mdc) {
				Marker item = wrapper.getDelegate();
	        
				if(item instanceof DomainMarker) {
					DomainMarker domainMarker = (DomainMarker)item;
		        	if(Objects.equals(domainMarker.getQualifier(), qualifier)) {
		        		domainMdc.putAll(domainMarker.getMap());
		        	}
				}
				
	        	if(item.hasReferences()) {
					Iterator<Marker> iterator = item.iterator();

					while(iterator.hasNext()) {
						Marker next = iterator.next();
		    			if(next instanceof DomainMarker) {
		    				DomainMarker domainMarker = (DomainMarker)next;
		    			
		    	        	if(Objects.equals(domainMarker.getQualifier(), qualifier)) {
		    	        		domainMdc.putAll(domainMarker.getMap());
		    	        	}
		    			}
					}
	        	}
	        }

			// mdc - filtered
	    	for (Map.Entry<?, ?> entry : domainMdc.entrySet()) {
	    		if(!map.containsKey(entry.getKey())) {
	                generator.writeFieldName(String.valueOf(entry.getKey()));
	                generator.writeObject(entry.getValue());
	    		}
            }

	    	// marker
	    	for (Map.Entry<?, ?> entry : map.entrySet()) {
                generator.writeFieldName(String.valueOf(entry.getKey()));
                generator.writeObject(entry.getValue());
            }
    		
        	if(qualifier != null && !qualifier.isEmpty()) {
        		generator.writeEndObject();
        	}
    	}
    }
    
    @Override
    public String toString() {
        return String.valueOf(map);
    }

    public boolean contains(String key) {
    	return map.containsKey(key);
    }

    public Object get(String key) {
    	return map.get(key);
    }    
    
    public Map<String, Object> getMap() {
		return map;
	}
    
    public String getQualifier() {
		return qualifier;
	}

	@Override
	public synchronized void add(Marker reference) {
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

}
