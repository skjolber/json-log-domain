package com.github.skjolber.log.domain.codegen;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.skjolber.log.domain.utils.AbstractDomainLogger;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

public class LoggerGenerator {
	
	private static final String LOGGER = "Logger";
	private static final String STATEMENT = "LogStatement";
	private static final String MARKER_BUILDER = "MarkerBuilder";

	private static MethodSpec getter(String methodName, FieldSpec field) {
		return MethodSpec.methodBuilder(methodName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.returns(field.type)
				.addStatement("return $N", field)
				.build();
	}
	
	public static JavaFile statement(Domain ontology) {
		
		List<Key> keys = ontology.getKeys();
		
		ClassName name = ClassName.get(ontology.getTargetPackage(), ontology.getName() + STATEMENT);

		ClassName superClassName = ClassName.get("com.github.skjolber.log.domain.utils", "AbstractDomainLogStatement");

		ClassName markerName = ClassName.get(ontology.getTargetPackage(), ontology.getName() + MarkerGenerator.MARKER);

		Builder builder = TypeSpec.classBuilder(name)
					.superclass(ParameterizedTypeName.get(superClassName, markerName))
					.addModifiers(Modifier.PUBLIC);

		ParameterSpec loggerParameter = ParameterSpec.builder(org.slf4j.Logger.class, "logger").build();
		ParameterSpec levelParameter = ParameterSpec.builder(int.class, "level").build();

		builder = builder.addMethod(MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addParameter(loggerParameter)
				.addParameter(levelParameter)
   
				.addStatement("super($N, $N, new $T())", loggerParameter, levelParameter, markerName)
		        .build());
		
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
		builder.append("private static final ");
		builder.append(name.simpleName());
		builder.append(" logger = new ");
		builder.append(name.simpleName());
		builder.append("(LoggerFactory.getLogger(MyClass.class))");
		builder.append("\n");
		builder.append("\n");

		
		return builder.toString();
	}

	private static MethodSpec getCreateLogStatementMethod(ClassName name) {
		MethodSpec.Builder builder = MethodSpec.methodBuilder("createLogStatement")
				.addModifiers(Modifier.PROTECTED)
				.addParameter(int.class, "level");

		builder = builder.addStatement("return new $T(slf4jLogger, level)", name);
		
		return builder.returns(name).build();
	}

	private static MethodSpec getTagsMethod(ClassName name, ClassName tags) {
		MethodSpec.Builder builder = MethodSpec.methodBuilder("tags")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(ArrayTypeName.of(tags), "value")
				.varargs();
		
		ClassName arrays = ClassName.get(Arrays.class);
		
		builder = builder.addStatement("marker.tags(value)");
		
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
		
		builder = builder.addStatement("marker." + key.getId() + "($N)", parameter);
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
	
	public static JavaFile logger(Domain ontology) {
		List<Key> keys = ontology.getKeys();
		
		ClassName name = ClassName.get(ontology.getTargetPackage(), ontology.getName() + LOGGER);

		ClassName statementName = ClassName.get(ontology.getTargetPackage(), ontology.getName() + STATEMENT);

		ClassName superClassName = ClassName.get(AbstractDomainLogger.class);

		ClassName markerName = ClassName.get(ontology.getTargetPackage(), ontology.getName() + MarkerGenerator.MARKER);
		
		Builder builder = TypeSpec.classBuilder(name)
				.superclass(ParameterizedTypeName.get(superClassName, statementName))				
				.addJavadoc(composeJavadoc(ontology, name))
				.addModifiers(Modifier.PUBLIC);
						
		ParameterSpec loggerParameter = ParameterSpec.builder(org.slf4j.Logger.class, "logger").build();

		builder = builder.addMethod(MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addParameter(loggerParameter)
   
				.addStatement("super($N)", loggerParameter)
		        .build());
		
		builder = builder.addMethod(getCreateLogStatementMethod(statementName));
		
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
