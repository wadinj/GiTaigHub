package com.ww.model;

import java.util.List;

public class AbstractStructuredMethod {

	private String[] args;
	private Class<?> annotation;
	private String name;
	private List<String> statements;

	public AbstractStructuredMethod(String[] args, Class<?> annotation, String name, List<String> statements) {
		super();
		this.args = args;
		this.annotation = annotation;
		this.name = name;
		this.statements = statements;
	}

	public List<String> getStatements() {
		return statements;
	}

	public void setStatements(List<String> statements) {
		this.statements = statements;
	}
	
	public void addStatement(String statement) {
		getStatements().add(statement);
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
