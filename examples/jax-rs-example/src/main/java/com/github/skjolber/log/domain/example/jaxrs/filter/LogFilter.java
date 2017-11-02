package com.github.skjolber.log.domain.example.jaxrs.filter;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.example.document.DocumentStoreMarker;
import com.example.document.DocumentStoreMarkerBuilder;
import com.example.document.DocumentStoreMdc;

// https://blog.dejavu.sk/2014/01/08/binding-jax-rs-providers-to-resource-methods/

@Provider
@Logged
public class LogFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private final int index;
	
    public LogFilter(int index) {
		this.index = index;
	}

	@Override
    public void filter(ContainerRequestContext context ) throws IOException {
        UriInfo uriInfo = context.getUriInfo();
        List<PathSegment> pathSegments = uriInfo.getPathSegments();
        if(pathSegments != null && !pathSegments.isEmpty()) {
            // http://memorynotfound.com/jaxrs-path-segments-matrix-parameters/
        	DocumentStoreMdc.mdc(DocumentStoreMarkerBuilder.id(pathSegments.get(index).toString()));
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext context) throws IOException {
        UriInfo uriInfo = requestContext.getUriInfo();
        
        List<PathSegment> pathSegments = uriInfo.getPathSegments();
        if(pathSegments != null && !pathSegments.isEmpty()) {
            DocumentStoreMarker.getMdc().remove();
        }
    }
    

}