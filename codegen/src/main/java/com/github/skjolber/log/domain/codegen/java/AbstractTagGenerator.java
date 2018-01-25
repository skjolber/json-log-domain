package com.github.skjolber.log.domain.codegen.java;

import java.lang.reflect.Type;

import javax.lang.model.element.Modifier;

import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.model.Tag;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public abstract class AbstractTagGenerator {
	
	public JavaFile tag(Domain ontology) {
		
		if(!ontology.hasTags()) {
			return null;
		}
		
		ClassName name = getName(ontology);

		Builder builder = TypeSpec.enumBuilder(name)
					.addModifiers(Modifier.PUBLIC)
					.addJavadoc(composeJavadoc(ontology, name))
					.addSuperinterface(getSuperInterface());

		for(Tag tag : ontology.getTags()) {
			builder.addEnumConstant(tag.getId().toUpperCase(), TypeSpec.anonymousClassBuilder("$S, $S", tag.getId(), tag.getDescription()).addJavadoc(tag.getDescription()).build());
		}
		
		FieldSpec id = FieldSpec.builder(String.class, "id", Modifier.PRIVATE, Modifier.FINAL).build();
		FieldSpec description = FieldSpec.builder(String.class, "description", Modifier.PRIVATE, Modifier.FINAL).build();
		
		builder
				.addField(id)
				.addField(description)
				.addMethod(MethodSpec.constructorBuilder()
						.addModifiers(Modifier.PRIVATE)
						.addParameter(String.class, "id")
						.addParameter(String.class, "description")
						.addStatement("this.$N = $N", "id", "id")
						.addStatement("this.$N = $N", "description", "description")
						.build())
				.addMethod(getter("getId", id))
				.addMethod(getter("getDescription", description));

		return JavaFile.builder(name.packageName(), builder.build()).build();
	}

	protected abstract Type getSuperInterface();

	public abstract ClassName getName(Domain ontology);

	private static MethodSpec getter(String methodName, FieldSpec field) {
		return MethodSpec.methodBuilder(methodName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.returns(field.type)
				.addStatement("return $N", field)
				.build();
	}

	private static String composeJavadoc(Domain ontology, ClassName name) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(ontology.getName());
		builder.append(" tags.");
		if(ontology.getDescription() != null) {
			builder.append("<br/>\n");
			builder.append(ontology.getDescription());
		}
		builder.append("\n");
		builder.append("\n");

		return builder.toString();
	}

}
