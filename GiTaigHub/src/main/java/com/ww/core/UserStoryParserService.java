package com.ww.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ww.model.AbstractStructuredMethod;
import com.ww.model.StructuredAttribut;
import com.ww.model.StructuredClass;
import com.ww.model.StructuredConstrutor;
import com.ww.model.StructuredMethod;
import com.ww.model.StructuredUserStory;
import com.ww.model.TaigaUserStory;
import com.ww.model.ThesaurusResponse;
import com.ww.utils.Keywords;
import com.ww.utils.StringUtils;

public class UserStoryParserService {
	private StructuredUserStory structuredUserStory;
	private static String classPattern = "([A-Z][a-z0-9]+)+";
	private Pattern classNamePattern = Pattern.compile(classPattern);
	private StructuredClass userStoryClass;
	private String userStoryArgClassName;

	public void parseUserStory(TaigaUserStory taigaUserStory) {
		structuredUserStory = new StructuredUserStory();
		List<StructuredClass> classes = getClassesDataFromUserStory(taigaUserStory);
		structuredUserStory.setClasses(classes);

		List<StructuredClass> tests = buildTestCase(taigaUserStory, structuredUserStory);
		structuredUserStory.setTests(tests);
	}

	private List<StructuredClass> buildTestCase(TaigaUserStory taigaUserStory,
			StructuredUserStory structuredUserStory) {
		List<String> classesNameToInitialize = new ArrayList<String>();
		List<StructuredClass> tests = new ArrayList<StructuredClass>();
		StructuredClass structuredTestClass = new StructuredClass(userStoryClass.getName() + "Test");
		List<String> statements = Arrays.asList(taigaUserStory.getDescription().split("\n"));
		StructuredMethod structuredTest = new StructuredMethod(
				StringUtils.stringAsMethodName(statements.get(0)) + "Test", void.class, new String[] {});
		structuredTest.setAnnotation(org.junit.Test.class);

		for (String statement : statements) {
			statement = statement.trim();
			if (!statement.contains(Keywords.CAN.getName())) {
				boolean isASimpleStatement = true;
				for (Keywords keyword : Keywords.values())
					if (statement.startsWith(keyword.getName()))
						isASimpleStatement = false;
				if (isASimpleStatement) {
					handleInitializationNeed(classesNameToInitialize, userStoryClass.getName());
					Matcher matcher;
					matcher = classNamePattern.matcher(statement);
					if(!matcher.find()) {
						extractSimpleAssertion(structuredTest, statement);
					} else { 
						extractComplexAssertion(classesNameToInitialize, structuredTest, statement, matcher);
					}
				}
				if (statement.startsWith(Keywords.IF.getName())) {
					structuredTest.addStatement("\nif(");
					structuredTest
							.addStatement(buildIfCondition(structuredUserStory,
									statement.substring(Keywords.IF.getName().length(),
											statement.length() - Keywords.THEN.getName().length()),
									classesNameToInitialize));
					structuredTest.addStatement(") {\n");
				}
				if (statement.startsWith(Keywords.ELSE.getName())) {
					structuredTest.addStatement("} else {");
				}
				if (statement.startsWith(Keywords.ENDIF.getName())) {
					structuredTest.addStatement("\n}\n");
				}
			}
		}
		for (String s : classesNameToInitialize) {
			String initStatement = s + " " + StringUtils.uncapitalize(s) + " = new " + s + "();\n";
			structuredTest.getStatements().add(0, initStatement);
		}
		structuredTestClass.addMethod(structuredTest);
		// for(String s : structuredTest.getStatements())
		// System.out.print(s);
		tests.add(structuredTestClass);
		return tests;
	}

