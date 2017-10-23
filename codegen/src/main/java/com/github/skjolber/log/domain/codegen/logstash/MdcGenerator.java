package com.github.skjolber.log.domain.codegen.logstash;

import javax.lang.model.element.Modifier;

import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.utils.DomainMdc;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class MdcGenerator {
	
	protected static final String MARKER_MDC = "Mdc";
	
	public static JavaFile statement(Domain ontology) {
		
		ClassName name = ClassName.get(ontology.getTargetPackage(), ontology.getName() + MARKER_MDC);

		ClassName markerName = MarkerGenerator.getName(ontology);
		
		return JavaFile.builder(name.packageName(), TypeSpec.classBuilder(name)
					.superclass(ParameterizedTypeName.get( ClassName.get(DomainMdc.class), markerName))
					.addModifiers(Modifier.PUBLIC)
					.addMethod(MethodSpec.constructorBuilder()
						.addModifiers(Modifier.PUBLIC)
						.addStatement("super($T.QUALIFIER)", markerName)
						.build()
					).build())
				.build();
	}
	
	public static TypeName getName(Domain ontology) {
		return ClassName.get(ontology.getTargetPackage(), ontology.getName() + MARKER_MDC);
	}
	
}
