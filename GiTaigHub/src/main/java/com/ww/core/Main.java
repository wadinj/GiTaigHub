package com.ww.core;

import com.ww.model.StructuredUserStory;

public class Main {

	public static void main(String[] args) {
		String usernameTaiga = args[0];
		String passwordTaiga = args[1];
		String projectTaiga = args[2];
		TaigaService service = new TaigaService(usernameTaiga, passwordTaiga, projectTaiga);
		UserStoryParserService userStoryParser = new UserStoryParserService();
		userStoryParser.parseUserStory(service.getUserStory("1"));
		StructuredUserStory structuredUs = userStoryParser.getStructuredUserStory();
//		CodeGeneratorService generator = new CodeGeneratorService();
//		generator.generateCodeFromStructuredUserStory(structuredUs);
	}

}
