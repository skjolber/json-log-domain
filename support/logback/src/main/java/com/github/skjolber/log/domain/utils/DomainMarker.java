package com.github.skjolber.log.domain.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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

    public DomainMarker(String qualifier) {
        super(MARKER_NAME);

        this.qualifier = qualifier;
    }
    
    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
    	if(qualifier != null && !qualifier.isEmpty()) { 
	        generator.writeFieldName(qualifier);
	        generator.writeObject(map);
	    } else {
	    	for (Map.Entry<?, ?> entry : map.entrySet()) {
                generator.writeFieldName(String.valueOf(entry.getKey()));
                generator.writeObject(entry.getValue());
            }	    	
	    }
    }
    
    @Override
    public String toString() {
        return String.valueOf(map);
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
}
