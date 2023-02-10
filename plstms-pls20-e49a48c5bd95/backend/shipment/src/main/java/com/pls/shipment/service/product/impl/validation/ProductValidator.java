package com.pls.shipment.service.product.impl.validation;

import org.springframework.stereotype.Component;

import com.pls.shipment.domain.LtlProductEntity;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Product validator.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
public class ProductValidator extends AbstractValidator<LtlProductEntity> {

    @Override
    protected void validateImpl(LtlProductEntity entity) {
        if (entity == null) {
            return;
        }
        asserts.notEmpty(entity.getDescription(), "description");
        // In PLS 1.0 commodityClass can be empty. Commented for old database integration.
        //asserts.notNull(entity.getCommodityClass(), "commodityClass");
        if (entity.isHazmat()) {
            asserts.notEmpty(entity.getHazmatInfo().getUnNumber(), "unNumber");
            asserts.notEmpty(entity.getHazmatInfo().getHazmatClass(), "hazmatClass");
        }
        asserts.notNull(entity.getCustomerId(), "customerId");
    }
}
