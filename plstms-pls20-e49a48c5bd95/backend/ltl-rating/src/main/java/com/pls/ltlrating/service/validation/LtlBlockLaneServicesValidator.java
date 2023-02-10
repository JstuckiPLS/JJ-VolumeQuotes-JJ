package com.pls.ltlrating.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlBlockLaneEntity;

/**
 * {@link LtlBlockLaneEntity} validator.
 *
 * @author Ashwini Neelgund
 *
 */
@Component
public class LtlBlockLaneServicesValidator extends AbstractValidator<LtlBlockLaneEntity> {

    @Override
    protected void validateImpl(LtlBlockLaneEntity entity) {
        asserts.notNull(entity.getCarrierId(), "carrierId");
        asserts.notNull(entity.getShipperId(), "shipperId");
        asserts.notNull(entity.getStatus(), "status");
        asserts.notNull(entity.getEffDate(), "effDate");
    }

}
