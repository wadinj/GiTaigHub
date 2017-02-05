package com.ww.core;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
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
	private static final String PACKAGE_FOR_GENERATED_CLASS = "packageGeneratedClass";
	private String packageGeneratedClass;
	private File directoryGeneratedClass;
	private File packageGeneratedFile;

	public CodeGeneratorService() {
		packageGeneratedClass = GiTaigHubPropertiesUtils.getInstance().getProperties(PACKAGE_FOR_GENERATED_CLASS);
		directoryGeneratedClass = new File(GiTaigHubPropertiesUtils.getInstance().getProperties(DIRECTORY_PATH_FOR_GENERATED_CLASS));
		packageGeneratedFile = new File(directoryGeneratedClass.getAbsoluteFile() + "\\" + packageGeneratedClass.replace('.', '\\'));
		try {
			FileUtils.cleanDirectory(directoryGeneratedClass);
		} catch (IOException e) {
			LOGGER.warn("Cannot clean directory of generated class, be careful on dependencies issues");
		}
	}

	public void generateCodeFromStructuredUserStory(StructuredUserStory userStory) {
		TypeSpec.Builder currentClassBuilder;
		MethodSpec.Builder currentMethodBuilder;
		FieldSpec currentField;

		for(StructuredClass structuredClass : userStory.getClasses()) {
			currentClassBuilder = TypeSpec.classBuilder(structuredClass.getName()).addModifiers(Modifier.PUBLIC);
			// Building all methods
			for(StructuredMethod structuredMethod : structuredClass.getMethods()) {
				LOGGER.debug("Method => " + structuredMethod.getName());
				currentMethodBuilder = MethodSpec.methodBuilder(structuredMethod.getName()).addModifiers(Modifier.PUBLIC);
				if(structuredMethod.getAnnotation() != null) {
					currentMethodBuilder.addAnnotation(structuredMethod.getAnnotation());
				}
				if(structuredMethod.getArgs() != null && structuredMethod.getArgs().length > 0) {
					for(String args : structuredMethod.getArgs()) {
						// Check here, toLowerCase on parameter is not good strategy to keep camelCase
						LOGGER.debug("==> Args : " + args);
						currentMethodBuilder.addParameter(getClassFromString(args.split(" ")[0]), args.trim().toLowerCase());
					}
				}
				currentMethodBuilder.returns(structuredMethod.getReturnType())
				.addComment("TODO: not yet implemented \n", new Object[] {});
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
						// Check here, toLowerCase on parameter is not good strategy to keep camelCase
						currentMethodBuilder.addParameter(getClassFromString(args.split(" ")[0]), args.trim().toLowerCase());
					}
				}
				currentMethodBuilder.addComment("TODO: not yet implemented \n", new Object[] {});
				currentClassBuilder.addMethod(currentMethodBuilder.build());
			}
			// Adding attributes
			for(StructuredAttribut attribute : structuredClass.getAttributs()) {
				currentField = FieldSpec.builder(getClassFromString(attribute.getType()), attribute.getName())
						.addModifiers(Modifier.PROTECTED)
						.build();
				currentClassBuilder.addField(currentField);
			}
			JavaFile javaFile = JavaFile.builder(packageGeneratedClass, currentClassBuilder.build()).build();
			try {
				LOGGER.debug("Writing class create to " + directoryGeneratedClass);
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

		File sourceFile = new File(packageGeneratedFile, className + ".java");
		//		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		//		compiler.run(null, null, null, sourceFile.getPath());
		try {
			compileClassOfGeneratedPackage();
		} catch (CompilationError e1) {
			LOGGER.error("Unable to compile your class : " + e1);
			throw new RuntimeException("Unable to compile class generated by JavaPoet");
		}
		// Load and instantiate compiled class.
		URLClassLoader classLoader;
		try {
			LOGGER.debug("ClassLoader set on : " + sourceFile.getParentFile().toURI().toURL());
			classLoader = URLClassLoader.newInstance(new URL[] { directoryGeneratedClass.toURI().toURL() });
			return Class.forName(packageGeneratedClass + "." + className, true, classLoader);
		} catch (MalformedURLException e) {
			LOGGER.error("Loading URL for file not found. Could you check your properties file ?");
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("ClassNotFound => How it is possible ? Why code generator doesn't generate it before");
			throw new RuntimeException(e);
		}
	}
	private void compileClassOfGeneratedPackage() throws CompilationError {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.getDefault(), null);
		List<JavaFileObject> javaObjects = scanRecursivelyForJavaObjects(directoryGeneratedClass, fileManager);

		if (javaObjects.size() == 0) {
			throw new CompilationError("There are no source files to compile in " + directoryGeneratedClass.getAbsolutePath());
		}
		String[] compileOptions = new String[]{"-d", directoryGeneratedClass.getAbsolutePath()} ;
		Iterable<String> compilationOptions = Arrays.asList(compileOptions);
		CompilationTask compilerTask = compiler.getTask(null, fileManager, diagnostics, compilationOptions, null, javaObjects) ;

		if (!compilerTask.call()) {
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				System.err.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
			}
			throw new CompilationError("Could not compile project");
		}


	}
	private List<JavaFileObject> scanRecursivelyForJavaObjects(File dir, StandardJavaFileManager fileManager) { 
		List<JavaFileObject> javaObjects = new LinkedList<JavaFileObject>(); 
		File[] files = dir.listFiles(); 
		for (File file : files) { 
			if (file.isDirectory()) { 
				javaObjects.addAll(scanRecursivelyForJavaObjects(file, fileManager)); 
			} 
			else if (file.isFile() && file.getName().toLowerCase().endsWith(".java")) { 
				javaObjects.add(readJavaObject(file, fileManager)); 
			} 
		} 
		return javaObjects; 
	} 
	private JavaFileObject readJavaObject(File file, StandardJavaFileManager fileManager) { 
		Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjects(file); 
		Iterator<? extends JavaFileObject> it = javaFileObjects.iterator(); 
		if (it.hasNext()) { 
			return it.next(); 
		} 
		throw new RuntimeException("Could not load " + file.getAbsolutePath() + " java file object"); 
	} 
}

