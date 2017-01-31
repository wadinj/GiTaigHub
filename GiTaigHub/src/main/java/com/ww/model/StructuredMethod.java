package com.ww.model;

public class StructuredMethod extends AbstractStructuredMethod {

	private String returnType;


	public StructuredMethod(String name, String returnType, String[] args, Class<?> annotation) {
		super(name, args, annotation);
		this.returnType = returnType;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}	
}
