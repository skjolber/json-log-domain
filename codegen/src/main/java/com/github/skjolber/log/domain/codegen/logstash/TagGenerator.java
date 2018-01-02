package com.github.skjolber.log.domain.codegen.logstash;

import java.lang.reflect.Type;

import com.github.skjolber.log.domain.codegen.java.AbstractTagGenerator;
import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.utils.DomainTag;
import com.squareup.javapoet.ClassName;

public class TagGenerator extends AbstractTagGenerator {
	
	public static final String TAG = "Tag";
	
	public ClassName getName(Domain ontology) {
		return ClassName.get(ontology.getTargetPackage(), ontology.getName() + TAG);
	}

	@Override
	protected Type getSuperInterface() {
		return DomainTag.class;
	}

}
