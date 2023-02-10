package com.pls.ltlrating.service.validation.profile;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;

/**
 * {@link LtlPricingProfileEntity} validator.
 *
 * @author Andrey Kachur
 *
 */
@Component
public class LtlPricingProfileValidator extends AbstractValidator<LtlPricingProfileEntity> {

    @Override
    protected void validateImpl(LtlPricingProfileEntity entity) {
        asserts.notNull(entity.getRateName(), "rateName");
    }

}