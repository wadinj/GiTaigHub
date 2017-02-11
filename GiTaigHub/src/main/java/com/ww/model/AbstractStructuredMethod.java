package com.ww.model;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractStructuredMethod {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractStructuredMethod other = (AbstractStructuredMethod) obj;
		if (!Arrays.equals(args, other.args))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public abstract Class<?> getReturnType();
	
}
