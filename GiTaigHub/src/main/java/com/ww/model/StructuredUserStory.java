package com.ww.model;

import java.util.ArrayList;
import java.util.List;

public class StructuredUserStory {

	private List<StructuredClass> classes;
	private StructuredTest test;
	
	public StructuredUserStory() {
		classes = new ArrayList<StructuredClass>();
	}

	public List<StructuredClass> getClasses() {
		return classes;
	}

	public void setClasses(List<StructuredClass> classes) {
		this.classes = classes;
	}

	public StructuredTest getTest() {
		return test;
	}

	public void setTest(StructuredTest test) {
		this.test = test;
	}
	
}
