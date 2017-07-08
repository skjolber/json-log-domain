package com.github.skjolber.log.domain.codegen;

public class Key {

	private String id;
	private String name;
	private String description;
	private String type;
	private String format;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	@Override
	public String toString() {
		return "Key [id=" + id + ", name=" + name + ", description=" + description + ", type=" + type + ", format="
				+ format + "]";
	}

	
}
