package com.github.skjolber.log.domain.codegen;

import java.util.ArrayList;
import java.util.List;

public class Domain {

	private String version;
	private String targetPackage;
	private String qualifier;
	private String name;
	private String description;
	
	private List<Key> keys = new ArrayList<Key>();
	private List<Tag> tags = new ArrayList<Tag>();

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTargetPackage() {
		return targetPackage;
	}

	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	
	public List<Tag> getTags() {
		return tags;
	}
	
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<Key> getKeys() {
		return keys;
	}
	
	public void setKeys(List<Key> keys) {
		this.keys = keys;
	}

	public void add(Key key) {
		this.keys.add(key);
	}
	
	public void add(Tag tag) {
		this.tags.add(tag);
	}

	public String getTargetPackageDirectory() {
		return qualifier.replace('.', '/');
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public boolean hasTags() {
		return !tags.isEmpty();
	}
}
