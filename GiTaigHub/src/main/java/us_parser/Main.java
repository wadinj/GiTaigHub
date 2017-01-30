package us_parser;

import com.ww.model.TaigaUserStory;

public class Main {

	public static void main(String[] args) {
		UserStoryParser userStoryParser = new UserStoryParser();
		userStoryParser.parseUserStory(new TaigaUserStory());
	}

}
