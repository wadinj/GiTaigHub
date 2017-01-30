package com.ww.core;

import java.util.List;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.client.GitHubClient;

import com.ww.utils.GiTaigHubPropertiesUtils;

public class GithubParser {

	/**
	 * Get all commit from a github repository from a taiga user story
	 * @param taigaUserStory the Taiga object which represents the user story
	 * @return list of commit
	 */
	public GithubParser(String username, String password) {
		GitHubClient client = new GitHubClient();
		client.setCredentials(username,password);
	}
//	public List<Commit> getCommitFromTaigaUs(String taigaUserStory) {
//		
//	}
	
}
