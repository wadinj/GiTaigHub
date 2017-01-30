package us_parser;

public class UserStoryData {

	private String spec = "marque : [Opel, Renault, BMW, Peugeot]\r\n" + 
			"modèle: [Corsa, Twingo, Série 1, 308]\r\n" + 
			"couleur: [Bleue, Rouge, Jaune, Noire, Blanche]";
	
	private Integer number = 1;
	
	private StatusEnum status = StatusEnum.READY_TO_TEST;
	
	private String title = "Création d'une voiture";

}
