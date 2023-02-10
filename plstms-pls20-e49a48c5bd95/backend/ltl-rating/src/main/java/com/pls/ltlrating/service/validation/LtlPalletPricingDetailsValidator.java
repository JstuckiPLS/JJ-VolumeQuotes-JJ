package com.pls.ltlrating.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;

/**
 * {@link LtlPalletPricingDetailsEntity} validator.
 *
 * @author Artem Arapov
 *
 */
@Component
public class LtlPalletPricingDetailsValidator extends AbstractValidator<LtlPalletPricingDetailsEntity> {

    @Override
    protected void validateImpl(LtlPalletPricingDetailsEntity entity) {
        asserts.notNull(entity.getProfileDetailId(), "profileDetailId");
        asserts.notNull(entity.getStatus(), "status");
    }

}
