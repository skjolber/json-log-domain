package com.github.skjolber.log.domain.example.jaxrs.filter;

import java.lang.reflect.Method;

import javax.ws.rs.Path;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class LoggingFeature implements DynamicFeature {
 
    @Override
    public void configure(final ResourceInfo resourceInfo,
                          final FeatureContext context) {
 
        final Method resourceMethod = resourceInfo.getResourceMethod();
        Path path = resourceMethod.getAnnotation(Path.class);
        if(path != null) {
        	
        	String paths[] = path.value().split("/");
        	for(int i = 0; i < paths.length; i++) {
	            if(paths[i].equals("{id}")) {
	            	context.register(new LogFilter(i - 1));
	                //context.register(LogFilter.class);
	            }
        	}
        }
        
    }
}