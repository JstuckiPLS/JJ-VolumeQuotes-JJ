package com.pls.ltlrating.service.validation.profile;

import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;

import org.springframework.stereotype.Component;

/**
 * Validator for entity {@link LtlPricingProfileDetailsEntity}.
 *
 * @author Andrey Kachur
 *
 */
@Component
public class LtlPricingProfileDetailsValidator extends AbstractValidator<LtlPricingProfileDetailsEntity> {

    @Override
    protected void validateImpl(LtlPricingProfileDetailsEntity entity) {
        asserts.notNull(entity.getLtlPricingProfileId(), "ltlPricingProfileId");
    }

}