	private void extractComplexAssertion(List<String> classesNameToInitialize, StructuredMethod structuredTest, String statement, Matcher matcher) {
		String testStatement = "\nassertTrue(" + StringUtils.uncapitalize(userStoryClass.getName()) + ".";
		
		Pattern patternActionMethod;
		Matcher matcherActionMethod;
		String testName = "";
		String[] args = new String[matcher.groupCount() + 1];
		int argsIndex = 0;
		int startIndex = 0;
		do {
			patternActionMethod = Pattern.compile("( " + matcher.group() + ")");
			matcherActionMethod = patternActionMethod.matcher(statement);
			if(matcherActionMethod.find())
				testName += statement.substring(startIndex, statement.indexOf(matcherActionMethod.group()));
			startIndex = statement.indexOf(matcher.group()) + matcher.group().length();
			args[argsIndex++] = matcher.group().trim();
		} while (matcher.find());

		testStatement += StringUtils.stringAsMethodName(testName) + "(";
		for(int i = 0; i < args.length; i++) {
			handleInitializationNeed(classesNameToInitialize, args[i]);
			testStatement += StringUtils.uncapitalize(args[i]);
			if((args.length == 2 && i == 0) || (i > 0) && i < args.length - 1)
				testStatement += ", ";
		}
			
		testStatement += "));\n";
		structuredTest.addStatement(testStatement);
	}

	private void extractSimpleAssertion(StructuredMethod structuredTest, String statement) {
		String testStatement = "\nassertTrue(" + StringUtils.uncapitalize(userStoryClass.getName()) + "."
				+ StringUtils.stringAsMethodName(statement) + "(";
		if (userStoryArgClassName != null && !userStoryArgClassName.isEmpty())
			testStatement += StringUtils.uncapitalize(userStoryArgClassName);
		testStatement += "));\n";
		structuredTest.addStatement(testStatement);
	}

	private String buildIfCondition(StructuredUserStory structuredUserStory, String statement,
			List<String> classesNameToInitialize) {
		String single = Keywords.HAS.getName() + Keywords.ONE.getName();
		String many = Keywords.HAS.getName() + Keywords.MANY.getName();
		String none = Keywords.HAS.getName() + Keywords.NONE.getName();

		String ifCondition = "";
		Matcher matcher;
		matcher = classNamePattern.matcher(statement);
		if (!statement.contains(Keywords.HAS.getName())) {
			if (statement.contains(Keywords.LESS.getName()) || statement.contains(Keywords.INFERIOR.getName())) {
				ifCondition += buildLessThanStatement(statement, classesNameToInitialize);
			} else {
				if (statement.contains(Keywords.MORE.getName()) || statement.contains(Keywords.SUPERIOR.getName())) {
					ifCondition += buildMoreThanStatement(statement, classesNameToInitialize);
				} else {
					if (statement.contains(Keywords.EQUALS.getName()) || statement.contains(Keywords.IS_SET_ON.getName())
							|| statement.contains(Keywords.IS.getName())) {
						ifCondition += buildEqualsStatement(statement, classesNameToInitialize);
					} else {
						StructuredMethod method = getMethod(structuredUserStory.getClasses(), statement);
						if (matcher.find()) {
							handleInitializationNeed(classesNameToInitialize, matcher.group());
							ifCondition += StringUtils.uncapitalize(matcher.group()) + "." + method.getName() + "(";
						}
						for (int i = 0; i < method.getArgs().length; i++) {
							if (i > 0)
								ifCondition += ", ";
							ifCondition += StringUtils.uncapitalize(method.getArgs()[i]);
						}
						ifCondition += ")";
					}
				}
			}
		} else {
			if (matcher.find()) {
				handleInitializationNeed(classesNameToInitialize, matcher.group());
				ifCondition += StringUtils.uncapitalize(matcher.group());
			}
			if (matcher.find())
				ifCondition += ".get" + matcher.group() + "()";
			if (statement.contains(single) || statement.contains(many))
				ifCondition += " != null";
			if (statement.contains(none))
				ifCondition += " == null";
		}
		return ifCondition;
	}

	private String buildLessThanStatement(String statement, List<String> classesNameToInitialize) {
		String lessKeyword = null;
		if (statement.contains(Keywords.LESS.getName()))
			lessKeyword = Keywords.LESS.getName();
		else
			lessKeyword = Keywords.INFERIOR.getName();
		String condition = buildAttributAccess(statement, lessKeyword, classesNameToInitialize);
		condition += " < ";
		String valueTest = statement.substring(statement.indexOf(lessKeyword));
		condition += valueTest.substring(lessKeyword.length());
		return condition;
	}

