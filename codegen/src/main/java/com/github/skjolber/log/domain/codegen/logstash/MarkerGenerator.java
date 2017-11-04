package com.github.skjolber.log.domain.codegen.logstash;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Marker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.skjolber.log.domain.codegen.TagGenerator;
import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.model.Key;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class MarkerGenerator {
	
	public static final String MARKER = "Marker";
	public static final String MARKER_BUILDER = "MarkerBuilder";
	protected static final String PARENT_FIELD_NAME = "parent";

	public static JavaFile marker(Domain ontology) {
		// Note: omitting equals-method because of the way the way LogstashMarker compares references. 
		
		List<Key> keys = ontology.getKeys();
		
		ClassName name = getName(ontology);

		ClassName superClassName = ClassName.get("com.github.skjolber.log.domain.utils", "DomainMarker");

		Builder builder = TypeSpec.classBuilder(name)
					.superclass(superClassName)
					.addJavadoc(composeJavadoc(ontology, name))
					.addModifiers(Modifier.PUBLIC);

		com.squareup.javapoet.MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.addStatement("super($S, QUALIFIER)", ontology.getName().toUpperCase())
			.addStatement("$T $N = mdc.get()", name, PARENT_FIELD_NAME)
			.beginControlFlow("if($N != null)", PARENT_FIELD_NAME);
			
		for(Key key : keys) {
			constructor.addStatement("this.$N = $N.$N", key.getId(), PARENT_FIELD_NAME, key.getId());
		}
		
		if(ontology.hasTags()) {
			constructor
				.addStatement("this.tags = $N.tags", PARENT_FIELD_NAME);
		}
		
		constructor
			.addStatement("this.$N = $N", PARENT_FIELD_NAME, PARENT_FIELD_NAME)
			.endControlFlow();
		
		builder.addMethod(constructor.build());

		boolean global = !ontology.hasQualifier();
		
		// private static final long serialVersionUID = 1L;
		builder.addField(FieldSpec.builder(long.class, "serialVersionUID", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("1L").build());
		builder.addField(FieldSpec.builder(String.class, "QUALIFIER", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL).initializer("$S", !global ? ontology.getQualifier() : "").build());

		TypeName mdcName = MdcGenerator.getName(ontology);
		
		builder.addField(FieldSpec.builder(mdcName, "mdc", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).build());

		builder.addStaticBlock(CodeBlock.builder().addStatement("mdc = new $T()", mdcName).addStatement("mdc.register()").build());

		builder.addMethod(MethodSpec.methodBuilder("getMdc")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addStatement("return mdc")
				.returns(mdcName)
				.build());
		
		ClassName tags;
		if(ontology.hasTags()) {
			tags = TagGenerator.getName(ontology);
		} else {
			tags = null;
		}
		
		for(Key key : keys) {
			builder.addField(getField(name, key));
		}

		if(ontology.hasTags()) {
			builder.addField(FieldSpec.builder(ArrayTypeName.of(tags), "tags", Modifier.PRIVATE).build());
		}
		
		for(Key key : keys) {
			builder.addMethod(getMethod(name, key));
		}

		for(Key key : keys) {
			builder.addMethod(getGetter(name, key));
		}
		
		builder
			.addMethod(getWriterMethod(keys, name, tags, global))
			.addMethod(getPopMethod(ontology))
			.addMethod(getPushMethod(ontology))
			.addMethod(getSetKeyMethod(keys))
			.addMethod(getParseAndSetKeyMethod(keys))
			.addMethod(MdcGenerator.getDefinesKeyMethod(keys))
			;
		
		if(ontology.hasTags()) {
			builder
				.addMethod(getTagsBuilderMethod(name, tags, ontology.getTags().size()))
				.addMethod(getTagsGetterMethod(tags));
		}

		builder
			.addMethod(getEqualToMethod(keys, name, tags, ontology.hasTags() ? ontology.getTags().size() : 0))
			.addMethod(getToStringBuilderMethod(ontology))
			;
		
		return JavaFile.builder(name.packageName(), builder.build()).build();
	}

	private static MethodSpec getParseAndSetKeyMethod(List<Key> keys) {
		ParameterSpec keyParameter = ParameterSpec.builder(String.class, "key").build();
		ParameterSpec valueParameter = ParameterSpec.builder(Object.class, "value").build();
		
		MethodSpec.Builder builder = MethodSpec.methodBuilder("parseAndSetKey")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(keyParameter)
				.addParameter(valueParameter);
				;
				
		com.squareup.javapoet.CodeBlock.Builder switchBlock = CodeBlock.builder();
		
		switchBlock.beginControlFlow("switch($N)", keyParameter);
				
		for(Key key : keys) {
			Class<?> type = parseTypeFormat(key.getType(), key.getFormat());
			if(type.isPrimitive()) {
				type = ClassUtils.primitiveToWrapper(type);
			}
			
			switchBlock.beginControlFlow("case $S :", key.getId());
			
			
			if(type == Date.class || type == String.class) {
				switchBlock.addStatement("this.$N = ($T)$N", key.getId(), type, valueParameter);
			} else if(type == Integer.class){
				switchBlock.addStatement("this.$N = asInteger($N)", key.getId(), valueParameter);
			} else if(type == Long.class){
				switchBlock.addStatement("this.$N = asLong($N)", key.getId(), valueParameter);
			} else if(type == Float.class){
				switchBlock.addStatement("this.$N = asFloat($N)", key.getId(), valueParameter);
			} else if(type == Double.class){
				switchBlock.addStatement("this.$N = asDouble($N)", key.getId(), valueParameter);
			} else {
				throw new IllegalArgumentException("Unknown type " + type.getName());
			}
					
			switchBlock
					.addStatement("break")
					.endControlFlow();
		}
		
		switchBlock.endControlFlow();

		builder.addCode(switchBlock.build());

		return builder.build();
	}

	public static ClassName getName(Domain ontology) {
		return ClassName.get(ontology.getTargetPackage(), ontology.getName() + MARKER);
	}

	private static MethodSpec getEqualToMethod(List<Key> fields, ClassName name, ClassName tags, int size) {
		ParameterSpec parameter = ParameterSpec.builder(Marker.class, "marker").build();

		MethodSpec.Builder builder = MethodSpec.methodBuilder("equalTo")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(parameter)
				.beginControlFlow("if($N instanceof $T)", parameter, name)
					.addStatement("$T domainMarker = ($T)$N", name, name, parameter)
				;

		for(Key key : fields) {
			builder
					.addCode(CodeBlock.builder()
					.beginControlFlow("if(!$T.equals(this.$N, domainMarker.$N))", Objects.class, key.getId(), key.getId())
					.addStatement("return false")
					.endControlFlow().build());
		}

		if(tags != null) {
			// tags
			builder
					.addCode(
							CodeBlock.builder()
							.beginControlFlow("if(!$T.equals(this.tags, domainMarker.tags))", Arrays.class)
								.addStatement("return false")
							.endControlFlow()
							.build());
		}
		
		
		
		return builder
			.addStatement("return true")
			.endControlFlow()
			.addStatement("return false")
			.returns(boolean.class)
			
			.build();
	}
	
	private static MethodSpec getToStringBuilderMethod(Domain ontology) {
		// output like in map
		ParameterSpec parameter = ParameterSpec.builder(StringBuilder.class, "builder").build();

		MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToString")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(parameter)
				;

		if(ontology.hasQualifier()) {
			builder.addStatement("$N.append($S)", parameter, ontology.getQualifier() + "{");
		} else {
			builder.addStatement("$N.append($S)", parameter, "{");
		}
		
		for(Key key : ontology.getKeys()) {
			builder
				.beginControlFlow("if(this.$N != null)", key.getId())
					.addStatement("$N.append($S)", parameter, key.getId() + "=")
					.addStatement("$N.append(this.$N)", parameter, key.getId())
					.addStatement("$N.append($S)", parameter, ", ")
				.endControlFlow();
		}

		int size;
		if(ontology.hasQualifier()) {
			size = ontology.getQualifier().length() + 1;
		} else {
			size = 1;
		}
		
		if(ontology.hasTags()) {
			ClassName tags = TagGenerator.getName(ontology);
			// tags
			builder
				.beginControlFlow("if(this.$N != null)", "tags")
				.addStatement("int mark = $N.length()", parameter)
					.addStatement("$N.append($S)", parameter, "tags=[") // size 6
					.beginControlFlow("for($T tag : tags)", tags)
						.beginControlFlow("if(tag != null)")
							.addStatement("$N.append(tag.getId())", parameter)
							.addStatement("$N.append($S)", parameter, ", ")
						.endControlFlow()
					.endControlFlow()
					.beginControlFlow("if(mark + 6 < $N.length())", parameter)
						.addComment("at least one tag was present")
						.addStatement("$N.setLength($N.length() - 2)", parameter, parameter)
						.addStatement("$N.append($S)", parameter, "]")
					.nextControlFlow("else if(mark == " + size + ")", parameter)
						.addComment("no fields")
						.addStatement("$N.setLength(mark)", parameter)
					.nextControlFlow("else")
						.addComment("at least one field was present, remove extra comma")
						.addStatement("$N.setLength(mark - 2)", parameter)
					.endControlFlow()
				.nextControlFlow("else if($N.length() != " + size + ")", parameter)
					.addStatement("$N.setLength($N.length() - 2)", parameter, parameter)
				.endControlFlow();
		} else {
			builder
			.beginControlFlow("if($N.length() != " + size + ")", parameter)
				.addComment("remove extra comma")
				.addStatement("$N.setLength($N.length() - 2)", parameter, parameter)
			.endControlFlow();
		}
		
		
		
		return builder
				.addStatement("$N.append($S)", parameter, "}")
				.addStatement("super.writeToString($N)", parameter)
				.build();
	}


	private static MethodSpec getWriterMethod(List<Key> fields, ClassName name, ClassName tags, boolean global) {
		ParameterSpec parameter = ParameterSpec.builder(JsonGenerator.class, "generator").build();
		
		MethodSpec.Builder builder = MethodSpec.methodBuilder("writeTo")
				.addModifiers(Modifier.PUBLIC)
				.addException(IOException.class)
				.addParameter(parameter);
		
		if(!global) {
			builder.addStatement("writeHeadTo($N)", parameter);
		}
		
		for(Key key : fields) {
			builder.addCode(CodeBlock.builder()
					.beginControlFlow("if(this.$N != null)", key.getId())
					.addStatement("$N.writeFieldName($S)", parameter, key.getId())
					.addStatement("$N." + getWriteJsonMethod(key) + "(this.$N)", parameter, key.getId()) // TODO microoptimize by checking for type
					.endControlFlow()
					.build());
		}

		if(tags != null) {
			// tags
			builder.addCode(CodeBlock.builder()
					.beginControlFlow("if(this.$N != null)", "tags")
					.addStatement("$N.writeFieldName($S)", parameter, "tags")
					.addStatement("$N.writeStartArray()", parameter)
					.beginControlFlow("for($T tag : tags)", tags)
					.beginControlFlow("if(tag != null)")
					.addStatement("generator.writeString(tag.getId())", parameter)
					.endControlFlow()
					.endControlFlow()
					.addStatement("$N.writeEndArray()", parameter)
					.endControlFlow()
					.build());
		}
		if(!global) {
			builder.addStatement("writeTailTo($N)", parameter);
		}
		
		return builder.build();
	}

	private static String getWriteJsonMethod(Key key) {
		// TODO more write methods
		if(key.getType().equals("string") && key.getFormat() == null) {
			return "writeString";
		}
		
		if(key.getType().equals("integer") || key.getType().equals("number")) {
			return "writeNumber";
		}
		
		return "writeObject";
	}

	private static FieldSpec getField(ClassName name, Key key) {
		Class<?> type = parseTypeFormat(key.getType(), key.getFormat());
		
		if(type.isPrimitive()) {
			type = ClassUtils.primitiveToWrapper(type);
		}
		
		return FieldSpec.builder(type, key.getId(), Modifier.PRIVATE).build();
	}

	private static String composeJavadoc(Domain ontology, ClassName name) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(ontology.getName());
		builder.append(" log-support.");
		if(ontology.getDescription() != null) {
			builder.append("<br/>\n");
			builder.append(ontology.getDescription());
		}
		builder.append("<br/><br/>");
		builder.append("\n");
		builder.append("\n");

		builder.append("Usage: <br/><br/>");
		builder.append("\n");
		builder.append("\n");
		builder.append("import static ");
		builder.append(name.reflectionName());
		builder.append(".*");
		builder.append("\n");
		builder.append("\n");
		
		return builder.toString();
	}

	private static MethodSpec getTagsBuilderMethod(ClassName name, ClassName tags, int size) {
		
		ParameterSpec parameter = ParameterSpec.builder(ArrayTypeName.of(tags), "tags").build();
		return MethodSpec.methodBuilder("tags")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(parameter)
				.varargs()
				.addStatement("$T clone = new $T[" + size + "]", ArrayTypeName.of(tags), tags)
				.beginControlFlow("if(this.tags != null)")
				.addStatement("System.arraycopy(this.tags, 0, clone, 0, " + size + ")")
				.endControlFlow()
				.beginControlFlow("for($T tag : tags)", tags)
				.addStatement("clone[tag.ordinal()] = tag")
				.endControlFlow()
				.addStatement("this.tags = clone")
				.returns(name)
				.addStatement("return this")
				.build();
	}

	private static MethodSpec getTagsGetterMethod(ClassName tags) {
		
		return MethodSpec.methodBuilder("getTags")
				.addModifiers(Modifier.PUBLIC)
				.returns(ArrayTypeName.of(tags))
				.addStatement("return tags")
				.build();
	}

	
	private static MethodSpec getMethod(ClassName name, Key key) {
		Class<?> type = parseTypeFormat(key.getType(), key.getFormat());
		if(type.isPrimitive()) {
			type = ClassUtils.primitiveToWrapper(type);
		}
		
		ParameterSpec parameter = ParameterSpec.builder(type, key.getId()).build();
		
		return MethodSpec.methodBuilder(key.getId())
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addParameter(parameter)
				.addStatement("this.$N = $N", key.getId(), parameter)
				.addJavadoc(key.getDescription())
				.returns(name)
				.addStatement("return this")
				.build();
	}
	
	private static MethodSpec getGetter(ClassName name, Key key) {
		Class<?> type = parseTypeFormat(key.getType(), key.getFormat());
		if(type.isPrimitive()) {
			type = ClassUtils.primitiveToWrapper(type);
		}
		
		String id = key.getId();
		
		return getGetter(key, type, id);
	}

	private static MethodSpec getGetter(Key key, Class<?> type, String id) {
		return MethodSpec.methodBuilder("get" + Character.toUpperCase(id.charAt(0)) + id.substring(1))
				.addModifiers(Modifier.PUBLIC)
				.addJavadoc(key.getDescription())
				.returns(type)
				.addStatement("return $N", id)
				.build();
	}

	protected static Class<?> parseTypeFormat(String type, String format) {
		switch(type) {
			case "integer" : {
				if(format != null) {
					if(format.equals("int32")) {
						return Integer.class;
					} else if(format.equals("int64")) {
						return Long.class;
					}
				}
				break;
			}
			case "string" : {
				if(format == null) {
					return String.class;
				} else if(format.equals("date")) {
					return Date.class;
				} else if(format.equals("date-time")) {
					return Date.class;
				} else if(format.equals("password")) {
					return String.class;
				} else if(format.equals("byte")) {
					return String.class;
				} else if(format.equals("binary")) {
					return String.class;
				}
				break;
			}
			case "number" : {
				if(format != null) {
					if(format.equals("float")) {
						return Float.class;
					} else if(format.equals("double")) {
						return Double.class;
					}
				}
				break;
			}
		}
		throw new IllegalArgumentException("Unknown type " + type + " format " + format);
	}
	
	public static JavaFile markerBuilder(Domain ontology) {
		List<Key> keys = ontology.getKeys();
		
		ClassName name = ClassName.get(ontology.getTargetPackage(), ontology.getName()+ MARKER_BUILDER);
		
		Builder builder = TypeSpec.classBuilder(name)
					.addJavadoc(composeJavadoc(ontology, name))
					.addModifiers(Modifier.PUBLIC);
				
		ClassName markerName = getName(ontology);
		
		for(Key key : keys) {
			builder.addMethod(getBuilderMethod(markerName, key));
		}
		
		if(ontology.hasTags()) {
			ClassName tagsName = TagGenerator.getName(ontology);
			
			ParameterSpec parameter = ParameterSpec.builder(ArrayTypeName.of(tagsName), "value").build();
			
			builder.addMethod(MethodSpec.methodBuilder("tags")
					.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
					.addParameter(parameter).varargs()
					.addStatement("$T marker = new $T()", markerName, markerName)
					.addStatement("marker.tags($N)", parameter)
					.returns(markerName)
					.addStatement("return marker")
					.build());
			
		}
		
		return JavaFile.builder(name.packageName(), builder.build()).build();
	}

	private static MethodSpec getBuilderMethod(ClassName name, Key key) {
		Class<?> type = parseTypeFormat(key.getType(), key.getFormat());
		
		ParameterSpec parameter = ParameterSpec.builder(type, "value").build();
		
		return MethodSpec.methodBuilder(key.getId())
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addParameter(parameter)
				.addStatement("$T marker = new $T()", name, name)
				.addStatement("marker." + key.getId() + "($N)", parameter)
				.addJavadoc(key.getDescription())
				.returns(name)
				.addStatement("return marker")
				.build();
	}
	
	private static MethodSpec getPopMethod(Domain ontology) {
		return MethodSpec.methodBuilder("popContext")
				.addModifiers(Modifier.PUBLIC)
				.addStatement("super.popContext()")
				.addStatement("mdc.pop(this)")
				.build();
	}
	
	private static MethodSpec getPushMethod(Domain ontology) {
		return MethodSpec.methodBuilder("pushContext")
				.addModifiers(Modifier.PUBLIC)
				.addStatement("mdc.push(this)")
				.addStatement("super.pushContext()")
				.build();
	}

	private static MethodSpec getSetKeyMethod(List<Key> fields) {
		ParameterSpec keyParameter = ParameterSpec.builder(String.class, "key").build();
		ParameterSpec valueParameter = ParameterSpec.builder(Object.class, "value").build();
		
		MethodSpec.Builder builder = MethodSpec.methodBuilder("setKey")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(keyParameter)
				.addParameter(valueParameter);
				;
				
		com.squareup.javapoet.CodeBlock.Builder switchBlock = CodeBlock.builder();
		
		switchBlock.beginControlFlow("switch($N)", keyParameter);
				
		for(Key key : fields) {
			Class<?> type = parseTypeFormat(key.getType(), key.getFormat());
			if(type.isPrimitive()) {
				type = ClassUtils.primitiveToWrapper(type);
			}
			
			switchBlock
					.beginControlFlow("case $S :", key.getId())
					.addStatement("this.$N = ($T)$N", key.getId(), type, valueParameter) // TODO microoptimize by checking for type
					.addStatement("break")
					.endControlFlow();
		}
		
		switchBlock.endControlFlow();

		builder.addCode(switchBlock.build());

		return builder.build();
	}
	
}
