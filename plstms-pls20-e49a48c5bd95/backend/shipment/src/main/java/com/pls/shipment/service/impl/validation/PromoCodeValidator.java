package com.pls.shipment.service.impl.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.shipment.service.PromoCodesService;

/**
 * Promo code validator.
 * 
 * @author Brichak Aleksandr
 */
@Component
public class PromoCodeValidator extends AbstractValidator<SimpleValue> {

    @Autowired
    private PromoCodesService promoCodesService;

    @Override
    protected void validateImpl(SimpleValue promoCode) {
        if (promoCode != null) {
            asserts.isTrue(promoCodesService.isPromoCodeUnique(promoCode.getName(), promoCode.getId()), "promoCode",
                    ValidationError.UNIQUE);
        }
    }
}
