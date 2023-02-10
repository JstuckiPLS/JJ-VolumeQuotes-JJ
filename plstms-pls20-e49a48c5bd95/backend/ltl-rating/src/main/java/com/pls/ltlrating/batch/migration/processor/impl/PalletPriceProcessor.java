package com.pls.ltlrating.batch.migration.processor.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemType;
import com.pls.ltlrating.dao.LtlImportExportPricesDao;
import com.pls.ltlrating.dao.LtlPalletPricingDetailsDao;
import com.pls.ltlrating.dao.LtlZonesDao;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlZoneGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlZoneGeoServicesEntity;
import com.pls.ltlrating.domain.LtlZonesEntity;
import com.pls.ltlrating.service.impl.GeoHelper;

/**
 * Pallet price processor.
 *
 * @author Alex Kyrychenko
 */
public class PalletPriceProcessor extends AbstractPriceItemProcessor<LtlPalletPricingDetailsEntity> {

    public static final int ZONE_NAME_MAX_LENGTH = 50;
    @Autowired
    private LtlPalletPricingDetailsDao palletPricingDetailsDao;

    @Autowired
    private LtlImportExportPricesDao importExportPricesDao;

    @Autowired
    private LtlZonesDao zonesDao;

    /**
     * Constructor accept which tipe of pricing item should be processed by this processor.
     */
    public PalletPriceProcessor() {
        super(LtlPricingItemType.PALLET);
    }

    @Override
    protected LtlPalletPricingDetailsEntity toEntity(final LtlPricingItem pricingItem) {
        LtlPalletPricingDetailsEntity entity = new LtlPalletPricingDetailsEntity();
        entity.setId(pricingItem.getItemId());
        entity.setProfileDetailId(pricingItem.getProfileDetailId());
        if (pricingItem.getMinCost() != null) {
            entity.setMinQuantity(pricingItem.getMinCost().longValue());
        }
        if (pricingItem.getMaxCost() != null) {
            entity.setMaxQuantity(pricingItem.getMaxCost().longValue());
        }
        if (pricingItem.getCostType() != null) {
            entity.setCostType(pricingItem.getCostType().getPersistentEnum());
        }
        entity.setUnitCost(pricingItem.getUnitCost());
        entity.setCostApplMinWt(pricingItem.getCostApplMinWt());
        entity.setCostApplMaxWt(pricingItem.getCostApplMaxWt());
        entity.setMarginPercent(pricingItem.getMarginPercent());
        entity.setEffDate(pricingItem.getEffectiveFrom());
        entity.setExpDate(pricingItem.getEffectiveTo());
        if (pricingItem.getUnitMargin() != null) {
            entity.setTransitTime(pricingItem.getUnitMargin().longValue());
        }
        if (pricingItem.getServiceType() != null) {
            entity.setServiceType(pricingItem.getServiceType().getPersistentEnum());
        }
        entity.setMovementType(pricingItem.getMovementType());
        return entity;
    }

    @Override
    protected void doProcess(final LtlPalletPricingDetailsEntity entity, final LtlPricingItem item) {
        entity.setExpDate(item.getEffectiveTo());
        if (item.getItemId() == null) {
            entity.setZoneFrom(processZone(entity.getZoneFrom(), item.getOrigin(), item));
            entity.setZoneTo(processZone(entity.getZoneTo(), item.getDestination(), item));
        }
        entity.setStatus(item.getStatus() == null ? Status.ACTIVE : item.getStatus().getPersistentEnum());
    }

    private Long processZone(final Long zoneId, final String geoValue, final LtlPricingItem item) {
        LtlZonesEntity zonesEntity;
        if (zoneId == null) {
            zonesEntity = new LtlZonesEntity();
            zonesEntity.setName(item.getScac() + " - " + item.getProfileDetailId() + geoValue);
            zonesEntity.setLtlPricProfDetailId(item.getProfileDetailId());
        } else {
            zonesEntity = zonesDao.find(zoneId);
            zonesEntity.setName(zonesEntity.getName() + "|" + geoValue);
        }
        zonesEntity.setStatus(item.getStatus() == null ? Status.ACTIVE : item.getStatus().getPersistentEnum());
        zonesEntity.setName(StringUtils.left(zonesEntity.getName(), ZONE_NAME_MAX_LENGTH));
        if (CollectionUtils.isEmpty(zonesEntity.getLtlZoneGeoServicesEntities())) {
            zonesEntity.getLtlZoneGeoServicesEntities().add(new LtlZoneGeoServicesEntity());
        }
        processGeos(zonesEntity, geoValue);
        zonesDao.saveOrUpdate(zonesEntity);
        return zonesEntity.getId();
    }

    private void processGeos(final LtlZonesEntity zonesEntity, final String geoValue) {
        LtlZoneGeoServicesEntity geoServicesEntity = zonesEntity.getLtlZoneGeoServicesEntities().iterator().next();
        if (geoServicesEntity.getId() == null) {
            zonesDao.saveOrUpdate(zonesEntity);
        }
        geoServicesEntity.getLtlZoneGeoServiceDetails().add(createGeoServDetails(geoValue, geoServicesEntity.getId()));
    }

    private LtlZoneGeoServiceDetailsEntity createGeoServDetails(final String geoValue, final Long geoId) {
        Pair<Integer, String> ltlZoneGeoPair = GeoHelper.getGeoServType(geoValue);
        return new LtlZoneGeoServiceDetailsEntity(geoId, geoValue.trim(), ltlZoneGeoPair.getLeft(), ltlZoneGeoPair.getRight());
    }

    @Override
    protected AbstractDao<LtlPalletPricingDetailsEntity, Long> getDaoToPersist() {
        return palletPricingDetailsDao;
    }

    @Override
    protected LtlPalletPricingDetailsEntity findEntityInDb(final LtlPricingItem item) {
        return importExportPricesDao.findPalletByPricingItem(item);
    }
}
