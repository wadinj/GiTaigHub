package com.ww.model;

public class StructuredAttribut {
	private Class<?> type;
	private String name;
	
	public StructuredAttribut(Class<?> type, String name) {
		super();
		this.type = type;
		this.name = name;
	}
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
