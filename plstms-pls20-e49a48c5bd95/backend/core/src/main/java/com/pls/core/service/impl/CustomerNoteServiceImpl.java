package com.pls.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.CustomerNoteDao;
import com.pls.core.domain.organization.CustomerNoteEntity;
import com.pls.core.service.CustomerNoteService;
import com.pls.core.service.validation.CustomerNoteValidator;
import com.pls.core.service.validation.ValidationException;

/**
 * {@link CustomerNoteService} implementation.
 * 
 * @author Aleksandr Leshchenko
 * @author Viacheslav Krot
 */
@Service
@Transactional
public class CustomerNoteServiceImpl implements CustomerNoteService {

    @Autowired
    private CustomerNoteDao dao;

    @Autowired
    private CustomerNoteValidator validator;

    @Override
    public List<CustomerNoteEntity> getAllCustomerNotes(Long customerId) {
        return dao.getCustomerNotesByCustomerId(customerId);
    }

    @Override
    public void saveCustomerNote(CustomerNoteEntity customerNote) throws ValidationException {
        validator.validate(customerNote);
        dao.persist(customerNote);
    }
}
