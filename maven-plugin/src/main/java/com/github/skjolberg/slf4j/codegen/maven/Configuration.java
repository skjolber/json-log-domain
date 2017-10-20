package com.github.skjolberg.slf4j.codegen.maven;

public class Configuration {

	private Boolean java;
	private Boolean elastic;
	private Boolean markdown;
	
	public Boolean getJava() {
		return java;
	}
	public void setJava(Boolean java) {
		this.java = java;
	}
	public Boolean getElastic() {
		return elastic;
	}
	public void setElastic(Boolean elastic) {
		this.elastic = elastic;
	}
	public Boolean getMarkdown() {
		return markdown;
	}
	public void setMarkdown(Boolean markdown) {
		this.markdown = markdown;
	}
	@Override
	public String toString() {
		return "Configuration [java=" + java + ", elastic=" + elastic + ", markdown=" + markdown + "]";
	}
	
	
}
