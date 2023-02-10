package com.pls.shipment.service.impl.validation;

import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.core.service.validation.support.AbstractValidator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Carrier invoice details entity validator.
 *
 * @author Alexander Kirichenko
 */
@Component
@Scope("prototype")
public class CarrierInvoiceDetailsValidator extends AbstractValidator<CarrierInvoiceDetailsEntity> {
    @Override
    protected void validateImpl(CarrierInvoiceDetailsEntity entity) {
        asserts.notEmpty(entity.getBolNumber(), "bolNumber");
    }
}