	private String buildEqualsStatement(String statement, List<String> classesNameToInitialize) {
		String or = Keywords.OR.getName();
		String equalsKeyword = null;
		boolean needToUseEquals = false;
		if (statement.contains(Keywords.IS_SET_ON.getName())) {
			equalsKeyword = Keywords.IS_SET_ON.getName();
			needToUseEquals = true;
		} else if (statement.contains(Keywords.IS.getName())) {
			equalsKeyword = Keywords.IS.getName();
			needToUseEquals = true;
		} else
			equalsKeyword = Keywords.EQUALS.getName();
		String attributAccess = buildAttributAccess(statement, equalsKeyword, classesNameToInitialize);
		String condition = "";
		String statementValueTest = statement.substring(statement.indexOf(equalsKeyword) + equalsKeyword.length());
		int nbOfOr = org.springframework.util.StringUtils.countOccurrencesOf(statementValueTest, or);
		do {
			String valueTest = null;
			if(nbOfOr > 0) {
				valueTest = statementValueTest.substring(0, statementValueTest.indexOf(or));
				statementValueTest = statementValueTest.substring(statementValueTest.indexOf(or) + or.length());
			}
			else 
				valueTest = statementValueTest;
			condition += attributAccess;
			if (needToUseEquals) {
				condition += ".equals(";
				condition += "\"" + valueTest + "\"";
				condition += ")";
			} else {
				condition += " = ";
				condition += valueTest.substring(equalsKeyword.length());
			}
			if(nbOfOr-- > 0)
				condition += " || ";
		} while(nbOfOr >= 0);
		return condition;
	}

	private String buildMoreThanStatement(String statement, List<String> classesNameToInitialize) {
		String moreKeyword = null;
		if (statement.contains(Keywords.MORE.getName()))
			moreKeyword = Keywords.MORE.getName();
		else
			moreKeyword = Keywords.SUPERIOR.getName();
		String condition = buildAttributAccess(statement, moreKeyword, classesNameToInitialize);
		condition += " > ";
		String valueTest = statement.substring(statement.indexOf(moreKeyword));
		condition += valueTest.substring(moreKeyword.length());
		return condition;
	}

	private void handleInitializationNeed(List<String> classesNameToInitialize, String className) {
		if(className.isEmpty())
			return;
		boolean needToInitialize = true;
		for (String name : classesNameToInitialize)
			if (name.equals(className))
				needToInitialize = false;
		if (needToInitialize)
			classesNameToInitialize.add(className);
	}

	private String buildAttributAccess(String statement, String testCondition, List<String> classesNameToInitialize) {
		String condition = "";
		String classParentName = "";
		String classChildName = "";
		String attributChildName = "";

		Pattern classParentPattern = Pattern.compile("(([A-Z][a-z0-9]+)+)'s");
		Pattern classChildPattern = Pattern.compile("('s ([A-Z][a-z0-9]+)+)");
		Pattern attributChildPattern = Pattern.compile("([A-Z][a-z0-9]+)" + "(.*)" + testCondition);
		Matcher classParentMatcher = classParentPattern.matcher(statement);
		Matcher classChildMatcher = classChildPattern.matcher(statement);
		Matcher attributChildMatcher = attributChildPattern.matcher(statement);
		if (classParentMatcher.find())
			classParentName = classParentMatcher.group().substring(0, classParentMatcher.group().length() - 2);
		if (classChildMatcher.find())
			classChildName = classChildMatcher.group().substring(3);
		if (attributChildMatcher.find())
			attributChildName = attributChildMatcher.group().substring(
					classParentName.length() + classChildName.length() + (classChildName.length() > 0 ? 4 : 3),
					attributChildMatcher.group().length() - testCondition.length());
		condition = StringUtils.uncapitalize(classParentName);
		if (classChildName != null && !classChildName.isEmpty())
			condition += ".get" + classChildName + "()";
		condition += ".get" + StringUtils.capitalize(attributChildName) + "()";
		handleInitializationNeed(classesNameToInitialize, classChildName);
		handleInitializationNeed(classesNameToInitialize, classParentName);
		return condition;
	}

