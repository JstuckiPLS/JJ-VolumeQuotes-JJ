package com.pls.ltlrating.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;

/**
 * {@link LtlPricingDetailsEntity} validator.
 *
 * @author Artem Arapov
 *
 */
@Component
public class LtlPricingDetailsValidator extends AbstractValidator<LtlPricingDetailsEntity> {

    @Override
    protected void validateImpl(LtlPricingDetailsEntity entity) {
        asserts.notNull(entity.getLtlPricProfDetailId(), "ltlPricProfDetailId");
        asserts.notNull(entity.getStatus(), "status");
    }

}
