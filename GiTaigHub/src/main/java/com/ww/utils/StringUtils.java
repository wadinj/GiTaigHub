package com.ww.utils;

import java.util.Arrays;
import java.util.List;

public class StringUtils {

	public static String stringAsMethodName(String line) {
		String name = "";
		List<String> words = Arrays.asList(line.split(" "));
		name += words.get(0).toLowerCase();
		for(int i = 1; i < words.size(); i++)
			name += capitalize(words.get(i));
		return name.trim();
	}

	public static String capitalize(final String word) {
		return Character.toUpperCase(word.charAt(0)) + word.substring(1);
	}
	
	public static String uncapitalize(final String word) {
		return Character.toLowerCase(word.charAt(0)) + word.substring(1);
	}
}
