package com.pls.core.service.validation;

import com.pls.core.domain.organization.OrganizationPhoneEntity;
import com.pls.core.service.validation.support.AbstractValidator;
import org.springframework.stereotype.Component;

/**
 * Phone & fax validator.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
public class OrganizationPhoneValidator extends AbstractValidator<OrganizationPhoneEntity> {
    private static final int CODES_MAX_LENGTH = 3;
    private static final int NUMBER_LENGTH = 7;

    @Override
    protected void validateImpl(OrganizationPhoneEntity entity) {
        asserts.notEmpty(entity.getPhoneNumber(), "phoneNumber");
        if (entity.getPhoneNumber() != null && entity.getPhoneNumber().length() != NUMBER_LENGTH) {
            asserts.fail("phoneNumber", ValidationError.FORMAT);
        }
        asserts.notNull(entity.getOrganization(), "organization");

        if (entity.getAreaCode() != null && entity.getAreaCode().length() > CODES_MAX_LENGTH) {
            asserts.fail("areaCode", ValidationError.GREATER_THAN);
        }
        if (entity.getDialingCode() != null && entity.getDialingCode().length() > CODES_MAX_LENGTH) {
            asserts.fail("dialingCode", ValidationError.GREATER_THAN);
        }
    }
}
