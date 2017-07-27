package com.github.skjolber.log.domain.test.matcher;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.github.skjolber.log.domain.utils.DomainTag;

public class TagMatcher extends BaseMatcher<DomainTag> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final DomainTag tags[];

	public TagMatcher(DomainTag ... tags) {
		this.tags = tags;
	}
	
	public TagMatcher(List<DomainTag> tags) {
		this.tags = tags.toArray(new DomainTag[tags.size()]);
	}

	public boolean matches(Object actual) {
    	if(actual instanceof List) {
    		List list = (List)actual;
    		for(DomainTag tag : tags) {
    			if(!list.contains(tag)) {
    				return false;
    			}
    		}
    		return true;
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
