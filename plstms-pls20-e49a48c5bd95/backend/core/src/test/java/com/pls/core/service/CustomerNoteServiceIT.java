package com.pls.core.service;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.CustomerNoteEntity;
import com.pls.core.service.impl.security.util.SecurityTestUtils;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;

/**
 * Customer Notes IT.
 * 
 * @author Viacheslav Krot
 */
public class CustomerNoteServiceIT extends BaseServiceITClass {
    private static final Long CUSTOMER_ID = 1L;

    private static final String USERNAME = "sysadmin";

    @Autowired
    private CustomerNoteService service;

    @Before
    public void before() {
        SecurityTestUtils.logout();
    }

    @Ignore("Revert after fix security configuration or remove this test")
    //TODO Revert after fix security configuration or remove this test
    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void testDenyGetAllWithoutAuthentication() throws Exception {
        service.getAllCustomerNotes(CUSTOMER_ID);
    }

    @Ignore("Revert after fix security configuration or remove this test")
    //TODO Revert after fix security configuration or remove this test
    @Test(expected = AccessDeniedException.class)
    public void testDenyGetAllWithoutRole() throws Exception {
        SecurityTestUtils.login(USERNAME);
        service.getAllCustomerNotes(CUSTOMER_ID);
    }

    @Test
    public void testGetAll() throws Exception {
        SecurityTestUtils.login(USERNAME);
        service.getAllCustomerNotes(CUSTOMER_ID);
    }

    @Ignore("Revert after fix security configuration or remove this test")
    //TODO Revert after fix security configuration or remove this test
    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void testDenySaveWithoutAuthentication() throws Exception {
        service.saveCustomerNote(createNote());
    }

    @Ignore("Revert after fix security configuration or remove this test")
    //TODO Revert after fix security configuration or remove this test
    @Test(expected = AccessDeniedException.class)
    public void testDenySaveWithoutRole() throws Exception {
        SecurityTestUtils.login(USERNAME);
        service.saveCustomerNote(createNote());
    }

    @Test
    public void testSave() throws Exception {
        SecurityTestUtils.login(USERNAME);
        try {
            service.saveCustomerNote(createNote());
        } catch (AccessDeniedException e) {
            Assert.fail("Authentication failed");
        } catch (AuthenticationException e) {
            Assert.fail("Authentication failed");
        }
    }

    private CustomerNoteEntity createNote() {
        CustomerNoteEntity note = new CustomerNoteEntity();
        note.setNote("1234");
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        note.setCustomer(customer);
        note.getModification().setCreatedBy(1L);
        note.getModification().setModifiedBy(1L);
        return note;
    }
}
