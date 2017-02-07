package com.ww.core;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ww.model.ThesaurusResponse;
import com.ww.utils.GiTaigHubPropertiesUtils;

public class ThesaurusService {

	public static final String THESAURUS_URL = GiTaigHubPropertiesUtils.getInstance().getProperties(GiTaigHubPropertiesUtils.THESAURUS_URL);
	
	public ThesaurusResponse getSynonymousOfWord(String word) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ThesaurusResponse> response;
		String requestUri = THESAURUS_URL + word + "/json";
		System.out.println("Request : " + requestUri);
		response = restTemplate.exchange(requestUri,HttpMethod.GET,null,ThesaurusResponse.class);
		return response.getBody();
	}
}
