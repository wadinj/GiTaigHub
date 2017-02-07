package com.ww.core;

import java.io.IOException;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ww.model.ThesaurusResponse;
import com.ww.utils.GiTaigHubPropertiesUtils;

public class ThesaurusService {

	public static final String THESAURUS_URL = GiTaigHubPropertiesUtils.getInstance().getProperties(GiTaigHubPropertiesUtils.THESAURUS_URL);
	
	/**
	 * Return synonymous of specific word
	 * @param word which you want to retrieve synonymous
	 * @return verbs and nouns synonymous
	 * Example of use : System.out.println(new ThesaurusService().getSynonymousOfWord("can").getVerb().getSyn().get(0));
	 */
	public ThesaurusResponse getSynonymousOfWord(String word) {
		ObjectMapper mapper = new ObjectMapper();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response;
		String requestUri = THESAURUS_URL + word + "/json";
		System.out.println("Request : " + requestUri);
		
		response = restTemplate.exchange(requestUri,HttpMethod.GET,null,String.class);
		
		try {
			return mapper.readValue(response.getBody(), ThesaurusResponse.class);
		} catch (IOException e) {
			/* NOTHING TO DO */
		}
		return null;
	}
}
