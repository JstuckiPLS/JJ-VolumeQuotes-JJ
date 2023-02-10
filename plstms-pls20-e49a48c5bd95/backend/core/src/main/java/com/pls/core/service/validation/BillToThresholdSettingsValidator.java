package com.pls.core.service.validation;

import org.springframework.stereotype.Component;

import com.pls.core.domain.organization.BillToThresholdSettingsEntity;
import com.pls.core.service.validation.support.AbstractValidator;

/**
 * BillToThresholdSettings entity validator.
 * 
 * @author Dmitry Nikolaenko
 *
 */
@Component
public class BillToThresholdSettingsValidator extends AbstractValidator<BillToThresholdSettingsEntity> {

    @Override
    protected void validateImpl(BillToThresholdSettingsEntity entity) {
        if (entity != null) {
            asserts.notNull(entity.getThreshold(), "threshold");
            asserts.notNull(entity.getTotalRevenue(), "totalRevenue");
            asserts.notNull(entity.getMargin(), "margin");
        }
    }
}
