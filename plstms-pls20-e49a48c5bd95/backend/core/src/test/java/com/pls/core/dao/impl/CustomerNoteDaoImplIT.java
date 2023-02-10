package com.pls.core.dao.impl;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.CustomerNoteDao;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.CustomerNoteEntity;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Test cases for {@linkCustomerNoteDaoImpl} class.
 * 
 * @author Aleksandr Leshchenko
 */
public class CustomerNoteDaoImplIT extends AbstractDaoTest {

    @Autowired
    private CustomerNoteDao dao;

    @Test
    public void shouldCreateNote() throws Exception {
        CustomerNoteEntity note = createNoteForTesting();

        flushAndClearSession();

        CustomerNoteEntity foundNote = (CustomerNoteEntity) getSession().get(CustomerNoteEntity.class, note.getId());
        verifyNoteEquals(note, foundNote);
    }

    @Test
    public void shouldGetCustomerNotesByCustomerIdInValidOrder() {
        prepareTestData();

        List<CustomerNoteEntity> notes = dao.getCustomerNotesByCustomerId(1L);
        Assert.assertTrue(notes.size() > 1);
        validateNotesOrder(notes);
    }

    @Test
    public void shouldLoadCustomerNotesWithValidCustomerReferenceAndValidOrder() {
        prepareTestData();

        CustomerEntity customer = (CustomerEntity) getSession().get(CustomerEntity.class, 1L);
        Assert.assertTrue(customer.getNotes().size() > 1);
        for (CustomerNoteEntity note : customer.getNotes()) {
            Assert.assertEquals(customer, note.getCustomer());
        }
        validateNotesOrder(customer.getNotes());
    }

    private void prepareTestData() {
        createNoteForTesting();
        createNoteForTesting();

        flushAndClearSession();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotRemoveNoteByEntity() {
        CustomerNoteEntity note = createNoteForTesting();
        dao.remove(note);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotRemoveNoteByID() {
        CustomerNoteEntity note = createNoteForTesting();
        dao.remove(note.getId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotUpdateNote() {
        CustomerNoteEntity note = createNoteForTesting();
        note.getModification().setModifiedDate(new Date());
        note.setNote(note.getNote() + ".Updated");
        dao.update(note);
    }

    /**
     * create note for testing and save it to DB.
     * 
     * @return note from DB
     */
    private CustomerNoteEntity createNoteForTesting() {
        CustomerNoteEntity note = createTestNote();
        dao.persist(note);
        return note;
    }

    /**
     * @return note for testing
     */
    private CustomerNoteEntity createTestNote() {
        CustomerNoteEntity note = new CustomerNoteEntity();
        note.setNote("Here goes some testing note for customer: " + Math.random());
        note.setCustomer(getCustomer());
        note.getModification().setCreatedBy(1L);
        note.getModification().setModifiedBy(1L);
        return note;
    }

    /**
     * @return customer for testing
     */
    private CustomerEntity getCustomer() {
        return (CustomerEntity) getSession().get(CustomerEntity.class, 1L);
    }

    /**
     * Checks that notes are sorted in the order they were created.
     * 
     * @param notes
     *            to validate
     */
    private void validateNotesOrder(List<CustomerNoteEntity> notes) {
        for (int i = 1; i < notes.size(); i++) {
            Assert.assertTrue(notes.get(i).getModification().getCreatedDate()
                    .before(notes.get(i - 1).getModification().getCreatedDate())
                    || notes.get(i).getModification().getCreatedDate()
                            .equals(notes.get(i - 1).getModification().getCreatedDate()));
        }
    }

    /**
     * @param note
     *            to be compared
     * @param foundNote
     *            - another note to be compared
     */
    private void verifyNoteEquals(CustomerNoteEntity note, CustomerNoteEntity foundNote) {
        Assert.assertEquals(note.getId(), foundNote.getId());
        Assert.assertEquals(note.getCustomer().getId(), foundNote.getCustomer().getId());
        Assert.assertEquals(note.getNote(), foundNote.getNote());
        Assert.assertEquals(note.getVersion(), foundNote.getVersion());
        Assert.assertEquals(note.isActiveStatus(), foundNote.isActiveStatus());
        Assert.assertEquals(note.isInternal(), foundNote.isInternal());
        PlainModificationObject foundNoteModification = foundNote.getModification();
        PlainModificationObject modification = note.getModification();
        Assert.assertEquals(DateUtils.truncate(modification.getCreatedDate(), Calendar.SECOND),
                foundNoteModification.getCreatedDate());
        Assert.assertEquals(modification.getCreatedBy(), foundNoteModification.getCreatedBy());
        Assert.assertEquals(DateUtils.truncate(modification.getCreatedDate(), Calendar.SECOND),
                foundNoteModification.getCreatedDate());
        Assert.assertEquals(modification.getModifiedBy(), foundNoteModification.getModifiedBy());
    }
}