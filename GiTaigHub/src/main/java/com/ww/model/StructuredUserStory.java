package com.ww.model;

import java.util.ArrayList;
import java.util.List;

public class StructuredUserStory {

	private List<StructuredClass> classes;
	
	public StructuredUserStory() {
		classes = new ArrayList<StructuredClass>();
	}

	public List<StructuredClass> getClasses() {
		return classes;
	}

	public void setClasses(List<StructuredClass> classes) {
		this.classes = classes;
	}
	
}
