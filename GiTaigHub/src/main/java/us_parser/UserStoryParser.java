package us_parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ww.model.StructuredClass;
import com.ww.model.StructuredMethod;
import com.ww.model.StructuredUserStory;
import com.ww.utils.StringUtils;

public class UserStoryParser {
	private StructuredUserStory structuredUserStory;
	
	public void parseUserStory(TaigaUserStory taigaUserStory) {
		// System.out.println(taigaUserStory.getDescription());
		// choper le nom de la classe avec le can et créer une méthode de ça

		// choper le nom de la méthode, la classe concernée, le type de retour
		// attendu (void en statement, boolean dans un if)
		// choper les statements
		// has => création d'une liste d'entity de [Classe] pour has Classe
		structuredUserStory = new StructuredUserStory();
		List<StructuredClass> classes = getClassesFromUserStory(taigaUserStory);
		structuredUserStory.setClasses(classes);
		
		for(StructuredClass structuredClass : classes) 
			System.out.println(structuredClass.getName() + structuredClass.getMethods().size());
	}

	private List<StructuredClass> getClassesFromUserStory(TaigaUserStory taigaUserStory) {
		List<StructuredClass> structuredClasses = new ArrayList<StructuredClass>();
		Map<StructuredClass, List<StructuredMethod>> classesMethodsMap = new HashMap<StructuredClass, List<StructuredMethod>>();

		List<String> statements = Arrays.asList(taigaUserStory.getDescription().split("\n"));
		Pattern pattern = Pattern.compile("([A-Z][a-z0-9]+)+");
		Matcher matcher;

		for (String statement : statements) {
			matcher = pattern.matcher(statement);
			int i = 0;
			while (matcher.find()) {
				StructuredClass parsedClass = new StructuredClass(matcher.group().trim());
				if (!classesMethodsMap.containsKey(parsedClass))
					classesMethodsMap.put(parsedClass, new ArrayList<StructuredMethod>());
				if(i++ == 0)
					handleStatementMethod(classesMethodsMap, statement, parsedClass);
			}

		}
		for(StructuredClass structuredClass : classesMethodsMap.keySet()) {
			StructuredClass newClass = new StructuredClass(structuredClass.getName());
			newClass.addMethods(classesMethodsMap.get(structuredClass));
			structuredClasses.add(newClass);
		}
		return structuredClasses;
	}

	private void handleStatementMethod(Map<StructuredClass, List<StructuredMethod>> classesMethodsMap, String statement,
			StructuredClass parsedClass) {
		StructuredMethod method;
		if (statement.contains(Keywords.CAN.getName()))
			method = getUserStoryMethod(statement);
		else 
			method = getMethod(statement);
		if(method.getName() != null && !method.getName().isEmpty())
			classesMethodsMap.get(parsedClass).add(method);
	}

	private StructuredMethod getUserStoryMethod(String statement) {
		String methodName = "";
		Class<?> returnType = getMethodReturnType(statement);
		Pattern patternMethod = null;
		String[] args = new String[1];
		if (statement.contains(Keywords.TO.getName())) {
			patternMethod = Pattern.compile(Keywords.CAN.getName() + "(.*)" + Keywords.TO.getName());
			args[0] = statement.substring(statement.indexOf(Keywords.TO.getName()) + Keywords.TO.getName().length()).trim();
		}
		else
			patternMethod = Pattern.compile(Keywords.CAN.getName() + "(.*)");
		Matcher matcherMethod = patternMethod.matcher(statement);
		while (matcherMethod.find())
			methodName = StringUtils.stringAsMethodName(matcherMethod.group().substring(Keywords.CAN.getName().length()));
		StructuredMethod structuredMethod = new StructuredMethod(methodName, returnType, args);
		
		return structuredMethod;
	}
	
	private StructuredMethod getMethod(String statement) {
		String methodName = "";
		Class<?> returnType = getMethodReturnType(statement);
		StructuredMethod structuredMethod = new StructuredMethod(methodName, returnType, null);
		
		return structuredMethod;
	}

	private Class<?> getMethodReturnType(String statement) {
		if (statement.startsWith(Keywords.IF.getName()))
			return boolean.class;
		else
			return void.class;
	}

	public StructuredUserStory getStructuredUserStory() {
		return structuredUserStory;
	}

	public void setStructuredUserStory(StructuredUserStory structuredUserStory) {
		this.structuredUserStory = structuredUserStory;
	}

}
