package com.ww.core;

public class Main {

	public static void main(String[] args) {
		String usernameGitHub = args[0];
		String passwordGitHub = args[1];
		String repositoryGitHub = args[2];
		GithubParser github = new GithubParser(usernameGitHub,passwordGitHub,repositoryGitHub);
	}

}
