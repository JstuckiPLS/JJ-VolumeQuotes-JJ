package com.pls.ltlrating.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.domain.LtlBlockCarrGeoServicesEntity;

/**
 * {@link LtlBlockCarrGeoServicesEntity} validator.
 *
 * @author Artem Arapov
 *
 */
@Component
public class LtlBlockCarrGeoServicesValidator extends AbstractValidator<LtlBlockCarrGeoServicesEntity> {

    @Override
    protected void validateImpl(LtlBlockCarrGeoServicesEntity entity) {
        asserts.notNull(entity.getProfileId(), "profileId");
        asserts.notNull(entity.getStatus(), "status");
    }

}
