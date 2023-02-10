package com.pls.core.service.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.pls.core.domain.PhoneNumber;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Validator for phone entity.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Component
public class PhoneValidator extends AbstractValidator<PhoneNumber> {

    private static final int AREA_CODE_MAX_LENGTH = 3;

    private static final int COUNTRY_CODE_MAX_LENGTH = 3;

    private static final int PHONE_NUMBER_LENGTH = 7;

    private static final int EXTENSION_MAX_LENGTH = 6;

    @Override
    protected void validateImpl(PhoneNumber entity) {
        asserts.notEmpty(entity.getNumber(), "number");
        if (entity.getNumber() != null) {
            validateNumber(entity);
        }
        asserts.notEmpty(entity.getAreaCode(), "areaCode");
        if (entity.getAreaCode() != null) {
            validateAreaCode(entity);
        }
        asserts.notEmpty(entity.getCountryCode(), "countryCode");
        if (entity.getCountryCode() != null) {
            validateCountryCode(entity);
        }
        if (StringUtils.isNotEmpty(entity.getExtension())) {
            validateExtension(entity);
        }
    }

    private void validateExtension(PhoneNumber entity) {
        if (entity.getExtension().length() > EXTENSION_MAX_LENGTH) {
            asserts.fail("extension", ValidationError.GREATER_THAN);
        }
        if (!entity.getExtension().matches("\\d+")) {
            asserts.fail("extension", ValidationError.FORMAT);
        }
    }

    private void validateCountryCode(PhoneNumber entity) {
        if (entity.getCountryCode().length() > COUNTRY_CODE_MAX_LENGTH) {
            asserts.fail("countryCode", ValidationError.GREATER_THAN);
        }
        if (!entity.getCountryCode().matches("\\d+")) {
            asserts.fail("countryCode", ValidationError.FORMAT);
        }
    }

    private void validateAreaCode(PhoneNumber entity) {
        if (entity.getAreaCode().length() > AREA_CODE_MAX_LENGTH) {
            asserts.fail("areaCode", ValidationError.GREATER_THAN);
        }
        if (!entity.getAreaCode().matches("\\d+")) {
            asserts.fail("areaCode", ValidationError.FORMAT);
        }
    }

    private void validateNumber(PhoneNumber entity) {
        if (entity.getNumber().length() < PHONE_NUMBER_LENGTH) {
            asserts.fail("number", ValidationError.LESS_THAN);
        } else if (entity.getNumber().length() > PHONE_NUMBER_LENGTH) {
            asserts.fail("number", ValidationError.GREATER_THAN);
        }
        if (!entity.getNumber().matches("\\d+")) {
            asserts.fail("number", ValidationError.FORMAT);
        }
    }


}
