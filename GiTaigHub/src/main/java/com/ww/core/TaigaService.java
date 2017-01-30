package com.ww.core;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ww.model.TaigaAuthentificationRequest;
import com.ww.model.TaigaAuthentificationResponse;
import com.ww.model.TaigaUserStory;

/**
 * Taiga Service which allows the user to request his taiga
 * @author Jo
 *
 */
public class TaigaService {

	private String token;
	private RestTemplate restTemplate;
	private final static String TAIGA_URI_AUTHENT = "https://api.taiga.io/api/v1/auth";
	private String projectReference;
	private final static String BASE_TAIGA_URI_GET_USERSTORY = "https://api.taiga.io/api/v1/userstories";
	public TaigaService(String usernameTaiga, String passwordTaiga,String projectReference) {
		restTemplate = new RestTemplate();
		TaigaAuthentificationRequest authentRequest = new TaigaAuthentificationRequest(usernameTaiga,passwordTaiga);
		TaigaAuthentificationResponse authentResponse = restTemplate.postForObject(TAIGA_URI_AUTHENT,authentRequest, TaigaAuthentificationResponse.class);
		this.token = authentResponse.getAuth_token();
		System.out.println(authentResponse.getUsername() + " " + token);
		this.projectReference = projectReference;
	}

	public String getUserStory(String ref) {
		HttpHeaders httpHeaders = new HttpHeaders(){
			{
				set("Content-Type", "application/json");
				set("Authorization", "Bearer " + token);
			}
		};
		ResponseEntity<String> response;
		String requestUri = BASE_TAIGA_URI_GET_USERSTORY + "?project=1";
		System.out.println("Request : " + requestUri);
		//String requestUri = "https://api.taiga.io/api/v1/userstories/by_ref?ref=1&project=1";
		response  = restTemplate.exchange(requestUri,HttpMethod.GET,new HttpEntity<Object>(httpHeaders),String.class);
		return response.getBody();
	}
}
