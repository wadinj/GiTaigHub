package com.ww.model;

import java.util.ArrayList;
import java.util.List;

public class StructuredUserStory {

	private List<StructuredClass> classes;
	private List<StructuredClass> tests;
	
	public StructuredUserStory() {
		classes = new ArrayList<StructuredClass>();
		tests = new ArrayList<StructuredClass>();
	}

	public List<StructuredClass> getClasses() {
		return classes;
	}

	public void setClasses(List<StructuredClass> classes) {
		this.classes = classes;
	}

	public List<StructuredClass> getTests() {
		return tests;
	}

	public void setTests(List<StructuredClass> tests) {
		this.tests = tests;
	}
	
}
