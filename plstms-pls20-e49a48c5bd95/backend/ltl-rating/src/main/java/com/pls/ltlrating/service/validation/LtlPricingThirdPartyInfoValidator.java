package com.pls.ltlrating.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;

/**
 * {@link LtlPricingThirdPartyInfoEntity} validator.
 *
 * @author Artem Arapov
 *
 */
@Component
public class LtlPricingThirdPartyInfoValidator extends AbstractValidator<LtlPricingThirdPartyInfoEntity> {

    @Override
    protected void validateImpl(LtlPricingThirdPartyInfoEntity entity) {
        asserts.notNull(entity.getPricProfDetailId(), "ltlPricProfDetailId");
        asserts.notNull(entity.getStatus(), "status");
    }

}
