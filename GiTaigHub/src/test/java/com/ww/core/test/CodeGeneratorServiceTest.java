package com.ww.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ww.core.CodeGeneratorService;
import com.ww.model.StructuredAttribut;
import com.ww.model.StructuredClass;
import com.ww.model.StructuredMethod;
import com.ww.model.StructuredUserStory;

public class CodeGeneratorServiceTest {

	private static final Logger LOGGER = Logger.getLogger(CodeGeneratorServiceTest.class);
	private static CodeGeneratorService generator;
	private final static StructuredUserStory MOCKED_STORY = new StructuredUserStory();
	private static final String HELLO_CLASS_NAME = "Hello";
	private static final String HELLO_CLASS_TYPE_ATTR = "String";
	private static final String HELLO_CLASS_NAME_ATTR = "nameOfPerson";
	private static final String HELLO_CLASS_METHOD_NAME = "sayHello";
	private static final Class<?> HELLO_CLASS_METHOD_RETURN = Void.class;
	private static final String[] HELLO_CLASS_METHOD_PARAMETER = new String[] { "String" };
	private static final String CONTENT_RESULT_CLASS_GENERATED = "package com.generated.by.ww;" +
																 "import java.lang.Void;" +
																 "public class Hello {" +
																 "  protected String nameOfPerson;" +
																 "  public Void sayHello(String string) {" +
																 "    // TODO: not yet implemented " +
																 "    return null;" +
																 "  }" +
																 "  public String getNameOfPerson() {" +
																 "    return nameOfPerson;" +
																 "  }" +
																 "  public void setNameOfPerson(String nameOfPerson) {" +
																 "    this.nameOfPerson = nameOfPerson;" +
																 "  }" +
																 "}";
	@BeforeClass
	public static void setUp() {
		generator = new CodeGeneratorService();
		List<StructuredClass> classes = new ArrayList<StructuredClass>();
		StructuredClass helloClass = new StructuredClass("Hello");
		helloClass.addAttribut(new StructuredAttribut(HELLO_CLASS_TYPE_ATTR, HELLO_CLASS_NAME_ATTR));
		helloClass.addMethod(new StructuredMethod(HELLO_CLASS_METHOD_NAME, HELLO_CLASS_METHOD_RETURN, HELLO_CLASS_METHOD_PARAMETER));
		classes.add(helloClass);
		MOCKED_STORY.setClasses(classes);
		generator.generateCodeFromStructuredUserStory(MOCKED_STORY);
	}

	@Test
	public void generatorFileTest() {

		File generatedFile = generator.getPackageGeneratedFile();
		assertTrue(generatedFile.exists());
		assertEquals(1,generatedFile.listFiles().length);
		assertEquals(HELLO_CLASS_NAME + ".java",generatedFile.listFiles()[0].getName());
	}
	@Test
	public void generatorFileContentTest() {
		File generatedFile = generator.getPackageGeneratedFile();
		File generatedClass = generatedFile.listFiles()[0];
		String classContent;
		try {
			classContent = FileUtils.readFileToString(generatedClass).replaceAll("\n", "");
		} catch (IOException e) {
			LOGGER.error("How that is possible ? Someone delete file between the two tests ?" + 
						 "Check your file system write");
			throw new RuntimeException(e);
		}
		assertEquals(CONTENT_RESULT_CLASS_GENERATED,classContent);
	}
}
