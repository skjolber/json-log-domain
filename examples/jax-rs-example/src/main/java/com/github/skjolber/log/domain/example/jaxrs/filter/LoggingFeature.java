package com.github.skjolber.log.domain.example.jaxrs.filter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Path;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import com.github.skjolber.log.domain.utils.DomainMarker;
import com.github.skjolber.log.domain.utils.DomainMdc;

@Provider
public class LoggingFeature implements DynamicFeature {
 
	private static final Pattern pattern = Pattern.compile("\\{(?<key>[a-zA-Z0-9]+)\\}");
	
    @Override
    public void configure(final ResourceInfo resourceInfo,
                          final FeatureContext context) {
 
        final Method resourceMethod = resourceInfo.getResourceMethod();
        Path path = resourceMethod.getAnnotation(Path.class);
        if(path != null) {
        	Logged annotation = resourceMethod.getAnnotation(Logged.class);
        	if(annotation == null) {
        		annotation = resourceInfo.getResourceClass().getAnnotation(Logged.class);
        	}

        	if(annotation != null) {
	        	Class<? extends DomainMarker> value = annotation.value();
	        	
	        	try {
					value.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
	        	DomainMdc<? extends DomainMarker> mdc = DomainMdc.mdcForType(value);
	        	
	        	List<String> keys = new ArrayList<>();
	        	List<Integer> indexes = new ArrayList<>();
	        	
	        	String paths[] = path.value().split("/"); // assume /a/b/c
	        	for(int i = 0; i < paths.length; i++) {
	        		// Now create matcher object.
	                Matcher m = pattern.matcher(paths[i]);
		            if(m.matches()) {
		            	String key = m.group("key");
		            	if(mdc.definesKey(key)) {
			            	keys.add(key);
			            	indexes.add(i - 1);
		            	}
		            }
	        	}
	        	
	        	int[] indexArray = new int[indexes.size()];
	        	for(int i = 0; i < indexArray.length; i++) {
	        		indexArray[i] = indexes.get(i);
	        	}
	        	
	        	context.register(new LogFilter(keys.toArray(new String[keys.size()]), indexArray, mdc));
        	}
        }
        
    }
}