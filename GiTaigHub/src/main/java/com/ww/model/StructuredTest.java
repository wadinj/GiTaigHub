package com.ww.model;

import java.util.ArrayList;
import java.util.List;

public class StructuredTest {
	
	private List<String> statements;
	
	public StructuredTest() {
		this.statements = new ArrayList<String>();
	}

	public StructuredTest(List<String> statements) {
		super();
		this.statements = statements;
	}

	public List<String> getStatements() {
		return statements;
	}

	public void setStatements(List<String> statements) {
		this.statements = statements;
	}
	
	public void addStatement(String statement) {
		getStatements().add(statement);
	}

}
