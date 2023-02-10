package com.pls.core.service.validation;

import com.pls.core.domain.organization.CustomerNoteEntity;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Customer note validator.
 * 
 * @author Viacheslav Krot
 */
@Component
public class CustomerNoteValidator extends AbstractValidator<CustomerNoteEntity> {

    @Override
    protected void validateImpl(CustomerNoteEntity entity) {
        asserts.notEmpty(entity.getNote(), "note");
        asserts.notNull(entity.getCustomer(), "customer");
    }
}
