package com.ww.model;

import java.util.List;

public class StructuredConstrutor extends AbstractStructuredMethod {

	public StructuredConstrutor(String name, String returnType, String[] args, Class<?> annotation, List<String> statements) {
		super(args, annotation, name, statements);
	}
}