	private List<StructuredClass> getClassesDataFromUserStory(TaigaUserStory taigaUserStory) {
		List<StructuredClass> structuredClasses = new ArrayList<StructuredClass>();
		List<String> statements = Arrays.asList(taigaUserStory.getDescription().split("\n"));
		Matcher matcher;

		for (String statement : statements) {
			statement = statement.trim();
			matcher = classNamePattern.matcher(statement);
			int i = 0;
			boolean isASimpleStatement = false;
			while (matcher.find()) {
//				isASimpleStatement = false;
				StructuredClass parsedClass = new StructuredClass(matcher.group().trim());
				if (!structuredClasses.contains(parsedClass)) {
					structuredClasses.add(0, parsedClass);
					StructuredConstrutor constructor = new StructuredConstrutor(parsedClass.getName(), null, null, null,
							new ArrayList<String>());
					parsedClass.addConstructor(constructor);
				}
				int index = getIndexOfClass(structuredClasses, matcher.group().trim());
				if (i++ == 0) // si c'est la première classe rencontrée dans le
								// statement
					if (statement.contains(Keywords.HAS.getName()))
						handleStatementAttribut(statement, structuredClasses.get(index), Keywords.HAS.getName());
					else 
						if(statement.contains(Keywords.OWNS.getName())) {
							handleStatementAttributOwn(statement, structuredClasses.get(index), structuredClasses);
						}
						else {
							String hasSynnonym = matchASynonym(statement, Keywords.HAS.getName());
							if (hasSynnonym != null)
								handleStatementAttribut(statement, structuredClasses.get(index), hasSynnonym);
							else
								handleStatementMethod(structuredClasses, statement, structuredClasses.get(index));
						}
			}
			for (Keywords keyword : Keywords.values())
				if (statement.contains(keyword.getName()) && !keyword.equals(Keywords.TO))
					isASimpleStatement = true;
			if (!isASimpleStatement) {
				extractActionMethod(structuredClasses, statement);
			}
		}
		
		for(StructuredClass classe : structuredClasses)
			for(StructuredAttribut attribut : classe.getAttributs())
				for(StructuredClass otherClass : structuredClasses)
					if(otherClass.getName().equals(StringUtils.capitalize(attribut.getName())))
						attribut.setType(otherClass.getName());
		return structuredClasses;
	}

	private void handleStatementAttributOwn(String statement, StructuredClass structuredClass, List<StructuredClass> structuredClasses) {
			String classParentName = "";
			String classChildName = "";
			String attributChildName = "";
			String testCondition = "";
			
			if (statement.contains(Keywords.EQUALS.getName()))
					testCondition = Keywords.EQUALS.getName();
			else if(statement.contains(Keywords.IS.getName()))
				testCondition = Keywords.IS.getName();
			else 
				testCondition = Keywords.IS_SET_ON.getName();

			Pattern classParentPattern = Pattern.compile("(([A-Z][a-z0-9]+)+)'s");
			Pattern classChildPattern = Pattern.compile("('s ([A-Z][a-z0-9]+)+)");
			Pattern attributChildPattern = Pattern.compile("([A-Z][a-z0-9]+)" + "(.*)" + testCondition);
			Matcher classParentMatcher = classParentPattern.matcher(statement);
			Matcher classChildMatcher = classChildPattern.matcher(statement);
			Matcher attributChildMatcher = attributChildPattern.matcher(statement);
			if (classParentMatcher.find())
				classParentName = classParentMatcher.group().substring(0, classParentMatcher.group().length() - 2);
			if (classChildMatcher.find())
				classChildName = classChildMatcher.group().substring(3);
			if (attributChildMatcher.find())
				attributChildName = attributChildMatcher.group().substring(
						classParentName.length() + classChildName.length() + 3,
						attributChildMatcher.group().length() - testCondition.length());
			String type = "String";
			if(statement.matches(".*\\d+.*"))
				type = "Integer";
			if(statement.matches(".*\\d\\.\\d+.*"))
				type = "Double";
			StructuredAttribut structuredAttribut = new StructuredAttribut(type, attributChildName.trim());
			String classNameToAddAttribute = "";
			if(classChildName != null  && !classChildName.isEmpty())
				classNameToAddAttribute = classChildName;
			else 
				classNameToAddAttribute = classParentName;
			for(StructuredClass classe : structuredClasses)
				if(classe.getName().equals(classNameToAddAttribute))
					if(!classe.getAttributs().contains(structuredAttribut))
						classe.addAttribut(structuredAttribut);
			
	}

