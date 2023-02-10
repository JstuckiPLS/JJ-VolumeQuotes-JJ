package com.pls.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.dao.CustomerNoteDao;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.CustomerNoteEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.impl.CustomerNoteServiceImpl;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.validation.CustomerNoteValidator;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.ValidationException;


/**
 * Test cases for {@link CustomerNoteServiceImpl} class.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerNoteServiceImplTest {
    private static final long CUSTOMER_ID = (long) (Math.random() * 100);
    private static final long USER_ID = (long) (Math.random() * 100);

    @Mock
    private CustomerNoteDao dao;

    @Mock
    private CustomerNoteValidator noteValidator;

    @InjectMocks
    private CustomerNoteServiceImpl service;

    @Before
    public void init() throws Exception {
        SecurityTestUtils.login("Test", USER_ID);
    }

    @Test
    public void shouldFailValidationNoText() throws ValidationException {
        // given
        CustomerNoteEntity customerNote = new CustomerNoteEntity();
        //TODO Do we really should check validator here?
        HashMap<String, ValidationError> errors = new HashMap<String, ValidationError>();
        errors.put("note", ValidationError.IS_EMPTY);
        Mockito.doThrow(new ValidationException(errors)).when(noteValidator).validate((CustomerNoteEntity) Mockito.anyObject());

        // when
        try {
            service.saveCustomerNote(customerNote);
        } catch (ValidationException e) {
            // check that empty note validation error is present
            ValidationError error = e.getErrors().get("note");
            Assert.assertNotNull(error);
            Assert.assertEquals(ValidationError.IS_EMPTY, error);
            return;
        }
        Assert.fail("Didn't get validation exception");
    }

    @Test
    public void shouldGetAllCustomerNotes() {
        // given
        List<CustomerNoteEntity> notesList = Collections
                .unmodifiableList(new ArrayList<CustomerNoteEntity>());
        Mockito.when(dao.getCustomerNotesByCustomerId(Matchers.eq(CUSTOMER_ID))).thenReturn(notesList);

        // when
        List<CustomerNoteEntity> foundNotes = service.getAllCustomerNotes(CUSTOMER_ID);

        // then
        Mockito.verify(dao).getCustomerNotesByCustomerId(Matchers.eq(CUSTOMER_ID));
        Assert.assertSame(notesList, foundNotes);
    }

    @Test
    public void shouldSaveCustomerNote() throws ApplicationException {
        // given
        CustomerNoteEntity customerNote = new CustomerNoteEntity();
        customerNote.setNote("note text");
        CustomerEntity customer = new CustomerEntity();
        customer.setId(CUSTOMER_ID);
        customerNote.setCustomer(customer);

        // when
        service.saveCustomerNote(customerNote);

        // then
        Mockito.verify(dao).persist(Matchers.argThat(new ArgumentMatcher<CustomerNoteEntity>() {
            @Override
            public boolean matches(Object argument) {
                CustomerNoteEntity realEntity = (CustomerNoteEntity) argument;
                Assert.assertNotNull(realEntity.getModification().getCreatedDate());
                Assert.assertNotNull(realEntity.getModification().getModifiedDate());
                Assert.assertTrue(DateUtils.isSameDay(realEntity.getModification().getCreatedDate(), realEntity.getModification().getModifiedDate()));
                Assert.assertSame(USER_ID, realEntity.getModification().getCreatedBy());
                Assert.assertSame(USER_ID, realEntity.getModification().getCreatedBy());
                return true;
            }
        }));
    }
}
