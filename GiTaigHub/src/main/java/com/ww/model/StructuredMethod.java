package com.ww.model;

public class StructuredMethod extends AbstractStructuredMethod {

	private Class<?> returnType;


	public StructuredMethod(String name, Class<?> returnType, String[] args, Class<?> annotation) {
		super(name, args, annotation);
		this.returnType = returnType;
	}
	
	public StructuredMethod(String name, Class<?> returnType, String[] args) {
		super(name, args, null);
		this.returnType = returnType;
	}
	
	public Class<?> getReturnType() {
		return returnType;
	}
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}	
}
