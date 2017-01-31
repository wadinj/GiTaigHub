package com.ww.model;

import java.util.ArrayList;
import java.util.List;

public class StructuredClass {

	private List<StructuredMethod> methods;
	private List<StructuredConstrutor> constructors;
	
	public StructuredClass() {
		methods = new ArrayList<StructuredMethod>();
		constructors = new ArrayList<StructuredConstrutor>();
	}
	public void addMethod(StructuredMethod method) {
		methods.add(method);
	}
	public void addConstructor(StructuredConstrutor constructor) {
		constructors.add(constructor);
	}
}
