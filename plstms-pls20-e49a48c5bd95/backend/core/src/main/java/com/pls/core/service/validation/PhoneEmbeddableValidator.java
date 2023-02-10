package com.pls.core.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.domain.address.PhoneEmbeddableObject;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Phone embeddable object validator.
 * 
 * @author Andrey Kachur
 */
@Component
public class PhoneEmbeddableValidator extends AbstractValidator<PhoneEmbeddableObject> {
    private static final int CODES_MAX_LENGTH = 3;
    private static final int NUMBER_LENGTH = 7;

    @Override
    protected void validateImpl(PhoneEmbeddableObject entity) {
        asserts.notEmpty(entity.getAreaCode(), "areaCode");
        asserts.notEmpty(entity.getCountryCode(), "countryCode");
        asserts.notEmpty(entity.getNumber(), "number");
        if (entity.getNumber() != null && entity.getNumber().length() != NUMBER_LENGTH) {
            asserts.fail("number", ValidationError.FORMAT);
        }
        if (entity.getAreaCode() != null && entity.getAreaCode().length() > CODES_MAX_LENGTH) {
            asserts.fail("areaCode", ValidationError.GREATER_THAN);
        }
        if (entity.getCountryCode() != null && entity.getCountryCode().length() > CODES_MAX_LENGTH) {
            asserts.fail("dialingCode", ValidationError.GREATER_THAN);
        }
    }

}
