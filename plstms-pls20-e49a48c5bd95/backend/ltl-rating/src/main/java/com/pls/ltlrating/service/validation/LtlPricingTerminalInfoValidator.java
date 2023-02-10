package com.pls.ltlrating.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;

/**
 * {@link LtlPricingTerminalInfoEntity} validator.
 *
 * @author Artem Arapov
 *
 */
@Component
public class LtlPricingTerminalInfoValidator extends AbstractValidator<LtlPricingTerminalInfoEntity> {

    @Override
    protected void validateImpl(LtlPricingTerminalInfoEntity entity) {
        asserts.notNull(entity.getPriceProfileId(), "priceProfileId");
        asserts.notNull(entity.getStatus(), "status");
    }

}
