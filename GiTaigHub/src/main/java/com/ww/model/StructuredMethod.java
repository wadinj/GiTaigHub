package com.ww.model;

import java.util.ArrayList;
import java.util.List;

public class StructuredMethod extends AbstractStructuredMethod {

	private Class<?> returnType;


	public StructuredMethod(String name, Class<?> returnType, String[] args, Class<?> annotation, List<String> statements) {
		super(args, annotation, name, statements);
		this.returnType = returnType;
	}
	
	public StructuredMethod(String name, Class<?> returnType, String[] args, List<String> statements) {
		super(args, null, name, statements);
		this.returnType = returnType;
	}
	
	public StructuredMethod(String name, Class<?> returnType, String[] args) {
		super(args, null, name, new ArrayList<String>());
		this.returnType = returnType;
	}
	
	public Class<?> getReturnType() {
		return returnType;
	}
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}	
	
}
