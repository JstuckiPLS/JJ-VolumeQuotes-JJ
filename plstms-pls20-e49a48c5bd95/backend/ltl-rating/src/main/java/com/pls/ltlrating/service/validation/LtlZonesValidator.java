package com.pls.ltlrating.service.validation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pls.core.service.validation.ValidationError;
import com.pls.core.service.validation.support.AbstractValidator;
import com.pls.ltlrating.dao.LtlZonesDao;
import com.pls.ltlrating.domain.LtlZoneGeoServicesEntity;
import com.pls.ltlrating.domain.LtlZonesEntity;

/**
 * {@link LtlZonesEntity} validator.
 *
 * @author Artem Arapov
 *
 */
@Component
public class LtlZonesValidator extends AbstractValidator<LtlZonesEntity> {

    private static final int LOCATION_MAX_LENGTH = 500;

    @Autowired
    private LtlZonesDao dao;

    public void setDao(LtlZonesDao dao) {
        this.dao = dao;
    }

    @Override
    protected void validateImpl(LtlZonesEntity entity) {
        asserts.notNull(entity.getLtlPricProfDetailId(), "profile detail id");
        asserts.notNull(entity.getName(), "name");
        asserts.notNull(entity.getStatus(), "status");
        for (LtlZoneGeoServicesEntity item : entity.getLtlZoneGeoServicesEntities()) {
            if (item.getLocation().length() > LOCATION_MAX_LENGTH) {
                asserts.fail("ltlZoneGeoServicesEntities.location", ValidationError.GREATER_THAN);
            }
        }
        LtlZonesEntity existedZone = dao.findZoneByProfileDetailIdAndName(entity.getLtlPricProfDetailId(), entity.getName());
        if (existedZone != null && !existedZone.getId().equals(entity.getId())) {
            asserts.fail("zoneName", ValidationError.UNIQUE);
        }
    }

}