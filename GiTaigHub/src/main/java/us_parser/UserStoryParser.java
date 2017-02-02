package us_parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	private static String classPattern = "([A-Z][a-z0-9]+)+";
	Pattern classNamePattern = Pattern.compile(classPattern);
	
	public void parseUserStory(TaigaUserStory taigaUserStory) {
		structuredUserStory = new StructuredUserStory();
		List<StructuredClass> classes = getClassesFromUserStory(taigaUserStory);
		structuredUserStory.setClasses(classes);
		
//		for(StructuredClass structuredClass : classes) 
//			System.out.println(structuredClass.getName() + structuredClass.getMethods().size());
	}

	private List<StructuredClass> getClassesFromUserStory(TaigaUserStory taigaUserStory) {
		List<StructuredClass> structuredClasses = new ArrayList<StructuredClass>();

		List<String> statements = Arrays.asList(taigaUserStory.getDescription().split("\n"));
		Matcher matcher;

		for (String statement : statements) {
			statement = statement.trim();
			matcher = classNamePattern.matcher(statement);
			int i = 0;
			while (matcher.find()) {
				StructuredClass parsedClass = new StructuredClass(matcher.group().trim());
				if (!structuredClasses.contains(parsedClass)) {
					structuredClasses.add(parsedClass);
					StructuredConstrutor constructor = new StructuredConstrutor(parsedClass.getName(), null, null, null, new ArrayList<String>());
					parsedClass.addConstructor(constructor);
				}
				int index = getIndexOfClass(structuredClasses, matcher.group().trim());
				if(i++ == 0) //si c'est la première classe rencontrée dans le statement
					if(statement.contains(Keywords.HAS.getName()))
						handleStatementAttribut(statement, structuredClasses.get(index));
					else
						handleStatementMethod(structuredClasses, statement, structuredClasses.get(index));
			}
		}
		return structuredClasses;
	}

	private int getIndexOfClass(List<StructuredClass> structuredClasses, String className) {
		int index = 0;
		for(int j = 0; j < structuredClasses.size(); j++)
			if(structuredClasses.get(j).getName().equals(className))
				index = j;
		return index;
	}

	private void handleStatementAttribut(String statement, StructuredClass parsedClass) {
		String single = Keywords.HAS.getName() + Keywords.ONE.getName();
		String many = Keywords.HAS.getName() + Keywords.MANY.getName();;
		String none = Keywords.HAS.getName() + Keywords.NONE.getName();
		String then = Keywords.THEN.getName(); 

		String attributClassName = null;
		StructuredAttribut structuredAttribut = null;
		
		if (statement.contains(single)) {
			structuredAttribut = buildStructuredAttribut(statement, single, then, attributClassName);
			//créer un statement dans les constructeurs de la classe qui initialise la valeur en appelant le constructeur vide
			String attributInitializationstatement = "this." + structuredAttribut.getName() + " = new " + structuredAttribut.getType() + "();";
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

	private void handleStatementMethod(List<StructuredClass> structuredClasses, String statement, StructuredClass parsedClass) {
		StructuredMethod method = null;
		if (statement.contains(Keywords.CAN.getName()))
			method = getUserStoryMethod(statement);
		if(method != null && method.getName() != null && !method.getName().isEmpty())
			parsedClass.addMethod(method);
		else 
			getMethod(structuredClasses, statement);
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
	
	private void getMethod(List<StructuredClass> structuredClasses, String statement) {
		String methodName = "";
		String className = "";
		Class<?> returnType = getMethodReturnType(statement);
		List<String> args = new ArrayList<String>();
	
		System.out.println(statement + returnType);
		//choper la classe dans laquelle on met la méthode
		Matcher matcher, matcherMethodName;
		matcher = classNamePattern.matcher(statement);
		if(matcher.find())
			className = matcher.group();
		else 
			return;
		//choper ses arguments (les classes suivantes sur le statement)
		while(matcher.find())
			args.add(matcher.group().trim() + " " + StringUtils.uncapitalize(matcher.group().trim()));
		String[] argsArray = new String[args.size()];
		args.toArray(argsArray);
		
		//choper le nom de la méthode patternmethode (entre 2 classes ou entre classes et then si y'a rien entre la méthode et then)
		String stmt = statement.substring(Keywords.IF.getName().length(), statement.length() - Keywords.THEN.getName().length());
		
		Pattern patternMethodName = Pattern.compile(" ([a-z0-9])+ ");
		matcherMethodName = patternMethodName.matcher(stmt);
		
		while(matcherMethodName.find()) {
			methodName = matcherMethodName.group().trim();
		}
		StructuredMethod structuredMethod = new StructuredMethod(methodName, returnType, argsArray);
		StructuredClass newClass = new StructuredClass(className);
		if (!structuredClasses.contains(newClass)) {
			structuredClasses.add(newClass);
			StructuredConstrutor constructor = new StructuredConstrutor(newClass.getName(), null, null, null, new ArrayList<String>());
			newClass.addConstructor(constructor);
			newClass.addMethod(structuredMethod);
		} else
			structuredClasses.get(getIndexOfClass(structuredClasses, className)).addMethod(structuredMethod);
	}

	private Class<?> getMethodReturnType(String statement) {
		statement = statement.trim();
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
