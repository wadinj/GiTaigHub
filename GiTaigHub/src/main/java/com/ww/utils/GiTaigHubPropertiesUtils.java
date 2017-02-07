package com.ww.utils;

import java.util.ResourceBundle;

/**
 * Utility file to read properties
 * @author Jo
 *
 */
public class GiTaigHubPropertiesUtils {

	private static ResourceBundle resource;
	private static GiTaigHubPropertiesUtils instance;
	private static final String PROPERTIES_NAME = "giTaigHub";
	public static final String THESAURUS_URL = "thesaurusUrl";
	private GiTaigHubPropertiesUtils() {
		resource = ResourceBundle.getBundle(PROPERTIES_NAME);
	}

	public static GiTaigHubPropertiesUtils getInstance() {
		if(instance == null) {
			instance = new GiTaigHubPropertiesUtils();
		}
		return instance;
	}

	public String getProperties(String key) {
		return resource.getString(key);
	}
}
