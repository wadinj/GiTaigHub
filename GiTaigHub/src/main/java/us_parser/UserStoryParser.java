package us_parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ww.model.StructuredAttribut;
import com.ww.model.StructuredClass;
import com.ww.model.StructuredConstrutor;
import com.ww.model.StructuredMethod;
import com.ww.model.StructuredUserStory;
import com.ww.utils.StringUtils;

public class UserStoryParser {
	private StructuredUserStory structuredUserStory;
	
	public void parseUserStory(TaigaUserStory taigaUserStory) {
		// System.out.println(taigaUserStory.getDescription());
		// choper le nom de la classe avec le can et cr�er une m�thode de �a

		// choper le nom de la m�thode, la classe concern�e, le type de retour
		// attendu (void en statement, boolean dans un if)
		// choper les statements
		// has => cr�ation d'une liste d'entity de [Classe] pour has Classe
		structuredUserStory = new StructuredUserStory();
		List<StructuredClass> classes = getClassesFromUserStory(taigaUserStory);
		structuredUserStory.setClasses(classes);
		
//		for(StructuredClass structuredClass : classes) 
//			System.out.println(structuredClass.getName() + structuredClass.getMethods().size());
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
				if (!classesMethodsMap.containsKey(parsedClass)) {
					classesMethodsMap.put(parsedClass, new ArrayList<StructuredMethod>());
					StructuredConstrutor constructor = new StructuredConstrutor(parsedClass.getName(), null, null, null, new ArrayList<String>());
					parsedClass.addConstructor(constructor);
				}
				if(i++ == 0)
					if(statement.contains(Keywords.HAS.getName()))
						handleStatementAttribut(classesMethodsMap, statement, parsedClass);
					else
						handleStatementMethod(classesMethodsMap, statement, parsedClass);
			}
		}
		for(StructuredClass structuredClass : classesMethodsMap.keySet()) {
			StructuredClass newClass = new StructuredClass(structuredClass.getName());
			newClass.addMethods(classesMethodsMap.get(structuredClass));
			newClass.addConstructors(structuredClass.getConstructors());
			newClass.addAttributs(structuredClass.getAttributs());
			structuredClasses.add(newClass);
		}
		return structuredClasses;
	}

	private void handleStatementAttribut(Map<StructuredClass, List<StructuredMethod>> classesMethodsMap,
			String statement, StructuredClass parsedClass) {
		String single = Keywords.HAS.getName() + Keywords.ONE.getName();
		String many = Keywords.HAS.getName() + Keywords.MANY.getName();;
		String none = Keywords.HAS.getName() + Keywords.NONE.getName();
		String then = Keywords.THEN.getName(); 

		String attributClassName = null;
		StructuredAttribut structuredAttribut = null;
		
		if (statement.contains(single)) {
			structuredAttribut = buildStructuredAttribut(statement, single, then, attributClassName);
			//créer un statement dans les constructeurs de la classe qui initialise la valeur en appelant le constructeur vide
			String attributInitializationstatement = structuredAttribut.getName() + " = new " + structuredAttribut.getType() + "();";
			for(StructuredConstrutor structuredConstrutor : parsedClass.getConstructors())
				structuredConstrutor.addStatement(attributInitializationstatement);
		} else
			if (statement.contains(many)) {
				attributClassName = getAttributClassName(statement, many, then, attributClassName);
				structuredAttribut = new StructuredAttribut("List<"+ attributClassName +">", StringUtils.uncapitalize(attributClassName + "s"));
			} else
				if(statement.contains(none))
					structuredAttribut = buildStructuredAttribut(statement, none, then, attributClassName);
					//on laisse l'attribut à null
		
		parsedClass.addAttribut(structuredAttribut);
	}

	private StructuredAttribut buildStructuredAttribut(String statement, String single, String then,
			String attributClassName) {
		StructuredAttribut structuredAttribut;
		attributClassName = getAttributClassName(statement, single, then, attributClassName);
		structuredAttribut = new StructuredAttribut(attributClassName, StringUtils.uncapitalize(attributClassName));
		return structuredAttribut;
	}

	private String getAttributClassName(String statement, String many, String then, String attributClass) {
		Pattern pattern = Pattern.compile(many + "(.*)" + then);
		Matcher matcher = pattern.matcher(statement);
		
		if (matcher.find())
			attributClass = statement.substring(statement.indexOf(many) + (many).length(), statement.length() - then.length()).trim();
		return attributClass;
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
		String can = Keywords.CAN.getName();
		String to = Keywords.TO.getName();
		
		if (statement.contains(to)) {
			patternMethod = Pattern.compile(can + "(.*)" + to);
			args[0] = statement.substring(statement.indexOf(to) + to.length()).trim();
		}
		else
			patternMethod = Pattern.compile(can + "(.*)");
		Matcher matcherMethod = patternMethod.matcher(statement);
		while (matcherMethod.find())
			methodName = StringUtils.stringAsMethodName(matcherMethod.group().substring(can.length()));
		StructuredMethod structuredMethod = new StructuredMethod(methodName, returnType, args);
		
		return structuredMethod;
	}
	
	private StructuredMethod getMethod(String statement) {
		String methodName = "";
		Class<?> returnType = getMethodReturnType(statement);
		System.out.println(statement);
		
		
		
		
		StructuredMethod structuredMethod = new StructuredMethod(methodName, returnType, null);
		
		return structuredMethod;
	}

	private Class<?> getMethodReturnType(String statement) {
		statement.replaceAll("\t", "");
		if (statement.startsWith(Keywords.IF.getName()))
			return Boolean.class;
		else
			return Void.class;
	}

	public StructuredUserStory getStructuredUserStory() {
		return structuredUserStory;
	}

	public void setStructuredUserStory(StructuredUserStory structuredUserStory) {
		this.structuredUserStory = structuredUserStory;
	}

}
