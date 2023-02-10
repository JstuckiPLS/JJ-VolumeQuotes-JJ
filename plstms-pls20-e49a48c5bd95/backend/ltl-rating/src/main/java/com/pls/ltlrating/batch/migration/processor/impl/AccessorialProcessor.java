package com.pls.ltlrating.batch.migration.processor.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemType;
import com.pls.ltlrating.dao.LtlAccessorialsDao;
import com.pls.ltlrating.dao.LtlImportExportPricesDao;
import com.pls.ltlrating.domain.LtlAccGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlAccGeoServicesEntity;
import com.pls.ltlrating.domain.LtlAccessorialsEntity;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.service.impl.GeoHelper;

/**
 * Accessorial processor.
 *
 * @author Alex Kyrychenko
 */
public class AccessorialProcessor extends AbstractPriceItemProcessor<LtlAccessorialsEntity> {

    @Autowired
    private LtlAccessorialsDao accessorialsDao;

    @Autowired
    private LtlImportExportPricesDao importExportPricesDao;

    /**
     * Constructor.
     */
    public AccessorialProcessor() {
        super(LtlPricingItemType.ACCESSORIAL);
    }

    @Override
    protected LtlAccessorialsEntity toEntity(final LtlPricingItem pricingItem) {
        LtlAccessorialsEntity accessorialsEntity = new LtlAccessorialsEntity();
        accessorialsEntity.setId(pricingItem.getItemId());
        accessorialsEntity.setLtlPricProfDetailId(pricingItem.getProfileDetailId());
        accessorialsEntity.setAccessorialType(pricingItem.getItemName());

        setCostRelatedProps(pricingItem, accessorialsEntity);

        if (pricingItem.getMarginType() != null) {
            accessorialsEntity.setMarginType(pricingItem.getMarginType().getPersistentEnum().name());
        }
        accessorialsEntity.setUnitMargin(pricingItem.getUnitMargin());
        accessorialsEntity.setMarginPercent(pricingItem.getMarginPercent());
        accessorialsEntity.setMarginDollarAmt(pricingItem.getMarginDollarAmt());
        accessorialsEntity.setEffDate(pricingItem.getEffectiveFrom());
        accessorialsEntity.setExpDate(pricingItem.getEffectiveTo());
        if (pricingItem.getServiceType() != null) {
            accessorialsEntity.setServiceType(pricingItem.getServiceType().getPersistentEnum());
        }
        accessorialsEntity.setMovementType(pricingItem.getMovementType());
        accessorialsEntity.setStatus(pricingItem.getStatus().getPersistentEnum());
        accessorialsEntity.setMinCost(pricingItem.getMinCost());
        accessorialsEntity.setMaxCost(pricingItem.getMaxCost());
        return accessorialsEntity;
    }

    private void setCostRelatedProps(final LtlPricingItem pricingItem, final LtlAccessorialsEntity accessorialsEntity) {
        accessorialsEntity.setUnitCost(pricingItem.getUnitCost());
        if (pricingItem.getCostType() != null) {
            accessorialsEntity.setCostType(pricingItem.getCostType().getPersistentEnum());
        }
        if (pricingItem.getCostApplMinWt() != null) {
            accessorialsEntity.setCostApplMinWt(pricingItem.getCostApplMinWt().longValue());
        }
        if (pricingItem.getCostApplMaxWt() != null) {
            accessorialsEntity.setCostApplMaxWt(pricingItem.getCostApplMaxWt().longValue());
        }
        if (pricingItem.getCostApplMinDist() != null) {
            accessorialsEntity.setCostApplMinDist(pricingItem.getCostApplMinDist().longValue());
        }
        if (pricingItem.getCostApplMaxDist() != null) {
            accessorialsEntity.setCostApplMaxDist(pricingItem.getCostApplMaxDist().longValue());
        }
    }

    @Override
    protected void doProcess(final LtlAccessorialsEntity entity, final LtlPricingItem item) {
        entity.setExpDate(item.getEffectiveTo());
        entity.setStatus(item.getStatus().getPersistentEnum());
        if (item.getItemId() == null) {
            processAccGeo(entity, item);
        }
    }

    private void processAccGeo(final LtlAccessorialsEntity entity, final LtlPricingItem item) {
        if (StringUtils.isNotBlank(item.getOrigin()) && StringUtils.isNotBlank(item.getDestination())) {
            entity.getLtlAccGeoServicesEntities().add(createAccGeo(item));
        }
    }

    private LtlAccGeoServicesEntity createAccGeo(final LtlPricingItem item) {
        LtlAccGeoServicesEntity accGeoServicesEntity = new LtlAccGeoServicesEntity();
        accGeoServicesEntity.setOriginAggValue(item.getOrigin());
        accGeoServicesEntity.setDestinationAggValue(item.getDestination());
        accGeoServicesEntity.setLtlAccOriginGeoServiceDetails(getAccGeoDetails(accGeoServicesEntity, item.getOrigin(), GeoType.ORIGIN));
        accGeoServicesEntity.setLtlAccDestGeoServiceDetails(getAccGeoDetails(accGeoServicesEntity, item.getDestination(), GeoType.DESTINATION));
        return accGeoServicesEntity;
    }

    private List<LtlAccGeoServiceDetailsEntity> getAccGeoDetails(final LtlAccGeoServicesEntity accGeoServicesEntity, final String geoValues,
                                                                 final GeoType geoType) {
        List<LtlAccGeoServiceDetailsEntity> accGeoDetails = new ArrayList<>();
        for (String geoValue : StringUtils.split(geoValues, ',')) {
            Pair<Integer, String> geoPair = GeoHelper.getGeoServType(geoValue);
            accGeoDetails.add(new LtlAccGeoServiceDetailsEntity(accGeoServicesEntity, geoValue, geoType, geoPair.getLeft(), geoPair.getRight()));
        }
        return accGeoDetails;
    }

    @Override
    protected AbstractDao<LtlAccessorialsEntity, Long> getDaoToPersist() {
        return accessorialsDao;
    }

    @Override
    protected LtlAccessorialsEntity findEntityInDb(final LtlPricingItem item) {
        return importExportPricesDao.findAccessorialByPricingItem(item);
    }
}
