package com.github.skjolber.log.domain.test.matcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Marker;

import com.github.skjolber.log.domain.utils.DomainMarker;
import com.github.skjolber.log.domain.utils.DomainMdc;

public class ContainsMdcMarkerMatcher<T> extends AbstractMarkerMatcher<T> {

	private static final long serialVersionUID = 1L;

	private List<DomainMarker> markers;
	
	public ContainsMdcMarkerMatcher(DomainMarker marker) {
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
				
				DomainMarker mdc = candidate;
				if(!isMdc(candidate)) {
					mdc = candidate.getParent();
				}
				if(mdc != null && required.equalTo(mdc)) {
					continue required;
				}
			}
			return false;
		}
		
		return true;
	}

	private boolean isMdc(DomainMarker candidate) {
		DomainMdc<? extends DomainMarker> mdc = DomainMdc.mdcForType(candidate.getClass());
		return mdc != null && mdc.exists(candidate);
	}

}
