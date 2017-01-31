package com.ww.model;

public class AbstractStructuredMethod {

	private String[] args;
	private Class<?> annotation;
	private String name;
	public AbstractStructuredMethod(String name, String[] args, Class<?> annotation) {
		super();
		this.name = name;
		this.args = args;
		this.annotation = annotation;
	}
	
	public Class<?> getAnnotation() {
		return annotation;
	}
	public void setAnnotation(Class<?> annotation) {
		this.annotation = annotation;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}

}
