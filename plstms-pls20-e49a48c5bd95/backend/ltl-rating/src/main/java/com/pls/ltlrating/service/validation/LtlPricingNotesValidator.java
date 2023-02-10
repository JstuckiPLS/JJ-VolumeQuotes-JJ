package com.pls.ltlrating.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;

/**
 * {@link LtlPricingNotesEntity} validator.
 *
 * @author Artem Arapov
 *
 */
@Component
public class LtlPricingNotesValidator extends AbstractValidator<LtlPricingNotesEntity> {

    private static final int MAX_NOTES_LENGTH = 3000;

    @Override
    protected void validateImpl(LtlPricingNotesEntity entity) {
        asserts.notNull(entity.getPricingProfileId(), "pricingProfileId");
        asserts.notNull(entity.getNotes(), "notes");
        asserts.notEmpty(entity.getNotes(), "notes");
        asserts.isTrue(entity.getNotes().length() < MAX_NOTES_LENGTH, "notes", ValidationError.GREATER_THAN);
        asserts.notNull(entity.getCreatedDate(), "createdDate");
        asserts.notNull(entity.getCreatedBy(), "createdBy");
    }

}
