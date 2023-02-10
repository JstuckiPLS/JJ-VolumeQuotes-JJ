package com.pls.user.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.domain.user.UserPhoneEntity;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Phone & fax validator.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
public class UserPhoneValidator extends AbstractValidator<UserPhoneEntity> {
    private static final int AREA_CODE_MAX_LENGTH = 3;
    private static final int COUNTRY_CODE_MAX_LENGTH = 3;
    private static final int PHONE_NUMBER_LENGTH = 7;
    private static final int EXTENSION_MAX_LENGTH = 6;

    @Override
    protected void validateImpl(UserPhoneEntity entity) {
        asserts.notEmpty(entity.getNumber(), "number");
        if (entity.getNumber() != null) {
            if (entity.getNumber().length() < PHONE_NUMBER_LENGTH) {
                asserts.fail("number", ValidationError.LESS_THAN);
            } else if (entity.getNumber().length() > PHONE_NUMBER_LENGTH) {
                asserts.fail("number", ValidationError.GREATER_THAN);
            }
        }
        asserts.notEmpty(entity.getAreaCode(), "areaCode");
        if (entity.getAreaCode() != null && entity.getAreaCode().length() > AREA_CODE_MAX_LENGTH) {
            asserts.fail("areaCode", ValidationError.GREATER_THAN);
        }
        asserts.notEmpty(entity.getCountryCode(), "countryCode");
        if (entity.getCountryCode() != null && entity.getCountryCode().length() > COUNTRY_CODE_MAX_LENGTH) {
            asserts.fail("countryCode", ValidationError.GREATER_THAN);
        }
        validateExtension(entity);
    }

    private void validateExtension(UserPhoneEntity entity) {
        // Extension can be null or empty and max length can be upto 6 digits.
        if (entity.getExtension() != null && entity.getExtension().length() > EXTENSION_MAX_LENGTH) {
            asserts.fail("extension", ValidationError.GREATER_THAN);
        }
    }
}
