package com.generated.by.ww;

import static org.junit.Assert.*;

import org.junit.Test;

public class BankManagementTest {
  @Test
  public void bankmanagementCanAllowLoanFacilityToCustomerTest() {
    BankManagement bankManagement = new BankManagement();
    Customer customer = new Customer();

    if(customer.getBankAccount() != null) {

    if(customer.getDues() == null) {

    assertTrue(bankManagement.allowLoanFacility(customer));
    } else {
    if(bankManagement.approves(customer)) {

    assertTrue(bankManagement.allowLoanFacility(customer));
    } else {
    assertTrue(bankManagement.reject(customer));

    }

    }
    } else {
    assertTrue(bankManagement.reject(customer));

    }
  }
}
