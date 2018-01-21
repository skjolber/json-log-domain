package com.github.skjolber.log.domain.test.matcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Marker;

import com.github.skjolber.log.domain.utils.DomainMarker;

public class ContainsMarkerMatcher<T> extends AbstractMarkerMatcher<T> {

	private static final long serialVersionUID = 1L;

	private List<DomainMarker> markers;
	
	public ContainsMarkerMatcher(DomainMarker marker) {
		super(marker);
		
		this.markers = toList(marker);
	}

	private List<DomainMarker> toList(Marker marker) {
		List<DomainMarker> markers = new ArrayList<>();
		populate(marker, markers);
		return markers;
	}

	private void populate(Marker marker, List<DomainMarker> markers) {
		if (marker instanceof DomainMarker) {
			markers.add((DomainMarker)marker);
		}
		
		if(marker.hasReferences()) {
	    	Iterator<Marker> iterator = marker.iterator();
	    	while(iterator.hasNext()) {
	    		populate(iterator.next(), markers);
	    	}
    	}
	}

	public boolean matches(Marker marker) {
		List<DomainMarker> candiates = toList(marker);
		
		required:
		for(DomainMarker required : markers) {
			for(DomainMarker candidate : candiates) {
				if(required.equalTo(candidate)) {
					continue required;
				}
			}
			return false;
		}
		
		return true;
	}

}
