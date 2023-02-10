package com.pls.shipment.service.product.impl.validation;

import com.pls.shipment.domain.LtlProductEntity;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Validator for {@link LtlProductEntity}.
 * 
 * @author Viacheslav Krot
 */
public class LtlProductValidator extends AbstractValidator<LtlProductEntity> {
    @Override
    protected void validateImpl(LtlProductEntity entity) {
        asserts.notNull(entity.getCustomerId(), "customerId");
        asserts.notNull(entity.getCommodityClass(), "commodityClass");
        asserts.notNull(entity.getTrackingId(), "trackingId");
        asserts.notEmpty(entity.getDescription(), "description");
        if (entity.isHazmat()) {
            asserts.notNull(entity.getHazmatInfo(), "hazmatInfo");
            asserts.notEmpty(entity.getHazmatInfo().getEmergencyCompany(), "hazmatInfo.EmergencyCompany");
        }
    }
}
