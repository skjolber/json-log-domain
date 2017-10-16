package com.github.skjolber.log.domain.test.matcher;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.github.skjolber.log.domain.utils.DomainTag;

public class TagMatcher extends BaseMatcher<DomainTag> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Set<DomainTag> tags;

	public TagMatcher(DomainTag ... tags) {
		this(Arrays.asList(tags));
	}
	
	public TagMatcher(List<DomainTag> tags) {
		this.tags = new HashSet<>(tags);
	}

	public boolean matches(Object actual) {
    	if(actual instanceof DomainTag[]) {
    		DomainTag[] array = (DomainTag[])actual;
    		
    		Set<DomainTag> tags = new HashSet<>();
    		for(DomainTag tag : array) {
    			if(tag != null) {
    				tags.add(tag);
    			}
    		}
    		return this.tags.equals(tags);
    	}
    	return false;
    }

	@Override
	public void describeTo(Description description) {
    	StringBuilder builder = new StringBuilder();
    	builder.append("equals(\"");
		builder.append(Arrays.asList(tags));
    	builder.append("\")");
		description.appendText(builder.toString());
	}

	
}
