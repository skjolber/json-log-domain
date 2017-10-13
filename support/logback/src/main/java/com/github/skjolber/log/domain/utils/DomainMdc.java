package com.github.skjolber.log.domain.utils;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import net.logstash.logback.marker.LogstashMarker;

public class DomainMdc implements AutoCloseable, Closeable {
	
	private static final long serialVersionUID = 1L;

    private static InheritableThreadLocal<List<DomainMdc>> inheritableThreadLocal = new InheritableThreadLocal<List<DomainMdc>>() {
        @Override
        protected List<DomainMdc> childValue(List<DomainMdc> parentValue) {
            if (parentValue == null) {
                return null;
            }
            return new ArrayList<DomainMdc>(parentValue);
        }
    };

    public static void clear() {
    	List<DomainMdc> list= inheritableThreadLocal.get();
    	if(list != null) {
    		list.clear();
    	}
    }

    public static List<DomainMdc> get() {
    	return inheritableThreadLocal.get();
    }

	public static void remove(DomainMdc marker) {
		List<DomainMdc> domainMarkers = inheritableThreadLocal.get();

		if(domainMarkers != null) {
			for(int i = 0; i < domainMarkers.size(); i++) {
				if(marker == domainMarkers.get(i)) {
					domainMarkers.remove(i);
					i--;
				}
			}
		}
	}

	public static void add(DomainMdc marker) {
		List<DomainMdc> current = inheritableThreadLocal.get();
		if(current == null) {
			current = new ArrayList<>();
			inheritableThreadLocal.set(current);
		}
		// add to chain
		current.add(marker);
	}
	
	public static List<DomainMdc> copy() {
		List<DomainMdc> list = inheritableThreadLocal.get();
		if(list != null) {
			return new ArrayList<>(list);
		}
		return null;
	}
	
	public static DomainMdc mdc(LogstashMarker marker) {
		return new DomainMdc(marker);
	}
	
	private final LogstashMarker delegate;

	public DomainMdc(LogstashMarker delegate) {
		this.delegate = delegate;
		
		add(this);
	}

	@Override
	public void close() {
		remove(this);
	}
	
	public LogstashMarker getDelegate() {
		return delegate;
	}	
}
 