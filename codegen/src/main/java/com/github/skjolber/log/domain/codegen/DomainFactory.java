package com.github.skjolber.log.domain.codegen;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.model.Key;
import com.github.skjolber.log.domain.model.Tag;

public class DomainFactory {

	public static Domain parse(Reader reader) throws IOException {
		
	    YamlReader yamlReader = new YamlReader(reader);

	    Object object = yamlReader.read();
	    Map map = (Map)object;

	    return parse(map);
	}
	
	public static Domain parse(Map map) throws IOException {
		
		Domain ontology = new Domain();
		
		ontology.setQualifier((String)map.get("qualifier"));
		ontology.setVersion((String)map.get("version"));
		ontology.setTargetPackage((String)map.get("package"));
		ontology.setName((String)map.get("name"));
		ontology.setDescription((String)map.get("description"));
	    
		List keys = (List) map.get("keys");
		if(keys != null) {
			for(int i = 0; i < keys.size(); i++) {
				ontology.add(parseKey((Map)keys.get(i)));
			}
		}

		List tags = (List) map.get("tags");
		if(tags != null) {
			for(int i = 0; i < tags.size(); i++) {
				ontology.add(parseTag((Map)tags.get(i)));
			}
		}
		
		return ontology;
	}

	private static Tag parseTag(Map map) {
		Tag tag = new Tag();
		Map.Entry<String, Object> entry = (Entry<String, Object>) map.entrySet().iterator().next();
		tag.setId((String)entry.getKey());
		tag.setDescription(entry.getValue().toString());
		
		return tag;
	}

	private static Key parseKey(Map map) {
		Key key = new Key();

		Map.Entry<String, Map> entry = (Entry<String, Map>) map.entrySet().iterator().next();
		key.setId(entry.getKey());
		Map entryMap = entry.getValue();
		key.setDescription((String)entryMap.get("description"));
		key.setName((String)entryMap.get("name"));
		key.setType((String)entryMap.get("type"));
		key.setFormat((String)entryMap.get("format"));
		
		return key;
	}

}
