package com.pls.dtobuilder.savedquote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.address.RouteEntity;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.shared.Status;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.dto.shipment.ShipmentDetailsDTO;
import com.pls.dto.shipment.ShipmentFinishOrderDTO;
import com.pls.dto.shipment.ShipmentMaterialDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.dtobuilder.address.ZipDTOBuilder;
import com.pls.dtobuilder.shipment.SavedQuotePricDtlsDTOBuilder;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.PricingDetailsBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;
import com.pls.shipment.domain.SavedQuoteAccessorialEntity;
import com.pls.shipment.domain.SavedQuoteCostDetailsEntity;
import com.pls.shipment.domain.SavedQuoteCostDetailsItemEntity;
import com.pls.shipment.domain.SavedQuoteEntity;
import com.pls.shipment.domain.SavedQuoteMaterialEntity;
import com.pls.shipment.domain.SavedQuotePricDtlsEntity;
import com.pls.shipment.service.ShipmentService;

/**
 * Saved Quote DTO builder.
 * 
 * @author Mikhail Boldinov, 27/03/13
 * @author Aleksandr Leshchenko
 */
public class SavedQuoteDTOBuilder extends AbstractDTOBuilder<SavedQuoteEntity, ShipmentDTO> {
    private static final CarrierDTOBuilder CARRIER_DTO_BUILDER = new CarrierDTOBuilder();

    private final SavedQuoteMaterialDTOBuilder savedQuoteMaterialDTOBuilder = new SavedQuoteMaterialDTOBuilder();

    private final ZipDTOBuilder zipDTOBuilder = new ZipDTOBuilder();

    private final DataProvider dataProvider;

    private static final SavedQuotePricDtlsDTOBuilder SAVED_QUOTE_PRIC_DTLS_BUILDER = new SavedQuotePricDtlsDTOBuilder();

