package com.pls.core.service;

import java.util.List;

import com.pls.core.domain.organization.CustomerNoteEntity;
import com.pls.core.service.validation.ValidationException;

/**
 * Business service that handles business logic for customer notes.
 * 
 * @author Aleksandr Leshchenko
 * 
 */
public interface CustomerNoteService {

    /**
     * Retrieve customer notes for specified customerId.
     * 
     * @param customerId
     *            for whom notes should be found
     * @return list of customer notes
     */
    List<CustomerNoteEntity> getAllCustomerNotes(Long customerId);

    /**
     * Save customer note in DB.
     * 
     * @param customerNote
     *            to be saved
     * @throws ValidationException
     *             if validation checks fail.
     */
    void saveCustomerNote(CustomerNoteEntity customerNote) throws ValidationException;
}
