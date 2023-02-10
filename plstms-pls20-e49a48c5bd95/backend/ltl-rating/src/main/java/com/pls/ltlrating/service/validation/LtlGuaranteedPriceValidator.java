package com.pls.ltlrating.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;

/**
 * {@link LtlGuaranteedPriceEntity} validator.
 *
 * @author Artem Arapov
 *
 */
@Component
public class LtlGuaranteedPriceValidator extends AbstractValidator<LtlGuaranteedPriceEntity> {

    @Override
    protected void validateImpl(LtlGuaranteedPriceEntity entity) {
        asserts.notNull(entity.getLtlPricProfDetailId(), "price profile id");
        asserts.notNull(entity.getStatus(), "status");
    }

}