    /**
     * Constructor with data provider.
     *
     * @param dataProvider
     *            data provider
     */
    public SavedQuoteDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        savedQuoteMaterialDTOBuilder.setDataProvider(dataProvider);
    }

    @Override
    public ShipmentDTO buildDTO(SavedQuoteEntity entity) {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setQuoteId(entity.getId());
        dto.setVolumeQuoteID(entity.getVolumeQuoteId());
        if (entity.getCustomer() != null) {
            dto.setOrganizationId(entity.getCustomer().getId());
            dto.setCustomerName(entity.getCustomer().getName());
            dto.setProductListPrimarySort(entity.getCustomer().getProductListPrimarySort());
        }

        dto.setOriginDetails(buildShipmentDetailsDTO(entity, true));
        dto.setDestinationDetails(buildShipmentDetailsDTO(entity, false));

        buildProposition(dto, entity);
        dto.setFinishOrder(buildFinishOrder(entity));
        dto.setStatus(ShipmentStatus.OPEN);
        dto.setProNumber(entity.getCarrierReferenceNumber());
        dto.setBolNumber(entity.getBol());
        if (entity.getModification() != null) {
            dto.setCreatedDate(entity.getModification().getCreatedDate());
        }
        return dto;
    }

    @Override
    public SavedQuoteEntity buildEntity(ShipmentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException(NULL_DTO);
        }
        SavedQuoteEntity entity = getQuote(dto.getQuoteId());

        buildRouteEntity(entity, dto);
        entity.setBol(dto.getBolNumber());
        entity.setVolumeQuoteId(dto.getVolumeQuoteID());
        if (dto.getSelectedProposition() != null && dto.getSelectedProposition().getPricingDetails() != null) {
            updateSavedQuoteWithPricingDetails(entity, dto.getSelectedProposition().getPricingDetails());
        }
        if (dto.getFinishOrder() != null) {
            ShipmentFinishOrderDTO finishOrderDTO = dto.getFinishOrder();
            entity.setPickupDate(finishOrderDTO.getPickupDate());

            entity.setPoNum(finishOrderDTO.getPoNumber());
            entity.setPickupNum(finishOrderDTO.getPuNumber());

            entity.setSoNumber(finishOrderDTO.getSoNumber());
            entity.setGlNumber(finishOrderDTO.getGlNumber());
            entity.setTrailer(finishOrderDTO.getTrailerNumber());
            entity.setQuoteReferenceNumber(finishOrderDTO.getRef());
            buildQuoteMaterials(entity, dto);
        }
        buildAccessorialsEntity(entity, dto);

        entity.setStatus(Status.ACTIVE);
        entity.setCarrierReferenceNumber(dto.getProNumber());
        return entity;
    }

    private void updateSavedQuoteWithPricingDetails(SavedQuoteEntity entity, PricingDetailsBO pricingDetails) {
        if (entity.getSavedQuotePricDtls() == null) {
            entity.setSavedQuotePricDtls(new HashSet<SavedQuotePricDtlsEntity>());
        }
        SavedQuotePricDtlsEntity savedQuotePricDtls = SAVED_QUOTE_PRIC_DTLS_BUILDER.buildEntity(pricingDetails);
        savedQuotePricDtls.setSavedQuote(entity);
        entity.getSavedQuotePricDtls().clear();
        entity.getSavedQuotePricDtls().add(savedQuotePricDtls);
    }

    private ShipmentDetailsDTO buildShipmentDetailsDTO(SavedQuoteEntity quote, boolean isOrigin) {
        ShipmentDetailsDTO dto = new ShipmentDetailsDTO();
        dto.setAccessorials(buildAccessorials(isOrigin ? LtlAccessorialGroup.PICKUP : LtlAccessorialGroup.DELIVERY,
                quote.getAccessorials()));
        if (isOrigin) {
            dto.setZip(zipDTOBuilder.buildDTO(quote.getRoute().getOriginZipCode()));
        } else {
            dto.setZip(zipDTOBuilder.buildDTO(quote.getRoute().getDestinationZipCode()));
        }
        return dto;
    }

    private Collection<String> buildAccessorials(LtlAccessorialGroup accessorialGroup, Set<SavedQuoteAccessorialEntity> ltlAccessorials) {
        return ltlAccessorials.stream().filter(acc -> acc.getAccessorialType().getAccessorialGroup() == accessorialGroup)
                .map(acc -> acc.getAccessorialType().getId()).collect(Collectors.toList());
    }

    private void updateGuaranteedCostDetailItems(List<CostDetailItemBO> items, Long guaranteedBy) {
        for (CostDetailItemBO item : items) {
            if (ShipmentService.GUARANTEED_SERVICE_REF_TYPE.equals(item.getRefType())) {
                item.setGuaranteedBy(guaranteedBy);
            }
        }
    }

    private void buildProposition(ShipmentDTO dto, SavedQuoteEntity entity) {
        ShipmentProposalBO proposition = new ShipmentProposalBO();
        SavedQuoteCostDetailsEntity costDetailsEntity = entity.getCostDetails();
        proposition.setMileage(entity.getMileage());
        proposition.setCarrier(CARRIER_DTO_BUILDER.buildDTO(entity.getCarrier()));
        if (proposition.getCarrier() != null) {
            proposition.getCarrier().setSpecialMessage(entity.getSpecialMessage());
        }
        proposition.setServiceType(costDetailsEntity.getServiceType());
        proposition.setEstimatedTransitTime(costDetailsEntity.getEstimatedTransitTime());
        proposition.setEstimatedTransitDate(costDetailsEntity.getEstTransitDate());
        proposition.setNewLiability(costDetailsEntity.getNewLiability());
        proposition.setUsedLiability(costDetailsEntity.getUsedLiability());
        proposition.setProhibited(costDetailsEntity.getProhibitedCommodities());
        proposition.setPricingProfileId(costDetailsEntity.getPricingProfileDetailId());
        proposition.setCostDetailItems(buildCostDetailItemsDTO(costDetailsEntity.getCostDetailsItems()));
        proposition.setCostOverride(entity.getCostOverride());
        proposition.setRevenueOverride(entity.getRevenueOverride());
        if (costDetailsEntity.getGuaranteedBy() != null) {
            updateGuaranteedCostDetailItems(proposition.getCostDetailItems(), costDetailsEntity.getGuaranteedBy());
            dto.setGuaranteedBy(costDetailsEntity.getGuaranteedBy());
        }
        proposition.setBlockedFrmBkng(entity.getBlockedFromBooking());
        proposition.setExternalUuid(entity.getExternalUuid());
        proposition.setCarrierQuoteNumber(entity.getCarrierQuoteNumber());
        proposition.setServiceLevelCode(entity.getServiceLevelCode());
        proposition.setServiceLevelDescription(entity.getServiceLevelDescription());
        if(entity.getVolumeQuoteId() != null) {
            proposition.setShipmentType(ShipmentType.VLTL);
        }
        dto.setSelectedProposition(proposition);
    }

    private List<CostDetailItemBO> buildCostDetailItemsDTO(Set<SavedQuoteCostDetailsItemEntity> costDetailsItems) {
        List<CostDetailItemBO> dtoItems = new ArrayList<CostDetailItemBO>();

        for (SavedQuoteCostDetailsItemEntity entity : costDetailsItems) {
            CostDetailItemBO dto = new CostDetailItemBO();
            dto.setRefType(entity.getRefType());
            dto.setSubTotal(entity.getSubTotal());
            dto.setCostDetailOwner(entity.getOwner());
            dtoItems.add(dto);
        }

        return dtoItems;
    }

    private ShipmentFinishOrderDTO buildFinishOrder(SavedQuoteEntity entity) {
        ShipmentFinishOrderDTO dto = new ShipmentFinishOrderDTO();

        dto.setPickupDate(entity.getPickupDate());

        dto.setRef(entity.getQuoteReferenceNumber());
        dto.setPoNumber(entity.getPoNum());
        dto.setPuNumber(entity.getPickupNum());
        dto.setSoNumber(entity.getSoNumber());
        dto.setGlNumber(entity.getGlNumber());
        dto.setTrailerNumber(entity.getTrailer());

        dto.setQuoteMaterials(buildQuoteMaterials(entity));
        return dto;
    }

    private List<ShipmentMaterialDTO> buildQuoteMaterials(SavedQuoteEntity entity) {
        if (entity.getMaterials() != null) {
            return savedQuoteMaterialDTOBuilder
                    .buildList(new ArrayList<SavedQuoteMaterialEntity>(entity.getMaterials()));
        }
        return Collections.emptyList();
    }

    private void buildQuoteMaterials(SavedQuoteEntity entity, ShipmentDTO dto) {
        Set<SavedQuoteMaterialEntity> entityMaterials = entity.getMaterials();
        if (entityMaterials == null) {
            entityMaterials = new HashSet<SavedQuoteMaterialEntity>();
            entity.setMaterials(entityMaterials);
        }
        List<ShipmentMaterialDTO> dtoMaterials = dto.getFinishOrder().getQuoteMaterials();
        for (ShipmentMaterialDTO material : dtoMaterials) {
            Long materialId = dto.getQuoteId() == null ? null : material.getId();
            SavedQuoteMaterialEntity materialEntity = findById(entityMaterials, materialId);
            if (materialEntity == null) {
                SavedQuoteMaterialEntity quoteMaterialEntity = savedQuoteMaterialDTOBuilder.buildEntity(material);
                quoteMaterialEntity.setQuote(entity);
                entityMaterials.add(quoteMaterialEntity);
            } else {
                savedQuoteMaterialDTOBuilder.updateEntity(materialEntity, material);
            }
        }
        // process removed materials
        if (entityMaterials.size() != dtoMaterials.size()) {
            Iterator<SavedQuoteMaterialEntity> materialsIterator = entityMaterials.iterator();
            while (materialsIterator.hasNext()) {
                if (isMaterialRemoved(dtoMaterials, materialsIterator.next().getId())) {
                    materialsIterator.remove();
                }
            }
        }
    }

    private SavedQuoteMaterialEntity findById(Set<SavedQuoteMaterialEntity> entityMaterials, Long id) {
        SavedQuoteMaterialEntity result = null;
        for (SavedQuoteMaterialEntity material : entityMaterials) {
            Long materialId = material.getId();
            if (materialId != null && materialId.equals(id)) {
                result = material;
                break;
            }
        }
        return result;
    }

    private boolean isMaterialRemoved(List<ShipmentMaterialDTO> dtoMaterials, Long id) {
        if (id == null) {
            return false;
        }
        for (ShipmentMaterialDTO materialDTO : dtoMaterials) {
            if (id.equals(materialDTO.getId())) {
                return false;
            }
        }
        return true;
    }

    private void buildRouteEntity(SavedQuoteEntity entity, ShipmentDTO dto) {
        RouteEntity route = entity.getRoute();
        if (route == null) {
            route = new RouteEntity();
            entity.setRoute(route);
        }
        route.setCreatedDate(new Date());
        route.setOriginZip(dto.getOriginDetails().getZip().getZip());
        route.setOriginCountry(dto.getOriginDetails().getZip().getCountry().getId());
        route.setOriginState(dto.getOriginDetails().getZip().getState());
        route.setOriginCity(dto.getOriginDetails().getZip().getCity());

        route.setDestZip(dto.getDestinationDetails().getZip().getZip());
        route.setDestCountry(dto.getDestinationDetails().getZip().getCountry().getId());
        route.setDestState(dto.getDestinationDetails().getZip().getState());
        route.setDestCity(dto.getDestinationDetails().getZip().getCity());
    }

    private void buildAccessorialsEntity(SavedQuoteEntity quote, ShipmentDTO dto) {
        if (quote.getAccessorials() == null) {
            quote.setAccessorials(new HashSet<SavedQuoteAccessorialEntity>());
        }
        if (!quote.getAccessorials().isEmpty()) {
            quote.getAccessorials().clear();
        }
        addAccessorials(quote, dto.getOriginDetails().getAccessorials(), LtlAccessorialGroup.PICKUP);
        addAccessorials(quote, dto.getDestinationDetails().getAccessorials(), LtlAccessorialGroup.DELIVERY);
    }

    private void addAccessorials(SavedQuoteEntity quote, Collection<String> accessorials,
            LtlAccessorialGroup accessorialGroup) {
        if (accessorials != null) {
            for (String accessorial : accessorials) {
                SavedQuoteAccessorialEntity entity = new SavedQuoteAccessorialEntity();
                entity.setSavedQuote(quote);
                AccessorialTypeEntity accessorialType = new AccessorialTypeEntity();
                accessorialType.setId(accessorial);
                accessorialType.setAccessorialGroup(accessorialGroup);
                entity.setAccessorialType(accessorialType);
                quote.getAccessorials().add(entity);
            }
        }
    }

    private SavedQuoteEntity getQuote(Long id) {
        if (id == null) {
            return new SavedQuoteEntity();
        }
        return dataProvider.findSavedQuoteById(id);
    }

    /**
     * Data provider for package type.
     *
     * @author Sergey Kirichenko
     */
    public interface DataProvider extends SavedQuoteMaterialDTOBuilder.DataProvider {
        /**
         * Find existing saved quote by id.
         *
         * @param id
         *            quote id
         * @return {@link SavedQuoteEntity}
         */
        SavedQuoteEntity findSavedQuoteById(Long id);
    }
}
