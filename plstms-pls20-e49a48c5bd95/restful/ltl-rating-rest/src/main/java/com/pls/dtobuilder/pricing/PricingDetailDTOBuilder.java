package com.pls.dtobuilder.pricing;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.dto.PricingAddressDTO;
import com.pls.dto.PricingDetailDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.ltlrating.domain.LtlFakMapEntity;
import com.pls.ltlrating.domain.LtlPricGeoServiceDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingGeoServicesEntity;
import com.pls.ltlrating.domain.enums.GeoType;
import com.pls.ltlrating.service.impl.GeoHelper;

/**
 * DTO Builder for {@link LtlPricingDetailsEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public class PricingDetailDTOBuilder extends AbstractDTOBuilder<LtlPricingDetailsEntity, PricingDetailDTO> {

    private DataProvider dataProvider;

    /**
     * Constructor.
     * 
     * @param dataProvider
     *            data provider
     */
    public PricingDetailDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public PricingDetailDTO buildDTO(LtlPricingDetailsEntity bo) {
        PricingDetailDTO dto = new PricingDetailDTO();
        dto.setId(bo.getId());
        dto.setProfileDetailId(bo.getLtlPricProfDetailId());
        dto.setMarginType(bo.getMarginType());
        dto.setMarginAmount(bo.getMarginAmount());
        dto.setMinMargin(bo.getMinMarginAmount());

        LtlPricingDetailsEntity entity = bo;
        if (bo.getParentPricingDetails() != null) {
            entity = bo.getParentPricingDetails();
        }
        dto.setCostType(entity.getCostType());
        dto.setCostAmount(entity.getUnitCost());
        dto.setMinWeight(entity.getCostApplMinWt());
        dto.setMaxWeight(entity.getCostApplMaxWt());
        dto.setMinDistance(entity.getCostApplMinDist());
        dto.setMaxDistance(entity.getCostApplMaxDist());
        dto.setMinCost(entity.getMinCost());
        dto.setEffDate(entity.getEffDate());
        dto.setExpDate(entity.getExpDate());
        dto.setServiceType(entity.getServiceType());
        dto.setSmcTariff(entity.getSmcTariff());
        dto.setFreightClass(entity.getCommodityClass());
        dto.setMovementType(entity.getMovementType());
        setFakMappingDTO(dto, entity.getFakMapping());
        dto.setAddresses(getAddressesDTO(entity.getGeoServices()));
        return dto;
    }

    private List<PricingAddressDTO> getAddressesDTO(Set<LtlPricingGeoServicesEntity> ltlPricingGeoServicesEntities) {
        if (CollectionUtils.isNotEmpty(ltlPricingGeoServicesEntities)) {
            return ltlPricingGeoServicesEntities.stream().map(this::convertToAddressDTO).collect(Collectors.toList());
        }
        return null;
    }

    private PricingAddressDTO convertToAddressDTO(LtlPricingGeoServicesEntity e) {
        PricingAddressDTO address = new PricingAddressDTO();
        address.setId(e.getId());
        address.setOrigin(toAddressString(e.getOriginDetails()));
        address.setDestination(toAddressString(e.getDestinationDetails()));
        return address;
    }

    private String toAddressString(Set<LtlPricGeoServiceDetailsEntity> geoDetails) {
        return geoDetails.stream().map(LtlPricGeoServiceDetailsEntity::getGeoValue).sorted().collect(Collectors.joining(","));
    }

    private void setFakMappingDTO(PricingDetailDTO dto, Set<LtlFakMapEntity> ltlFakMapEntities) {
        if (CollectionUtils.isNotEmpty(ltlFakMapEntities)) {
            dto.setFakMapping(
                    ltlFakMapEntities.stream().collect(Collectors.toMap(LtlFakMapEntity::getActualClass, LtlFakMapEntity::getMappingClass)));
        }
    }

    @Override
    public LtlPricingDetailsEntity buildEntity(PricingDetailDTO dto) {
        LtlPricingDetailsEntity entity;
        if (dto.getId() != null) {
            entity = dataProvider.getById(dto.getId());

            // if profile detail id is different, then we are saving new price for Blanket/CSP profile
            if (!dto.getProfileDetailId().equals(entity.getLtlPricProfDetailId()) || entity.getParentPricingDetails() != null) {
                if (entity.getParentPricingDetails() == null) {
                    // if parent pricing details is null, then we are saving new pricing details
                    entity = getPricingDetailsEntityWithParent(entity, dto.getProfileDetailId());
                }
                entity.setMarginType(dto.getMarginType());
                entity.setMarginAmount(dto.getMarginAmount());
                entity.setMinMarginAmount(dto.getMinMargin());
                return entity;
            }
        } else {
            entity = new LtlPricingDetailsEntity();
        }
        entity.setId(dto.getId());
        entity.setLtlPricProfDetailId(dto.getProfileDetailId());
        entity.setCostType(dto.getCostType());
        entity.setUnitCost(dto.getCostAmount());
        entity.setCostApplMinWt(dto.getMinWeight());
        entity.setCostApplMaxWt(dto.getMaxWeight());
        entity.setCostApplMinDist(dto.getMinDistance());
        entity.setCostApplMaxDist(dto.getMaxDistance());
        entity.setMinCost(dto.getMinCost());
        entity.setMarginType(dto.getMarginType());
        entity.setMarginAmount(dto.getMarginAmount());
        entity.setMinMarginAmount(dto.getMinMargin());
        entity.setEffDate(dto.getEffDate());
        entity.setExpDate(dto.getExpDate());
        entity.setServiceType(dto.getServiceType());
        entity.setSmcTariff(dto.getSmcTariff());
        entity.setCommodityClass(dto.getFreightClass());
        entity.setMovementType(dto.getMovementType());
        setFakMappingEntity(entity, dto.getFakMapping());
        setAddressesEntity(entity, dto.getAddresses());
        return entity;
    }

    private LtlPricingDetailsEntity getPricingDetailsEntityWithParent(LtlPricingDetailsEntity parentEntity, Long profileDetailId) {
        LtlPricingDetailsEntity entity = new LtlPricingDetailsEntity();
        entity.setParentPricingDetails(parentEntity);
        entity.setLtlPricProfDetailId(profileDetailId);
        return entity;
    }

    private void setAddressesEntity(LtlPricingDetailsEntity entity, List<PricingAddressDTO> addresses) {
        entity.getGeoServices().removeIf(e -> addresses.stream().noneMatch(a -> ObjectUtils.equals(a.getId(), e.getId())));
        for (PricingAddressDTO address : addresses) {
            if (address.getId() != null) {
                LtlPricingGeoServicesEntity geoEntity = entity.getGeoServices().stream()
                        .filter(e -> ObjectUtils.equals(address.getId(), e.getId())).findFirst().get();
                updateGeoDetails(geoEntity.getOriginDetails(), address.getOrigin(), geoEntity, GeoType.ORIGIN);
                updateGeoDetails(geoEntity.getDestinationDetails(), address.getDestination(), geoEntity, GeoType.DESTINATION);
            } else {
                LtlPricingGeoServicesEntity geoEntity = new LtlPricingGeoServicesEntity();
                geoEntity.setPricingDetail(entity);
                geoEntity.setOriginDetails(getGeoServiceDetails(geoEntity, address.getOrigin(), GeoType.ORIGIN));
                geoEntity.setDestinationDetails(getGeoServiceDetails(geoEntity, address.getDestination(), GeoType.DESTINATION));
                entity.getGeoServices().add(geoEntity);
            }
        }
    }

    private void updateGeoDetails(Set<LtlPricGeoServiceDetailsEntity> geoDetails, String geoValue, LtlPricingGeoServicesEntity geoService,
            GeoType geoType) {
        String[] geoCodes = StringUtils.split(geoValue, ',');
        geoDetails.removeIf(geoDetail -> !ArrayUtils.contains(geoCodes, geoDetail.getGeoValue()));
        Stream.of(geoCodes).filter(StringUtils::isNotBlank).forEach(geoCode -> {
            if (geoDetails.stream().noneMatch(geoDetail -> geoCode.equals(geoDetail.getGeoValue()))) {
                geoDetails.add(getGeoDetail(geoCode, geoService, geoType));
            }
        });
    }

    private Set<LtlPricGeoServiceDetailsEntity> getGeoServiceDetails(LtlPricingGeoServicesEntity geoService, String geoValue, GeoType geoType) {
        return Stream.of(StringUtils.split(geoValue, ',')).filter(StringUtils::isNotBlank)
                .map(geoCode -> getGeoDetail(geoCode, geoService, geoType)).collect(Collectors.toSet());
    }

    private LtlPricGeoServiceDetailsEntity getGeoDetail(String geoCode, LtlPricingGeoServicesEntity geoService, GeoType geoType) {
        Pair<Integer, String> geoPair = GeoHelper.getGeoServType(geoCode);
        LtlPricGeoServiceDetailsEntity geoDetail = new LtlPricGeoServiceDetailsEntity();
        geoDetail.setGeoService(geoService);
        geoDetail.setGeoValue(geoCode.trim());
        geoDetail.setGeoType(geoType);
        geoDetail.setGeoServType(geoPair.getLeft());
        geoDetail.setGeoValueSearchable(geoPair.getRight());
        return geoDetail;
    }

    private void setFakMappingEntity(LtlPricingDetailsEntity entity, Map<CommodityClass, CommodityClass> fakMapping) {
        for (CommodityClass commodityClass : CommodityClass.values()) {
            if (fakMapping.get(commodityClass) != null) {
                entity.getFakMapping().stream().filter(e -> e.getActualClass() == commodityClass).findFirst().orElseGet(() -> {
                    LtlFakMapEntity mapEntity = new LtlFakMapEntity();
                    mapEntity.setPricingDetail(entity);
                    mapEntity.setActualClass(commodityClass);
                    entity.getFakMapping().add(mapEntity);
                    return mapEntity;
                }).setMappingClass(fakMapping.get(commodityClass));
            } else {
                entity.getFakMapping().removeIf(e -> e.getActualClass() == commodityClass);
            }
        }
    }

    /**
     * Data provider to build entity.
     */
    public interface DataProvider {
        /**
         * Get pricing detail by id.
         * 
         * @param id
         *            {@link LtlPricingDetailsEntity#getId()}
         * @return {@link LtlPricingDetailsEntity}
         */
        LtlPricingDetailsEntity getById(Long id);
    }
}
