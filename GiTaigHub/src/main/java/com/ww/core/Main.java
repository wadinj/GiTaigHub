package com.ww.core;

import com.ww.model.StructuredUserStory;

import us_parser.UserStoryParser;

public class Main {

	public static void main(String[] args) {
		String usernameTaiga = args[0];
		String passwordTaiga = args[1];
		String projectTaiga = args[2];
		TaigaService service = new TaigaService(usernameTaiga, passwordTaiga, projectTaiga);
		UserStoryParser userStoryParser = new UserStoryParser();
		userStoryParser.parseUserStory(service.getUserStory("1"));
		StructuredUserStory structuredUs = userStoryParser.getStructuredUserStory();
//		CodeGeneratorService generator = new CodeGeneratorService();
//		generator.generateCodeFromStructuredUserStory(structuredUs);
		System.out.println(new ThesaurusService().getSynonymousOfWord("can"));
	}

}
