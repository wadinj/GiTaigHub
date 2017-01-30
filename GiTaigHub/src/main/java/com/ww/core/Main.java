package com.ww.core;

public class Main {

	public static void main(String[] args) {
		String usernameTaiga = args[0];
		String passwordTaiga = args[1];
		String projectTaiga = args[2];
		TaigaService service = new TaigaService(usernameTaiga, passwordTaiga, projectTaiga);
		//TaigaUserStory us = service.getUserStory("1");
		//System.out.println(us.getDescription());
		System.out.println(service.getUserStory("1").getDescription());
	}

}
