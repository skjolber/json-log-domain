package com.github.skjolber.log.domain.model;

public class Tag {

	private String id;
	private String description;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "Tag [id=" + id + ", description=" + description + "]";
	}
	
	
}
