package com.github.skjolber.log.domain.codegen.stackdriver;

import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang3.ClassUtils;

import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.model.Key;
import com.github.skjolber.log.domain.stackdriver.utils.DomainPayloadMdc;
import com.github.skjolber.log.domain.stackdriver.utils.DomainPayload;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

public class MdcGenerator {
	
	protected static final String MARKER_MDC = "PayloadMdc";
	
	public static JavaFile statement(Domain ontology) {
		
		ClassName name = ClassName.get(ontology.getTargetPackage(), ontology.getName() + MARKER_MDC);

		ClassName markerName = PayloadGenerator.getName(ontology);
		
	    TypeName wildcard = WildcardTypeName.subtypeOf(DomainPayload.class);
	    TypeName classOfAny = ParameterizedTypeName.get(ClassName.get(Class.class), wildcard);
	    
		ParameterSpec type = ParameterSpec.builder(classOfAny, "type").build();

		return JavaFile.builder(name.packageName(), TypeSpec.classBuilder(name)
					.superclass(ParameterizedTypeName.get( ClassName.get(DomainPayloadMdc.class), markerName))
					.addModifiers(Modifier.PUBLIC)
					.addMethod(MethodSpec.constructorBuilder()
						.addModifiers(Modifier.PUBLIC)
						.addStatement("super($T.QUALIFIER)", markerName)
						.build()
					)
					.addMethod(MethodSpec.methodBuilder("createPayload")
							.addModifiers(Modifier.PUBLIC)
							.addStatement("return new $T()", markerName)
							.returns(markerName)
							.build()
						)
					.addMethod(MethodSpec.methodBuilder("supports")
							.addModifiers(Modifier.PUBLIC)
							.addParameter(type)
							.addStatement("return $T.class == $N", markerName, type)
							.returns(boolean.class)
							.build()
						)
					.addMethod(MethodSpec.methodBuilder("getType")
							.addModifiers(Modifier.PUBLIC)
							.addStatement("return $T.class", markerName)
							.returns(ParameterizedTypeName.get(ClassName.get(Class.class), markerName))
							.build()
						)
					.build())
				
				.build();
	}
	
	public static TypeName getName(Domain ontology) {
		return ClassName.get(ontology.getTargetPackage(), ontology.getName() + MARKER_MDC);
	}
	
	protected static MethodSpec getDefinesKeyMethod(List<Key> fields) {
		ParameterSpec keyParameter = ParameterSpec.builder(String.class, "key").build();
		
		MethodSpec.Builder builder = MethodSpec.methodBuilder("definesKey")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(keyParameter)
				;
				
		com.squareup.javapoet.CodeBlock.Builder switchBlock = CodeBlock.builder();
		
		switchBlock.beginControlFlow("switch($N)", keyParameter);
				
		for(Key key : fields) {
			Class<?> type = PayloadGenerator.parseTypeFormat(key.getType(), key.getFormat());
			if(type.isPrimitive()) {
				type = ClassUtils.primitiveToWrapper(type);
			}
			
			switchBlock
					.beginControlFlow("case $S :", key.getId())
					.addStatement("return true")
					.endControlFlow();
		}
		
		switchBlock.endControlFlow();

		builder.addCode(switchBlock.build());

		return builder
				.addStatement("return false")
				.returns(boolean.class)
				.build();
	}
}
