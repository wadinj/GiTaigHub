package com.ww.core;


import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.ww.model.AbstractStructuredMethod;
import com.ww.model.StructuredAttribut;
import com.ww.model.StructuredClass;
import com.ww.model.StructuredConstrutor;
import com.ww.model.StructuredUserStory;
import com.ww.utils.GiTaigHubPropertiesUtils;
import com.ww.utils.StringUtils;

public class CodeGeneratorService {

	private static final Logger LOGGER = Logger.getLogger(CodeGeneratorService.class);
	private static final String DIRECTORY_PATH_FOR_GENERATED_CLASS = "directoryGeneratedClass";
	private static final String PACKAGE_FOR_GENERATED_CLASS = "packageGeneratedClass";
	private String packageGeneratedClass;
	private File directoryGeneratedClass;
	private File packageGeneratedFile;
	private JavaFile javaFile;

	public CodeGeneratorService() {
		packageGeneratedClass = GiTaigHubPropertiesUtils.getInstance().getProperties(PACKAGE_FOR_GENERATED_CLASS);
		directoryGeneratedClass = new File(GiTaigHubPropertiesUtils.getInstance().getProperties(DIRECTORY_PATH_FOR_GENERATED_CLASS));
		packageGeneratedFile = new File(directoryGeneratedClass.getAbsoluteFile() + "\\" + packageGeneratedClass.replace('.', '\\'));
		try {
			FileUtils.cleanDirectory(packageGeneratedFile);
		} catch (IOException e) {
			LOGGER.warn("Cannot clean directory of generated class, be careful on dependencies issues");
		}
	}

	public void generateCodeFromStructuredUserStory(StructuredUserStory userStory) {
		createClassFromList(userStory.getClasses());
		createClassFromList(userStory.getTests());
	}

	private void createClassFromList(List<StructuredClass> classes) {
		TypeSpec.Builder currentClassBuilder;
		MethodSpec.Builder currentMethodBuilder;
		FieldSpec currentField;
		boolean isTestClass = false;
		for(StructuredClass structuredClass : classes) {
			currentClassBuilder = TypeSpec.classBuilder(structuredClass.getName()).addModifiers(Modifier.PUBLIC);
			// Building all methods
			for(AbstractStructuredMethod structuredMethod : structuredClass.getMethods()) {
				LOGGER.debug("Method => " + structuredMethod.getName());
				currentMethodBuilder = MethodSpec.methodBuilder(structuredMethod.getName()).addModifiers(Modifier.PUBLIC);
				if(structuredMethod.getAnnotation() != null) {
					currentMethodBuilder.addAnnotation(structuredMethod.getAnnotation());
				}
				if(structuredMethod.getArgs() != null && structuredMethod.getArgs().length > 0) {
					for(String args : structuredMethod.getArgs()) {
						LOGGER.debug("==> Args : " + args);
						currentMethodBuilder.addParameter(ClassName.get(packageGeneratedClass, args.split(" ")[0]), StringUtils.uncapitalize(args.trim()));
					}
				}
				currentMethodBuilder.returns(structuredMethod.getReturnType());
				if(structuredMethod.getStatements() != null && structuredMethod.getStatements().size() > 0) {
					for(String statement : structuredMethod.getStatements()) {
						currentMethodBuilder.addCode(statement);
					}
					isTestClass = true;
				} else {
					currentMethodBuilder.addComment("TODO: not yet implemented \n", new Object[] {});
					currentMethodBuilder.addCode("return null;\n");
				}
				currentClassBuilder.addMethod(currentMethodBuilder.build());
			}
			// Building constructors
			for(StructuredConstrutor structuredConstructor : structuredClass.getConstructors()) {
				currentMethodBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
				if(structuredConstructor.getAnnotation() != null) {
					currentMethodBuilder.addAnnotation(structuredConstructor.getAnnotation());
				}
				if(structuredConstructor.getArgs() != null && structuredConstructor.getArgs().length > 0) {
					for(String args : structuredConstructor.getArgs()) {
						currentMethodBuilder.addParameter(ClassName.get(packageGeneratedClass, args.split(" ")[0]), StringUtils.uncapitalize(args.trim()));
					}
				}
				currentMethodBuilder.addComment("TODO: not yet implemented \n", new Object[] {});
				currentClassBuilder.addMethod(currentMethodBuilder.build());
			}
			// Adding attributes
			for(StructuredAttribut attribute : structuredClass.getAttributs()) {
				currentField = FieldSpec.builder(ClassName.get(packageGeneratedClass, attribute.getType()), attribute.getName())
						.addModifiers(Modifier.PROTECTED)
						.build();
				addGetterAndSetter(currentClassBuilder, attribute);
				currentClassBuilder.addField(currentField);
			}
			if(!isTestClass) {
				javaFile = JavaFile.builder(packageGeneratedClass, currentClassBuilder.build()).build();
			} else {
				javaFile = JavaFile.builder(packageGeneratedClass, currentClassBuilder.build()).addStaticImport(org.junit.Assert.class, "*").build();
			}
			isTestClass = false;
			try {
				LOGGER.debug("Writing class create to " + directoryGeneratedClass);
				javaFile.writeTo(directoryGeneratedClass);
			} catch (IOException e) {
				LOGGER.error("Unable to write in " + directoryGeneratedClass + " do you have rights ?");
				throw new RuntimeException(e);
			}
		}

	}
	private void addGetterAndSetter(TypeSpec.Builder currentClass, StructuredAttribut attribute) {
		String attributeCamelCase = attribute.getName().substring(0,1).toUpperCase() + attribute.getName().substring(1);
		MethodSpec.Builder getter = MethodSpec.methodBuilder("get"+attributeCamelCase).addModifiers(Modifier.PUBLIC);
		MethodSpec.Builder setter = MethodSpec.methodBuilder("set"+attributeCamelCase).addModifiers(Modifier.PUBLIC);
		getter.addCode("return " + attribute.getName() + ";\n")
		.returns(ClassName.get(packageGeneratedClass, attribute.getType()));
		setter.addParameter(ClassName.get(packageGeneratedClass, attribute.getType()), attribute.getName());
		setter.addCode("this." + attribute.getName() + " = " + attribute.getName() + ";\n");
		currentClass.addMethod(getter.build());
		currentClass.addMethod(setter.build());
	}
}

