package us_parser;

public enum Keywords {
	CAN(" can "),
	TO(" to "),
	HAS(" has "),
	IF("IF "),
	THEN(" THEN"),
	ELSE("ELSE"),
	ENDIF("ENDIF"),
	ONE("a "),
	NONE("no "),
	MANY(" many "),
	LESS(" is less than "),
	MORE(" is more than "),
	SUPERIOR(" > "),
	INFERIOR(" < ");
	
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
