package com.pls.core.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * Bill To entity validator.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
@Component
public class BillToValidator extends AbstractValidator<BillToEntity> {

    @Autowired
    private BillToThresholdSettingsValidator thresholdSettingsValidator;

    @Override
    protected void validateImpl(BillToEntity billTo) {
        validateComponent(thresholdSettingsValidator, billTo.getBillToThresholdSettings(), "thresholdSettings");
    }
}
