package com.ww.core;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.ww.model.StructuredAttribut;
import com.ww.model.StructuredClass;
import com.ww.model.StructuredConstrutor;
import com.ww.model.StructuredMethod;
import com.ww.model.StructuredUserStory;
import com.ww.utils.GiTaigHubPropertiesUtils;

public class CodeGeneratorService {

	private static final Logger LOGGER = Logger.getLogger(CodeGeneratorService.class);
	private static final String DIRECTORY_PATH_FOR_GENERATED_CLASS = "directoryGeneratedClass";
	private static final String PACKAGE_FOR_GENERATED_CLASS = "	packageGeneratedClass";
	private String packageGeneratedClass;
	private File directoryGeneratedClass;
	
	public CodeGeneratorService() {
		directoryGeneratedClass = new File(GiTaigHubPropertiesUtils.getInstance().getProperties(DIRECTORY_PATH_FOR_GENERATED_CLASS));
		packageGeneratedClass = GiTaigHubPropertiesUtils.getInstance().getProperties(PACKAGE_FOR_GENERATED_CLASS);
	}

	public void generateCodeFromStructuredUserStory(StructuredUserStory userStory) {
		TypeSpec.Builder currentClassBuilder;
		MethodSpec.Builder currentMethodBuilder;
		FieldSpec currentField;
		  
		for(StructuredClass structuredClass : userStory.getClasses()) {
			currentClassBuilder = TypeSpec.classBuilder(structuredClass.getName());
			// Building all methods
			for(StructuredMethod structuredMethod : structuredClass.getMethods()) {
				currentMethodBuilder = MethodSpec.methodBuilder(structuredMethod.getName());
				if(structuredMethod.getAnnotation() != null) {
					currentMethodBuilder.addAnnotation(structuredMethod.getAnnotation());
				}
				if(structuredMethod.getArgs() != null && structuredMethod.getArgs().length > 0) {
					for(String args : structuredMethod.getArgs()) {
						// Check here, toLowerCase on parameter is not good strategy to keep camelCase
						currentMethodBuilder.addParameter(getClassFromString(args), args.toLowerCase());
					}
				}
				currentMethodBuilder.returns(structuredMethod.getReturnType())
				.addComment("// TODO: not yet implemented \n", new Object[] {});
				currentClassBuilder.addMethod(currentMethodBuilder.build());
			}
			// Building constructors
			for(StructuredConstrutor structuredConstructor : structuredClass.getConstructors()) {
				currentMethodBuilder = MethodSpec.constructorBuilder();
				if(structuredConstructor.getAnnotation() != null) {
					currentMethodBuilder.addAnnotation(structuredConstructor.getAnnotation());
				}
				if(structuredConstructor.getArgs() != null && structuredConstructor.getArgs().length > 0) {
					for(String args : structuredConstructor.getArgs()) {
						// Check here, toLowerCase on parameter is not good strategy to keep camelCase
						currentMethodBuilder.addParameter(getClassFromString(args), args.toLowerCase());
					}
				}
				currentMethodBuilder.addComment("// TODO: not yet implemented \n", new Object[] {});
				currentClassBuilder.addMethod(currentMethodBuilder.build());
			}
			// Adding attributes
			for(StructuredAttribut attribute : structuredClass.getAttributs()) {
				currentField = FieldSpec.builder(getClassFromString(attribute.getType()), attribute.getName())
					    .build();
				currentClassBuilder.addField(currentField);
			}
			JavaFile javaFile = JavaFile.builder(packageGeneratedClass, currentClassBuilder.build()).build();
			try {
				javaFile.writeTo(directoryGeneratedClass);
			} catch (IOException e) {
				LOGGER.error("Unable to write in " + directoryGeneratedClass + " do you have rights ?");
				throw new RuntimeException(e);
			}
		}
	}
	private Class<?> getClassFromString(String className) {
		LOGGER.debug("Retrieve class from string : " + className);

		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			LOGGER.debug("Class not found in current classpath, try to load it in generated classes");
			return loadClassGenerated(className);
		}

	}
	private Class<?> loadClassGenerated(String className) {

		File sourceFile = new File(directoryGeneratedClass, className + ".java");
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, sourceFile.getPath());
		// Load and instantiate compiled class.
		URLClassLoader classLoader;
		try {
			classLoader = URLClassLoader.newInstance(new URL[] { sourceFile.toURI().toURL() });
			return Class.forName(className, true, classLoader);
		} catch (MalformedURLException e) {
			LOGGER.error("Loading URL for file not found. Could you check your properties file ?");
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("ClassNotFound => How it is possible ? Why code generator doesn't generate it before");
			throw new RuntimeException(e);
		}
	}
}
