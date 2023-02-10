package com.pls.ltlrating.batch.migration.processor.impl;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemType;
import com.pls.ltlrating.dao.LtlFuelSurchargeDao;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Fuel surcharge processor.
 *
 * @author Alex Krychenko
 */
public class FuelSurchargeProcessor extends AbstractPriceItemProcessor<LtlFuelSurchargeEntity> {

    @Autowired
    private LtlFuelSurchargeDao fuelSurchargeDao;

    /**
     * Constructor.
     */
    public FuelSurchargeProcessor() {
        super(LtlPricingItemType.FUEL);
    }

    @Override
    protected LtlFuelSurchargeEntity toEntity(final LtlPricingItem pricingItem) {
        LtlFuelSurchargeEntity entity = new LtlFuelSurchargeEntity();
        entity.setLtlPricingProfileId(pricingItem.getProfileDetailId());
        entity.setSurcharge(pricingItem.getUnitCost());
        entity.setMinRate(pricingItem.getMinCost());
        entity.setMaxRate(pricingItem.getMaxCost());
        return entity;
    }

    @Override
    protected void doProcess(final LtlFuelSurchargeEntity entity, final LtlPricingItem item) {
        entity.setStatus(item.getStatus().getPersistentEnum());
    }

    @Override
    protected AbstractDao<LtlFuelSurchargeEntity, Long> getDaoToPersist() {
        return fuelSurchargeDao;
    }

    @Override
    protected LtlFuelSurchargeEntity findEntityInDb(final LtlPricingItem item) {
        return null;
    }
}
