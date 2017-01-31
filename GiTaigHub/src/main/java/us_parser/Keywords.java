package us_parser;

public enum Keywords {
	CAN(" can "),
	TO(" to "),
	HAS(" has "),
	IF("if "),
	THEN(" then"),
	ELSE("else"),
	ENDIF("endif");
	
	private String name;
	
	private Keywords(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
