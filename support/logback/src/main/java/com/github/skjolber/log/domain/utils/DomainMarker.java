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
    
    protected List<DomainMdcMarker> mdc;

    public DomainMarker(String qualifier) {
        super(MARKER_NAME);

        this.qualifier = qualifier;
    }

    public void captureDomainMdc() {
    	if(mdc == null) {
    		mdc = DomainMdc.copy();
    	}
    }
    
    public List<DomainMdcMarker> getMdc() {
    	if(mdc == null) {
    		mdc = DomainMdc.copy();
    	}
		return mdc;
	}
    
    public void setMdc(List<DomainMdcMarker> mdc) {
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
	        for(DomainMdcMarker item : mdc) {
	        	if(Objects.equals(item.getQualifier(), qualifier)) {
	        		domainMdc.putAll(item.getMap());
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
    
    /**
     * Search chained {@linkplain DomainMarker}s for the correct qualifier.
     * 
     * @param qualifier
     * @return a marker matching the qualifier, or null if no such exists.
     */
    
    public <T extends DomainMarker> T find(String qualifier) {
    	if(Objects.equals(qualifier, this.qualifier)) {
    		return (T) this;
    	}
    	
    	Iterator<Marker> iterator = iterator();
    	while(iterator.hasNext()) {
    		Marker next = iterator.next();
    		if(next instanceof DomainMarker) {
    			DomainMarker domainMarker = (DomainMarker)next;
        		if(Objects.equals(qualifier, domainMarker.qualifier)) {
            		return (T) domainMarker;
            	}
    		}
    	}
    	
    	return null;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainMarker other = (DomainMarker) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		if (qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!qualifier.equals(other.qualifier))
			return false;
		return true;
	}
    
	@Override
	public synchronized void add(Marker reference) {
		if(reference instanceof LogstashMarker) {
			super.add(reference);
		} else {
			throw new IllegalArgumentException("Expected marker instance of " + LogstashMarker.class.getName());
		}
	}


    
}
