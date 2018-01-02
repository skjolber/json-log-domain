package com.github.skjolber.log.domain.codegen.stackdriver;

import java.lang.reflect.Type;

import com.github.skjolber.log.domain.codegen.java.AbstractTagGenerator;
import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.stackdriver.utils.DomainPayloadTag;
import com.squareup.javapoet.ClassName;

public class TagGenerator extends AbstractTagGenerator {
	
	public static final String TAG = "PayloadTag";
	
	public ClassName getName(Domain ontology) {
		return ClassName.get(ontology.getTargetPackage(), ontology.getName() + TAG);
	}

	@Override
	protected Type getSuperInterface() {
		return DomainPayloadTag.class;
	}

}
