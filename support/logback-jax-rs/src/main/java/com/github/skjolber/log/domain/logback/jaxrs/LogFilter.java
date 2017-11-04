package com.github.skjolber.log.domain.logback.jaxrs;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import com.github.skjolber.log.domain.utils.DomainMarker;
import com.github.skjolber.log.domain.utils.DomainMdc;

// https://blog.dejavu.sk/2014/01/08/binding-jax-rs-providers-to-resource-methods/

public class LogFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private final int[] indexes;
	private final String[] keys;
	private final DomainMdc<? extends DomainMarker> mdc;
	
	public LogFilter(String[] keys, int[] indexes, Class<? extends DomainMarker> type) {
		this.keys = keys;
		this.indexes = indexes;
		this.mdc = DomainMdc.mdcForType(type);
	}

	@Override
    public void filter(ContainerRequestContext context ) throws IOException {
        UriInfo uriInfo = context.getUriInfo();
        List<PathSegment> pathSegments = uriInfo.getPathSegments();
        if(pathSegments != null && !pathSegments.isEmpty()) {
            // http://memorynotfound.com/jaxrs-path-segments-matrix-parameters/
        	DomainMarker marker = mdc.createMarker();
        	for(int i = 0; i < indexes.length; i++) {
        		marker.parseAndSetKey(keys[i], pathSegments.get(indexes[i]).toString());
        	}
        	marker.pushContext();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext context) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        
        List<PathSegment> pathSegments = uriInfo.getPathSegments();
        if(pathSegments != null && !pathSegments.isEmpty()) {
        	mdc.remove();
        }
    }
    

}