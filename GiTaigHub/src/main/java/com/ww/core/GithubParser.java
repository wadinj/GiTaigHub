package com.ww.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import us_parser.TaigaUserStory;

public class GithubParser {

	/**
	 * Get all commit from a github repository from a taiga user story
	 * @param taigaUserStory the Taiga object which represents the user story
	 * @return list of commit
	 */
	private String repositoryName;
	private GitHubClient client;
	private RepositoryService repoService;
	public GithubParser(String username, String password, String repositoryName) {

		this.client = client;
		this.repositoryName = repositoryName;
	}
	public List<Commit> getCommitFromTaigaUs(TaigaUserStory taigaUserStory) {
		Repository repo = new Repository();
		repoService = new RepositoryService(client);
		
		return new ArrayList<Commit>();
	}
	private Repository getSpecificRepository(String name) {
		try {
			List<Repository> repositories = repoService.getRepositories();
			for(Repository repository : repositories) {
				if(repository.getName().equals(name)) {
					return repository;
				}
			}
		} catch (IOException e) {
			System.err.println("Problem to retrieve repository from GitHub");
		}
		return null;
	}
	
}
