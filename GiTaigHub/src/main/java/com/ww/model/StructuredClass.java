package com.ww.model;

import java.util.ArrayList;
import java.util.List;

public class StructuredClass {

	private String name;
	private List<AbstractStructuredMethod> methods;
	private List<StructuredConstrutor> constructors;
	private List<StructuredAttribut> attributs;
	
	public StructuredClass(String name) {
		this.name = name;
		methods = new ArrayList<AbstractStructuredMethod>();
		constructors = new ArrayList<StructuredConstrutor>();
		attributs = new ArrayList<StructuredAttribut>();
	}

	public StructuredClass() {
		methods = new ArrayList<AbstractStructuredMethod>();
		constructors = new ArrayList<StructuredConstrutor>();
		attributs = new ArrayList<StructuredAttribut>();
	}

	public void addMethod(AbstractStructuredMethod method) {
		boolean isNotAlreadyAClassMethod = true;
		for(AbstractStructuredMethod structuredMethod : methods)
			if(structuredMethod.equals(method))
				isNotAlreadyAClassMethod = false;
		if(isNotAlreadyAClassMethod)
			methods.add(method);
	}
	
	public void addMethods(List<AbstractStructuredMethod> methods) {
		this.methods.addAll(methods);
	}
	
	public void addConstructors(List<StructuredConstrutor> constructors) {
		this.constructors.addAll(constructors);
	}
	
	public void addAttributs(List<StructuredAttribut> attributs) {
		this.attributs.addAll(attributs);
	}

	public void addConstructor(StructuredConstrutor constructor) {
		constructors.add(constructor);
	}

	public void addAttribut(StructuredAttribut attribut) {
		attributs.add(attribut);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<AbstractStructuredMethod> getMethods() {
		return methods;
	}
	
	public List<StructuredConstrutor> getConstructors() {
		return constructors;
	}

	public List<StructuredAttribut> getAttributs() {
		return attributs;
	}

	public void setConstructors(List<StructuredConstrutor> constructors) {
		this.constructors = constructors;
	}


	public void setAttributs(List<StructuredAttribut> attributs) {
		this.attributs = attributs;
	}

	public void setMethods(List<AbstractStructuredMethod> methods) {
		this.methods = methods;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		StructuredClass other = (StructuredClass) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