	private String matchASynonym(String statement, String name) {
		// chercher les synonymes de name, et tester à chaque fois si le
		// statement le contains, si oui, renvoyer
		String matched = null;
		ThesaurusResponse thesaurusResponse = new ThesaurusService().getSynonymousOfWord(name.trim());
		if (thesaurusResponse != null && thesaurusResponse.getVerb() != null) {
			for (String syn : thesaurusResponse.getVerb().getSyn())
				if (statement.contains(" " + syn + " "))
					matched = " " + syn + " ";
		}
		return matched;
	}

	private void extractActionMethod(List<StructuredClass> structuredClasses, String statement) {
		Matcher matcher;
		matcher = classNamePattern.matcher(statement);
		if(!matcher.find()) {
			extractSimpleActionMethod(structuredClasses, statement);
		} else { 
			extractComplexActionMethod(structuredClasses, statement, matcher);
		}
	}

	private void extractComplexActionMethod(List<StructuredClass> structuredClasses, String statement, Matcher matcher) {
		Pattern patternActionMethod;
		Matcher matcherActionMethod;
		String methodName = "";
		String[] args = new String[matcher.groupCount() + 1];
		int argsIndex = 0;
		int startIndex = 0;
		do {
			patternActionMethod = Pattern.compile("( " + matcher.group() + ")");
			matcherActionMethod = patternActionMethod.matcher(statement);
			if(matcherActionMethod.find())
				methodName += statement.substring(startIndex, statement.indexOf(matcherActionMethod.group()));
			startIndex = statement.indexOf(matcher.group()) + matcher.group().length();
			args[argsIndex++] = matcher.group().trim();
		} while (matcher.find());
		String actionMethodName = StringUtils.stringAsMethodName(methodName);
		userStoryClass.addMethod(new StructuredMethod(actionMethodName, getMethodReturnType(statement), args));
	}

	private void extractSimpleActionMethod(List<StructuredClass> structuredClasses, String statement) {
		boolean methodAlreadyParsed = false;
		String actionMethodName = StringUtils.stringAsMethodName(statement);
		for (AbstractStructuredMethod method : userStoryClass.getMethods())
			if (method.getName().equals(actionMethodName))
				methodAlreadyParsed = true;
		if (!methodAlreadyParsed) {
			StructuredClass classArg = getClassNamed(structuredClasses, userStoryArgClassName);
			StructuredMethod newMethod = null;
			if (classArg == null)
				newMethod = new StructuredMethod(actionMethodName, getMethodReturnType(statement), null);
			else {
				String[] args = new String[1];
				args[0] = classArg.getName();
				newMethod = new StructuredMethod(actionMethodName, getMethodReturnType(statement), args);
			}
			userStoryClass.addMethod(newMethod);
		}
	}

	private int getIndexOfClass(List<StructuredClass> structuredClasses, String className) {
		int index = 0;
		for (int j = 0; j < structuredClasses.size(); j++)
			if (structuredClasses.get(j).getName().equals(className))
				index = j;
		return index;
	}

	private StructuredClass getClassNamed(List<StructuredClass> structuredClasses, String className) {
		for (StructuredClass structuredClass : structuredClasses)
			if (className.equals(structuredClass.getName()))
				return structuredClass;
		return null;
	}

