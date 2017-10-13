package com.github.skjolber.log.domain.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.json.JSONObject;

import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.model.Key;

/**
 * 
 * Generator for adding mapping to Elastic Search.
 * 
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-get-field-mapping.html">Elastic manual</a>
 * 
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-templates.html">Elastic manual</a>
 */

public class ElasticGenerator {

	public static void generate(File file, File outputDirectory) throws IOException {
		Domain domain = DomainFactory.parse(new FileReader(file));

		generate(domain, outputDirectory);
	}

	public static void generate(Domain domain, File outputDirectory) throws IOException {
		Writer writer = new OutputStreamWriter(new FileOutputStream(outputDirectory));
		try {
			writer.write(generate(domain));
		} finally {
			writer.close();
		}
	}

	public static String generate(Domain domain) throws IOException {
		return generateJson(domain).toString();
	}

	public static JSONObject generateJson(Domain domain) throws IOException {

		JSONObject properties = new JSONObject();

		for(Key key : domain.getKeys()) {

			JSONObject property = new JSONObject();

			property.put("type", parseTypeFormat(key.getType(), key.getFormat()));
			
			properties.put(key.getId(), property);
		}
		
		if(domain.hasTags()) {
			// https://www.elastic.co/guide/en/elasticsearch/reference/current/array.html
			JSONObject property = new JSONObject();
			property.put("type", "text");
			properties.put("tags", property);
		}
		
		return new JSONObject().put("mappings", new JSONObject().put(domain.getQualifier(), new JSONObject().put("properties", properties)));
	}
	
	/**
	 * @see https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html
	 */
	
	private static String parseTypeFormat(String type, String format) {
		switch(type) {
			case "integer" : {
				if(format != null) {
					if(format.equals("int32")) {
						return "integer";
					} else if(format.equals("int64")) {
						return "long";
					}
				}
				break;
			}
			case "string" : {
				if(format == null) {
					return "text";
				} else if(format.equals("date")) {
					return "date";
				} else if(format.equals("date-time")) {
					return "date";
				} else if(format.equals("password")) {
					return "text";
				} else if(format.equals("byte")) {
					return "byte";
				} else if(format.equals("binary")) {
					return "binary";
				}
				break;
			}
			case "number" : {
				if(format != null) {
					if(format.equals("float")) {
						return "float";
					} else if(format.equals("double")) {
						return "double";
					}
				}
				break;
			}
		}
		throw new IllegalArgumentException("Unknown type " + type + " format " + format);
	}

}
