package com.github.skjolber.log.domain.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DomainMdc {
	
	private static final long serialVersionUID = 1L;

    private static InheritableThreadLocal<List<DomainMdcMarker>> inheritableThreadLocal = new InheritableThreadLocal<List<DomainMdcMarker>>() {
        @Override
        protected List<DomainMdcMarker> childValue(List<DomainMdcMarker> parentValue) {
            if (parentValue == null) {
                return null;
            }
            return new ArrayList<DomainMdcMarker>(parentValue);
        }
    };

    public static void clear() {
    	List<DomainMdcMarker> list= inheritableThreadLocal.get();
    	if(list != null) {
    		list.clear();
    	}
    }

    public static List<DomainMdcMarker> get() {
    	return inheritableThreadLocal.get();
    }

	public static void remove(DomainMdcMarker marker) throws IOException {
		List<DomainMdcMarker> domainMarkers = inheritableThreadLocal.get();

		if(domainMarkers != null) {
			for(int i = 0; i < domainMarkers.size(); i++) {
				if(marker == domainMarkers.get(i)) {
					domainMarkers.remove(i);
					i--;
				}
			}
		}
	}

	public static void add(DomainMdcMarker marker) {
		List<DomainMdcMarker> current = inheritableThreadLocal.get();
		if(current == null) {
			current = new ArrayList<>();
			inheritableThreadLocal.set(current);
		}
		// add to chain
		current.add(marker);
	}
	
	public static List<DomainMdcMarker> copy() {
		List<DomainMdcMarker> list = inheritableThreadLocal.get();
		if(list != null) {
			return new ArrayList<>(list);
		}
		return null;
	}
	
}
 