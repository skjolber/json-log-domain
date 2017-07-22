package com.github.skjolber.log.domain.codegen;

import javax.lang.model.element.Modifier;

import com.github.skjolber.log.domain.utils.DomainMarker;
import com.github.skjolber.log.domain.utils.DomainTag;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class TagGenerator {
	
	public static final String TAG = "Tag";

	@SuppressWarnings("restriction")
	public static JavaFile tag(Domain ontology) {
		
		ClassName name = ClassName.get(ontology.getTargetPackage(), ontology.getName() + TAG);

		Builder builder = TypeSpec.enumBuilder(name)
					.addModifiers(Modifier.PUBLIC)
					.addJavadoc(composeJavadoc(ontology, name))
					.addSuperinterface(DomainTag.class);

		for(Tag tag : ontology.getTags()) {
			builder = builder.addEnumConstant(tag.getId().toUpperCase(), TypeSpec.anonymousClassBuilder("$S, $S", tag.getId(), tag.getDescription()).addJavadoc(tag.getDescription()).build());
		}
		
		FieldSpec id = FieldSpec.builder(String.class, "id", Modifier.PRIVATE, Modifier.FINAL).build();
		FieldSpec description = FieldSpec.builder(String.class, "description", Modifier.PRIVATE, Modifier.FINAL).build();
		
		builder = builder
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
