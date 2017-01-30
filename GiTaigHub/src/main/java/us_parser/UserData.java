package us_parser;

import us_parser.StatusEnum;

public class UserData {

	private String spec = "marque : [Opel, Renault, BMW, Peugeot]\r\n" + 
			"mod�le: [Corsa, Twingo, S�rie 1, 308]\r\n" + 
			"couleur: [Bleue, Rouge, Jaune, Noire, Blanche]";
	
	private Integer number = 1;
	
	private StatusEnum status = StatusEnum.READY_TO_TEST;
	
	private String title = "Cr�ation d'une voiture";

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
}
