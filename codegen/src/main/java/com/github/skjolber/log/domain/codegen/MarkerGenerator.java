package com.github.skjolber.log.domain.codegen;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class MarkerGenerator {
	
	protected static final String MARKER = "Marker";
	protected static final String MARKER_BUILDER = "MarkerBuilder";

	private static MethodSpec getter(String methodName, FieldSpec field) {
		return MethodSpec.methodBuilder(methodName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.returns(field.type)
				.addStatement("return $N", field)
				.build();
	}
	
	public static JavaFile marker(Domain ontology) {
		
		List<Key> keys = ontology.getKeys();
		
		ClassName name = ClassName.get(ontology.getTargetPackage(), ontology.getName() + MARKER);

		ClassName superClassName = ClassName.get("com.github.skjolber.log.domain.utils", "DomainMarker");

		Builder builder = TypeSpec.classBuilder(name)
					.superclass(superClassName)
					.addJavadoc(composeJavadoc(ontology, name))
					.addModifiers(Modifier.PUBLIC);

		builder = builder.addMethod(MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addStatement("super($S)", ontology.getQualifier())
		        .build());
		
		// private static final long serialVersionUID = 1L;
		FieldSpec build = FieldSpec.builder(long.class, "serialVersionUID", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).initializer("1L").build();
		builder = builder.addField(build);
		
		for(Key key : keys) {
			builder = builder.addMethod(getMethod(name, key));
		}
		
		ClassName tags = ClassName.get(ontology.getTargetPackage(), ontology.getName() + TagGenerator.TAG);
		
		builder = builder.addMethod(getTagsMethod(name, tags));
		
		com.squareup.javapoet.JavaFile.Builder file = JavaFile.builder(name.packageName(), builder.build());
		
		return file.build();		
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

	private static MethodSpec getTagsMethod(ClassName name, ClassName tags) {
		MethodSpec.Builder builder = MethodSpec.methodBuilder("tags")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(ArrayTypeName.of(tags), "value")
				.varargs();
		
		ClassName arrays = ClassName.get(Arrays.class);
		
		builder = builder.addStatement("map.put($S, $T.asList(value))", "tags", arrays);
		
		return builder
				.returns(name)
				.addStatement("return this")
				.build();
	}

	private static MethodSpec getMethod(ClassName name, Key key) {
		Class type = parseTypeFormat(key.getType(), key.getFormat());
		
		ParameterSpec parameter = ParameterSpec.builder(type, "value").build();
		
		MethodSpec.Builder builder = MethodSpec.methodBuilder(key.getId())
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addParameter(parameter);
		
		builder = builder.addStatement("map.put($S, $N)", key.getId(), parameter);
		builder = builder.addJavadoc(key.getDescription());
		
		return builder.returns(name)
			.addStatement("return this")
			.build();
	}

	private String toString(Class type) {
		if(type == String.class) {
			return "$N";
		} else if(type == int.class) {
			return "Integer.toString($N)";
		} else if(type == boolean.class) {
			return "Boolean.parseBoolean($N)";
		} else if(type == long.class) {
			return "Long.toString($N)";
		} else if(type == float.class) {
			return "Float.toString($N)";
		} else if(type == double.class) {
			return "Double.toString($N)";
		} else if(type == byte[].class) {
			return "Integer.toString($N)";
		} else if(type == Date.class) {
			return "Integer.toString($N)";
		}

		throw new IllegalArgumentException();
	}

	private static Class parseTypeFormat(String type, String format) {
		switch(type) {
			case "integer" : {
				if(format != null) {
					if(format.equals("int32")) {
						return int.class;
					} else if(format.equals("int64")) {
						return long.class;
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
						return float.class;
					} else if(format.equals("double")) {
						return double.class;
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
				
		for(Key key : keys) {
			builder = builder.addMethod(getBuilderMethod(ClassName.get(ontology.getTargetPackage(), ontology.getName()+ MARKER), key));
		}
		
		com.squareup.javapoet.JavaFile.Builder file = JavaFile.builder(name.packageName(), builder.build());
		
		return file.build();		
		
	}

	private static MethodSpec getBuilderMethod(ClassName name, Key key) {
		Class type = parseTypeFormat(key.getType(), key.getFormat());
		
		ParameterSpec parameter = ParameterSpec.builder(type, "value").build();
		
		MethodSpec.Builder builder = MethodSpec.methodBuilder(key.getId())
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addParameter(parameter);
		
		builder = builder
				.addStatement("$T marker = new $T()", name, name)
				.addStatement("marker." + key.getId() + "($N)", parameter);
		
		builder = builder.addJavadoc(key.getDescription());

		return builder.returns(name)
			.addStatement("return marker")
			.build();
	}
	
}
