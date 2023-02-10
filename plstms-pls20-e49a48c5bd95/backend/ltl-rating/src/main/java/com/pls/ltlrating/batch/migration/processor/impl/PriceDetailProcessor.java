package com.pls.ltlrating.batch.migration.processor.impl;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.ltlrating.batch.migration.model.LtlPricingItem;
import com.pls.ltlrating.batch.migration.model.enums.LtlPricingItemType;
import com.pls.ltlrating.dao.LtlImportExportPricesDao;
import com.pls.ltlrating.dao.LtlPricingDetailsDao;
import com.pls.ltlrating.domain.LtlFakMapEntity;
import com.pls.ltlrating.domain.LtlPricGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingGeoServicesEntity;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.service.impl.GeoHelper;
import com.pls.ltlrating.service.impl.GeoOptimizingHelper;

/**
 * Price detail processor.
 *
 * @author Alex Kyrychenko
 */
public class PriceDetailProcessor extends AbstractPriceItemProcessor<LtlPricingDetailsEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(PriceDetailProcessor.class);

    @Autowired
    private LtlPricingDetailsDao pricingDetailsDao;

    @Autowired
    private LtlImportExportPricesDao importExportPricesDao;

    @Autowired
    private GeoOptimizingHelper geoHelper;

    /**
     * Constructor.
     */
    public PriceDetailProcessor() {
        super(LtlPricingItemType.PRICE);
    }

    @Override
    protected void doProcess(final LtlPricingDetailsEntity entity, final LtlPricingItem item) {
        if (entity.getId() == null) {
            processFAKMapping(entity, item);
        }
        entity.setExpDate(item.getEffectiveTo());
        entity.setStatus(item.getStatus().getPersistentEnum());
        if (item.getItemId() == null) {
            processGeo(entity, item);
        }
    }

    @Override
    protected AbstractDao<LtlPricingDetailsEntity, Long> getDaoToPersist() {
        return pricingDetailsDao;
    }

    @Override
    protected LtlPricingDetailsEntity findEntityInDb(final LtlPricingItem item) {
        return importExportPricesDao.findPricingDetailByPricingItem(item);
    }

    @Override
    protected LtlPricingDetailsEntity toEntity(final LtlPricingItem pricingItem) {
        LtlPricingDetailsEntity pricingDetailsEntity = new LtlPricingDetailsEntity();
        pricingDetailsEntity.setId(pricingItem.getItemId());
        pricingDetailsEntity.setLtlPricProfDetailId(pricingItem.getProfileDetailId());
        if (pricingItem.getCostType() != null) {
            pricingDetailsEntity.setCostType(pricingItem.getCostType().getPersistentEnum());
        }
        pricingDetailsEntity.setUnitCost(pricingItem.getUnitCost());
        pricingDetailsEntity.setCostApplMinWt(pricingItem.getCostApplMinWt());
        pricingDetailsEntity.setCostApplMaxWt(pricingItem.getCostApplMaxWt());
        pricingDetailsEntity.setCostApplMinDist(pricingItem.getCostApplMinDist());
        pricingDetailsEntity.setCostApplMaxDist(pricingItem.getCostApplMaxDist());
        if (pricingItem.getMinCost() != null) {
            pricingDetailsEntity.setMinCost(pricingItem.getMinCost());
        }
        if (pricingItem.getMarginType() != null) {
            pricingDetailsEntity.setMarginType(pricingItem.getMarginType().getPersistentEnum());
        }
        pricingDetailsEntity.setMarginAmount(pricingItem.getUnitMargin());
        pricingDetailsEntity.setMinMarginAmount(pricingItem.getMarginDollarAmt());
        pricingDetailsEntity.setEffDate(pricingItem.getEffectiveFrom());
        pricingDetailsEntity.setExpDate(pricingItem.getEffectiveTo());
        if (pricingItem.getServiceType() != null) {
            pricingDetailsEntity.setServiceType(pricingItem.getServiceType().getPersistentEnum());
        }
        pricingDetailsEntity.setSmcTariff(pricingItem.getItemName());
        pricingDetailsEntity.setMovementType(pricingItem.getMovementType());
        pricingDetailsEntity.setMinCost(pricingItem.getMinCost());
        return pricingDetailsEntity;
    }

    private void processFAKMapping(final LtlPricingDetailsEntity entity, final LtlPricingItem item) {
        for (Field property : LtlPricingItem.class.getDeclaredFields()) {
            if (property.getName().matches("ac\\d{2,3}")) {
                Object fakValue = getFAKValue(item, property);
                if (fakValue != null) {
                    processFAK(entity, fakValue, property.getName());
                }
            }
        }
    }

    private void processFAK(final LtlPricingDetailsEntity entity, Object fakValue, String propertyName) {
        CommodityClass actualClass = CommodityClass.convertFromDbCode(correctFAK(propertyName.substring(2)));
        if (entity.getFakMapping().stream().noneMatch(m -> m.getActualClass() == actualClass)) {
            LtlFakMapEntity ltlFakMapEntity = new LtlFakMapEntity();
            ltlFakMapEntity.setPricingDetail(entity);
            ltlFakMapEntity.setActualClass(actualClass);
            ltlFakMapEntity.setMappingClass(CommodityClass.convertFromDbCode(correctFAK(String.valueOf(fakValue))));
            entity.getFakMapping().add(ltlFakMapEntity);
        }
    }

    private Object getFAKValue(final LtlPricingItem item, Field property) {
        Object fakValue = null;
        try {
            property.setAccessible(true);
            fakValue = property.get(item);
        } catch (IllegalAccessException e) {
            LOG.error("Got exception on processing FAK Mapping[{}]: {}", property.getName(), e);
        }
        return fakValue;
    }

    private void processGeo(final LtlPricingDetailsEntity entity, final LtlPricingItem item) {
        if (StringUtils.isNotBlank(item.getOrigin()) && StringUtils.isNotBlank(item.getDestination())) {
            LtlPricingGeoServicesEntity newGeoService = createGeo(item);
            if (isNotDuplicateGeo(entity, newGeoService)) {
                newGeoService.setPricingDetail(entity);
                entity.getGeoServices().add(newGeoService);
                improveGeoDetails(entity);
            }
        }
    }

    private void improveGeoDetails(final LtlPricingDetailsEntity entity) {
        if (entity.getId() == null) {
            // TODO because of concurrency issues we can't update existing geo details.
            // it's better to run improving geo details as the main job is complete
            geoHelper.improveGeoDetails(entity);
        }
    }

    private boolean isNotDuplicateGeo(LtlPricingDetailsEntity entity, LtlPricingGeoServicesEntity newGeoService) {
        String str = getGeoServiceString(newGeoService);
        return entity.getGeoServices().stream().noneMatch(item -> getGeoServiceString(item).equals(str));
    }

    private String getGeoServiceString(LtlPricingGeoServicesEntity newGeoService) {
        return getGeoDetailsString(newGeoService.getOriginDetails()) + '*' + getGeoDetailsString(newGeoService.getDestinationDetails());
    }

    private String getGeoDetailsString(Set<LtlPricGeoServiceDetailsEntity> details) {
        return details.stream().map(LtlPricGeoServiceDetailsEntity::getGeoValue).sorted().collect(Collectors.joining("_"));
    }

    private LtlPricingGeoServicesEntity createGeo(final LtlPricingItem item) {
        LtlPricingGeoServicesEntity geoServicesEntity = new LtlPricingGeoServicesEntity();
        geoServicesEntity.setOriginDetails(getGeoDetails(geoServicesEntity, item.getOrigin(), GeoType.ORIGIN));
        geoServicesEntity.setDestinationDetails(getGeoDetails(geoServicesEntity, item.getDestination(), GeoType.DESTINATION));
        return geoServicesEntity;
    }

    private Set<LtlPricGeoServiceDetailsEntity> getGeoDetails(final LtlPricingGeoServicesEntity geoServicesEntity, final String geoValues,
                                                               final GeoType geoType) {
        Set<LtlPricGeoServiceDetailsEntity> geoDetails = new HashSet<>();
        for (String geoValue : StringUtils.split(geoValues, ',')) {
            Pair<Integer, String> geoPair = GeoHelper.getGeoServType(geoValue);
            LtlPricGeoServiceDetailsEntity geoDetail = new LtlPricGeoServiceDetailsEntity();
            geoDetail.setGeoService(geoServicesEntity);
            geoDetail.setGeoValue(geoValue.trim());
            geoDetail.setGeoType(geoType);
            geoDetail.setGeoServType(geoPair.getLeft());
            geoDetail.setGeoValueSearchable(geoPair.getRight());
            geoDetails.add(geoDetail);
        }
        return geoDetails;
    }

    private String correctFAK(final String originFAK) {
        switch (originFAK) {
            case "77":
                return "77.5";
            case "92":
                return "92.5";
            default:
                return originFAK;
        }
    }
}
