package com.ww.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaigaAuthentificationRequest {

	private String type;
	private String username;
	private String password;
	
	public TaigaAuthentificationRequest(String username, String password) {
		this.username = username;
		this.password = password;
		this.type = "normal";
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}


}
