package com.ww.core.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ww.core.UserStoryParserService;
import com.ww.model.StructuredClass;
import com.ww.model.StructuredUserStory;
import com.ww.model.TaigaUserStory;

public class UserStoryParserTest {

	private TaigaUserStory userStory;
	private UserStoryParserService parser;
	private StructuredUserStory structuredUserStory;

	private static final String TEST_CONTENT_RESULT =
			"BankManagement bankManagement = new BankManagement();" +
					"BankAccount bankAccount = new BankAccount();" +
					"Customer customer = new Customer();" +
					"if(customer.getBankAccount() != null) {" +
					"if(customer.getBankAccount().getValue() < 0) {"+
					"assertTrue(bankManagement.sendAWarning(customer));"+
					"}}";
	private static final String TEST_NAME = "BankManagementTest";
	private static final String BANK_MANAGEMENT_CLASS_NAME = "BankManagement";
	private static final String BANK_ACCOUNT_CLASS_NAME = "BankAccount";
	private static final String CUSTOMER_CLASS_NAME = "Customer";
	private static final String BANK_ACCOUNT_ATTR_NAME = "bankAccount";
	@Before
	public void setUp() throws Exception {
		userStory = new TaigaUserStory();
		userStory.setDescription("BankManagement can send a warning to Customer\n" +
				"IF Customer has a BankAccount THEN\n" +
				"IF Customer's BankAccount value is less than 0 THEN\n" +
				"send a warning\n" +
				"ENDIF\n" +
				"ENDIF\n");
		parser = new UserStoryParserService();
		parser.parseUserStory(userStory);
		structuredUserStory = parser.getStructuredUserStory();
	}

	@Test
	public void testParserTestContent() {
		String testContent = "";
		// Retrieve test content
		for(String statement : structuredUserStory.getTests().get(0).getMethods().get(0).getStatements()) {
			testContent += statement;
		}
		assertEquals(TEST_CONTENT_RESULT, testContent.replace("\n", ""));
	}
	
	@Test
	public void testParserDescriptionToStructuredClass() {
		// Waiting for BankManagement, Customer, BankAccount
		assertEquals(3,structuredUserStory.getClasses().size());
		for(StructuredClass strClass : structuredUserStory.getClasses()) {
			switch(strClass.getName()) {
			case CUSTOMER_CLASS_NAME :
				assertEquals(0,strClass.getMethods().size());
				assertEquals(1,strClass.getAttributs().size());
				assertEquals(BANK_ACCOUNT_ATTR_NAME,strClass.getAttributs().get(0).getName());
				assertEquals(BANK_ACCOUNT_CLASS_NAME,strClass.getAttributs().get(0).getType());
				break;
			case BANK_ACCOUNT_CLASS_NAME :
				assertEquals(0,strClass.getMethods().size());
				assertEquals(1,strClass.getAttributs().size());
				assertEquals("value",strClass.getAttributs().get(0).getName());
				assertEquals("Integer",strClass.getAttributs().get(0).getType());
				break;
			case BANK_MANAGEMENT_CLASS_NAME :
				assertEquals(1,strClass.getMethods().size());
				assertEquals("sendAWarning",strClass.getMethods().get(0).getName());
				assertEquals(Boolean.class,strClass.getMethods().get(0).getReturnType());
				assertEquals("Customer",strClass.getMethods().get(0).getArgs()[0]);
				assertEquals(0,strClass.getAttributs().size());
				break;
			}
		}
	}
}