	private void handleStatementAttribut(String statement, StructuredClass parsedClass, String hasKeyword) {
		String single = hasKeyword + Keywords.ONE.getName();
		String many = hasKeyword + Keywords.MANY.getName();
		String none = hasKeyword + Keywords.NONE.getName();
		String then = Keywords.THEN.getName();

		String attributClassName = null;
		StructuredAttribut structuredAttribut = null;

		if (statement.contains(single)) {
			structuredAttribut = buildStructuredAttribut(statement, single, then, attributClassName);
			// créer un statement dans les constructeurs de la classe qui
			// initialise la valeur en appelant le constructeur vide
			String attributInitializationstatement = "this." + structuredAttribut.getName() + " = new "
					+ structuredAttribut.getType() + "();";
			for (StructuredConstrutor structuredConstrutor : parsedClass.getConstructors())
				structuredConstrutor.addStatement(attributInitializationstatement);
		} else if (statement.contains(many)) {
			attributClassName = getAttributClassName(statement, many, then, attributClassName);
			structuredAttribut = new StructuredAttribut("List<" + attributClassName + ">",
					StringUtils.uncapitalize(attributClassName + "s"));
		} else if (statement.contains(none))
			structuredAttribut = buildStructuredAttribut(statement, none, then, attributClassName);
		// on laisse l'attribut à null

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
			attributClass = statement
					.substring(statement.indexOf(many) + (many).length(), statement.length() - then.length()).trim();
		return attributClass;
	}

	private void handleStatementMethod(List<StructuredClass> structuredClasses, String statement,
			StructuredClass parsedClass) {
		StructuredMethod method = null;
		if (statement.contains(Keywords.CAN.getName())) {
			method = getUserStoryMethod(statement);
			userStoryClass = parsedClass;
		}
		if (method != null && method.getName() != null && !method.getName().isEmpty())
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
			userStoryArgClassName = args[0];
		} else
			patternMethod = Pattern.compile(can + "(.*)");
		Matcher matcherMethod = patternMethod.matcher(statement);
		while (matcherMethod.find())
			methodName = StringUtils
					.stringAsMethodName(
							matcherMethod.group().substring(can.length(), matcherMethod.group().length() - to.length()))
					.trim();
		StructuredMethod structuredMethod = new StructuredMethod(methodName, returnType, args);

		return structuredMethod;
	}

	private StructuredMethod getMethod(List<StructuredClass> structuredClasses, String statement) {
		String methodName = "";
		String className = "";
		Class<?> returnType = getMethodReturnType(statement);
		List<String> args = new ArrayList<String>();

		// choper la classe dans laquelle on met la méthode
		Matcher matcher, matcherMethodName;
		matcher = classNamePattern.matcher(statement);
		if (matcher.find())
			className = matcher.group();
		else
			return null;
		// choper ses arguments (les classes suivantes sur le statement)
		while (matcher.find())
			args.add(matcher.group().trim());
		String[] argsArray = new String[args.size()];
		args.toArray(argsArray);

		// choper le nom de la méthode patternmethode (entre 2 classes ou entre
		// classes et then si y'a rien entre la méthode et then)
		String stmt = statement.substring(Keywords.IF.getName().length(),
				statement.length() - Keywords.THEN.getName().length());

		Pattern patternMethodName = Pattern.compile(" ([a-z0-9])+ ");
		matcherMethodName = patternMethodName.matcher(stmt);

		while (matcherMethodName.find()) {
			methodName = matcherMethodName.group().trim();
		}
		StructuredMethod structuredMethod = new StructuredMethod(methodName, returnType, argsArray);
		StructuredClass newClass = new StructuredClass(className);
		if (!structuredClasses.contains(newClass)) {
			structuredClasses.add(newClass);
			StructuredConstrutor constructor = new StructuredConstrutor(newClass.getName(), null, null, null,
					new ArrayList<String>());
			newClass.addConstructor(constructor);
			newClass.addMethod(structuredMethod);
		} else
			structuredClasses.get(getIndexOfClass(structuredClasses, className)).addMethod(structuredMethod);
		return structuredMethod;
	}

	private Class<?> getMethodReturnType(String statement) {
		// statement = statement.trim();
		// if (statement.startsWith(Keywords.IF.getName()))
		// return Boolean.class;
		// else
		// return Void.class;
		return Boolean.class;
	}

	public StructuredUserStory getStructuredUserStory() {
		return structuredUserStory;
	}

	public void setStructuredUserStory(StructuredUserStory structuredUserStory) {
		this.structuredUserStory = structuredUserStory;
	}

}
