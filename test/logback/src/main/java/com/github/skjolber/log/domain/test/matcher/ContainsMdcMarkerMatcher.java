package com.github.skjolber.log.domain.test.matcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Marker;

import com.github.skjolber.log.domain.utils.DeferredMdcMarker;
import com.github.skjolber.log.domain.utils.DomainMarker;

public class ContainsMdcMarkerMatcher<T> extends AbstractMarkerMatcher<T> {

	private static final long serialVersionUID = 1L;

	private List<DomainMarker> markers;
	
	public ContainsMdcMarkerMatcher(DomainMarker marker) {
		super(marker);
		
		this.markers = toList(marker, false);
	}

	private List<DomainMarker> toList(Marker marker, boolean mdc) {
		List<DomainMarker> markers = new ArrayList<>();
		populate(marker, markers, mdc);
		return markers;
	}

	private void populate(Marker marker, List<DomainMarker> markers, boolean mdc) {
		if (marker instanceof DomainMarker) {
			DomainMarker domainMarker = (DomainMarker)marker;

			if(mdc) {
				DomainMarker parent = domainMarker.getParent(); // assume from mdc context
				if(parent != null) {
					markers.add(parent);
				}
			} else {
				markers.add(domainMarker);
			}
		} else if(marker instanceof DeferredMdcMarker) {
			DeferredMdcMarker deferredMdcMarker = (DeferredMdcMarker)marker;
			
			for(DomainMarker mdcMarker : deferredMdcMarker.getMarkers()) {
				markers.add(mdcMarker);
			}
		}
		
		if(marker.hasReferences()) {
	    	Iterator<Marker> iterator = marker.iterator();
	    	while(iterator.hasNext()) {
	    		populate(iterator.next(), markers, mdc);
	    	}
    	}
	}

	public boolean matches(Marker marker) {
		List<DomainMarker> candiates = toList(marker, true);
		
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
