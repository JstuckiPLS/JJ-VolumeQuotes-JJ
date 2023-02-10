package com.pls.dtobuilder.shipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.pls.core.dao.impl.FreightBillPayToDaoImpl;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.domain.enums.PaymentTerms;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.service.util.PhoneUtils;
import com.pls.dto.address.AddressBookEntryDTO;
import com.pls.dto.address.CustomsBrokerDTO;
import com.pls.dto.enums.PaymentTermsDTO;
import com.pls.dto.shipment.PrepaidDetailDTO;
import com.pls.dto.shipment.ShipmentDTO;
import com.pls.dto.shipment.ShipmentDetailsDTO;
import com.pls.dtobuilder.AbstractDTOBuilder;
import com.pls.dtobuilder.CarrierDTOBuilder;
import com.pls.dtobuilder.FreightBillPayToDTOBuilder;
import com.pls.dtobuilder.address.AddressBookEntryDTOBuilder;
import com.pls.dtobuilder.address.BillToDTOBuilder;
import com.pls.dtobuilder.address.ZipDTOBuilder;
import com.pls.ltlrating.domain.LtlPricingProfileDetailsEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.proposal.CostDetailItemBO;
import com.pls.ltlrating.domain.bo.proposal.ShipmentProposalBO;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;
import com.pls.shipment.domain.LoadAdditionalFieldsEntity;
import com.pls.shipment.domain.LoadAdditionalInfoEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LtlLoadAccessorialEntity;
import com.pls.shipment.domain.PackageTypeEntity;
import com.pls.shipment.domain.PrepaidDetailEntity;
import com.pls.shipment.domain.bo.QuotedBO;
import com.pls.shipment.domain.enums.CommodityCd;
import com.pls.shipment.domain.enums.ShipmentSourceIndicator;
import com.pls.shipment.service.ShipmentService;

/**
 * ShipmentDTOBuilder.
 * 
 * @author Gleb Zgonikov
 */
public class ShipmentDTOBuilder extends AbstractDTOBuilder<LoadEntity, ShipmentDTO> {

    private final CarrierDTOBuilder carrierBuilder = new CarrierDTOBuilder();

    private final BillToDTOBuilder billToBuilder = new BillToDTOBuilder();

    private final CostDetailItemDTOBuilder costDetailItemBuilder = new CostDetailItemDTOBuilder();

    private final ShipmentFinishOrderDTOBuilder finishOrderBuilder = new ShipmentFinishOrderDTOBuilder();

    private DataProvider dataProvider;

    private final AddressBookEntryDTOBuilder addressBookEntryDTOBuilder = new AddressBookEntryDTOBuilder();

    private final ZipDTOBuilder zipDTOBuilder = new ZipDTOBuilder();

    private final FreightBillPayToDTOBuilder freightBillPayToDTOBuilder = new FreightBillPayToDTOBuilder();

    private final AdjustmentDTOBuilder adjustmentDTOBuilder = new AdjustmentDTOBuilder();

    /**
     * Constructor.
     * 
     * @param dataProvider
     *            data provider
     */
    public ShipmentDTOBuilder(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public ShipmentDTO buildDTO(LoadEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException(NULL_ENTITY);
        }

        ShipmentDTO dto = new ShipmentDTO();

        dto.setId(entity.getId());
        dto.setVersion(entity.getVersion());
        dto.setStatus(entity.getStatus());
        dto.setBolNumber(entity.getNumbers().getBolNumber());
        dto.setProNumber(entity.getNumbers().getProNumber());

        if (entity.getLoadAdditionalFields() != null) {
            dto.setCargoValue(entity.getLoadAdditionalFields().getCargoValue());
        }

        if (entity.getCostDetails() != null && entity.getFinalizationStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE) {
            // load may not have cost details for example when we need to get a copy of load without costs information
            dto.setInvoiceNumber(entity.getActiveCostDetail().getInvoiceNumber());
            dto.setInvoiceDate(entity.getActiveCostDetail().getGeneralLedgerDate());
            dto.setAdjustments(adjustmentDTOBuilder.buildDTO(entity));
        }
        dto.setFinalizationStatus(entity.getFinalizationStatus());
        buildProposalDTO(entity, dto);

        dto.setFinishOrder(finishOrderBuilder.buildDTO(entity));

        dto.setOriginDetails(buildShipmentDetailsDTO(entity, true));
        dto.setDestinationDetails(buildShipmentDetailsDTO(entity, false));

        if (entity.getBillTo() != null) {
            dto.setBillTo(billToBuilder.buildDTO(entity.getBillTo()));
        }

        if (entity.getModification() != null) {
            dto.setCreatedDate(entity.getModification().getCreatedDate());
        }
        buidOrganization(entity, dto);
        if (entity.getLocation() != null) {
            dto.setLocation(getLocationBO(entity.getLocation()));
        }

        buildCustomsBrokerDTO(dto, entity);
        buildFreightBillPayToDTO(dto, entity);

        dto.setPaymentTerms(PaymentTermsDTO.valueOf(entity.getPayTerms().name()));
        dto.setShipmentDirection(entity.getShipmentDirection().getCode());
        dto.setVolumeQuoteID(entity.getVolumeQuoteId());
        dto.setGenerateConsigneeInvoice(entity.getOrganization().isGenerateConsigneeInvoice());

        if (entity.getSavedQuote() != null) {
            dto.setQuoteRef(entity.getSavedQuote().getQuoteReferenceNumber());
        }
        buildQuoteFields(dto, entity.getId());
        dto.setRequirePermissionChecking(entity.getFinalizationStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING
                     || entity.getFinalizationStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        getVendorBillInformation(entity, dto);
        getMarkup(entity, dto);
        getPrepaidDetails(entity, dto);
        setHoldFinalizationStatus(entity, dto);
        return dto;
    }

    private void setHoldFinalizationStatus(LoadEntity entity, ShipmentDTO dto) {
        if (entity.getFinalizationStatus().equals(ShipmentFinancialStatus.FINANCE_HOLD)) {
            dto.setHoldFinalizationStatus(true);
        } else if (entity.getFinalizationStatus().equals(ShipmentFinancialStatus.NONE)) {
            dto.setHoldFinalizationStatus(false);
        }
    }

    private void getPrepaidDetails(LoadEntity entity, ShipmentDTO dto) {
        if (entity.getPrepaidDetails() != null && !entity.getPrepaidDetails().isEmpty()) {
            List<PrepaidDetailDTO> prepaidDetails = new ArrayList<>();
            for (PrepaidDetailEntity detailEntity : entity.getPrepaidDetails()) {
                prepaidDetails.add(new PrepaidDetailDTO(detailEntity.getId(), detailEntity.getPaymentId(), detailEntity
                        .getPaymentDate(), detailEntity.getAmount()));
            }
            dto.setPrepaidDetails(prepaidDetails);
        }
    }

    private void buidOrganization(LoadEntity entity, ShipmentDTO dto) {
        if (entity.getOrganization() != null) {
            dto.setOrganizationId(entity.getOrganization().getId());
            dto.setCustomerName(entity.getOrganization().getName());
            dto.setProductListPrimarySort(entity.getOrganization().getProductListPrimarySort());
        }
    }

    private void getMarkup(LoadEntity entity, ShipmentDTO dto) {
        LoadAdditionalInfoEntity loadAdditionalInfoEntity = entity.getLoadAdditionalInfo();
        dto.setMarkup(loadAdditionalInfoEntity != null && loadAdditionalInfoEntity.getMarkup() != null ? loadAdditionalInfoEntity
                .getMarkup() : 0L);
    }

    private void getVendorBillInformation(LoadEntity entity, ShipmentDTO dto) {
        if (entity.getVendorBillDetails() != null) {
            dto.setFreightBillDate(entity.getVendorBillDetails().getFrtBillRecvDate());
            dto.setIsVendorBillMatched(entity.getVendorBillDetails().getFrtBillRecvFlag());
            if (entity.getVendorBillDetails().getCarrierInvoiceDetails() != null
                    && !entity.getVendorBillDetails().getCarrierInvoiceDetails().isEmpty()) {
                dto.setVendorBillDate(entity.getVendorBillDetails().getCarrierInvoiceDetails().iterator().next()
                        .getInvoiceDate());
            }
        }
    }

    private ShipmentLocationBO getLocationBO(OrganizationLocationEntity locationEntity) {
        ShipmentLocationBO location = new ShipmentLocationBO();
        if (locationEntity != null) {
            location.setId(locationEntity.getId());
            location.setName(locationEntity.getLocationName());
            if (locationEntity.getBillTo() != null) {
                location.setBillToId(locationEntity.getBillTo().getId());
            }
        }
        return location;
    }

    @Override
    public LoadEntity buildEntity(ShipmentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException(NULL_DTO);
        }
        LoadEntity entity = buildLoadEntity(dto);
        holdFinalizationStatus(dto, entity);

        updateOrganizationAndLocation(entity, dto.getOrganizationId(), dto.getLocation().getId());

        entity.setCommodity(CommodityCd.MISC.name());
        if (StringUtils.isEmpty(entity.getSourceInd())) {
            entity.setSourceInd(getIndicator(dto.getMatchedVendorBillId()));
        }
        entity.setStatus(dto.getStatus());
        entity.getNumbers().setProNumber(dto.getProNumber());
        entity.getNumbers().setBolNumber(dto.getBolNumber());
        entity.setVolumeQuoteId(dto.getVolumeQuoteID());
        if (dto.getQuoteId() != null) {
            entity.setSavedQuoteId(dto.getQuoteId());
        }
        buildAccessorialsEntity(entity, dto);
        if (dto.getSelectedProposition() != null) {
            updateVersionOfCostDetails(dto.getSelectedProposition().getId(), dto.getSelectedProposition().getVersion(), entity.getCostDetails());

            // other costs-related information is built on Services layer
        }

        buildFreightBillPayTo(entity, dto);

        if (dto.getBillTo() != null) {
            entity.setBillTo(dataProvider.getBillTo(dto.getBillTo().getId()));
        }

        buildCustomsBrokerEntity(entity, dto.getCustomsBroker());

        if (dto.getPaymentTerms() != null) {
            entity.setPayTerms(PaymentTerms.valueOf(dto.getPaymentTerms().name()));
        }
        if (dto.getShipmentDirection() != null && !dto.getShipmentDirection().isEmpty()) {
            entity.setShipmentDirection(ShipmentDirection.getByCode(dto.getShipmentDirection()));
        }
        setFreightBillDate(dto, entity);
        createAdditionalInfo(dto, entity);
        createAdditionalFields(dto, entity);

        buildPrepaidDetails(dto, entity);
        return entity;
    }

    private void holdFinalizationStatus(ShipmentDTO dto, LoadEntity entity) {
        if (dto.isHoldFinalizationStatus() != null
                && (entity.getFinalizationStatus().equals(ShipmentFinancialStatus.NONE) || entity
                        .getFinalizationStatus().equals(ShipmentFinancialStatus.FINANCE_HOLD))) {
            if (dto.isHoldFinalizationStatus()) {
                entity.setFinalizationStatus(ShipmentFinancialStatus.FINANCE_HOLD);
            } else {
                entity.setFinalizationStatus(ShipmentFinancialStatus.NONE);
            }
        }
    }

    private void buildPrepaidDetails(ShipmentDTO dto, LoadEntity entity) {
        if (dto.getPrepaidDetails() != null && !dto.getPrepaidDetails().isEmpty()) {

            Set<PrepaidDetailEntity> prepaidDetails = entity.getPrepaidDetails() != null ? entity.getPrepaidDetails()
                    : new HashSet<>(dto.getPrepaidDetails().size());
            Set<PrepaidDetailEntity> prepaidDetailsNew = new HashSet<>();
            for (PrepaidDetailDTO detailDTO : dto.getPrepaidDetails()) {
                if (detailDTO.getId() == null) {
                    PrepaidDetailEntity prepaidDetail = new PrepaidDetailEntity(entity, detailDTO.getId(),
                            detailDTO.getPaymentId(), detailDTO.getDate(), detailDTO.getAmount());
                    prepaidDetailsNew.add(prepaidDetail);
                } else {
                    PrepaidDetailEntity prepaidDetail = prepaidDetails.stream()
                            .filter(n -> n.getId().equals(detailDTO.getId())).findFirst().get();
                    prepaidDetail.setAmount(detailDTO.getAmount());
                    prepaidDetail.setPaymentDate(detailDTO.getDate());
                    prepaidDetail.setPaymentId(detailDTO.getPaymentId());
                    prepaidDetailsNew.add(prepaidDetail);
                }
            }
            entity.getPrepaidDetails().clear();
            entity.getPrepaidDetails().addAll(prepaidDetailsNew);
        } else {
            if (entity.getPrepaidDetails() != null) {
                entity.getPrepaidDetails().clear();
            }
        }
    }

    private String getIndicator(Long matchedVendorBillId) {
        return matchedVendorBillId == null ? ShipmentSourceIndicator.SYS.name() : ShipmentSourceIndicator.EDI_M.name();
    }

    private void createAdditionalInfo(ShipmentDTO dto, LoadEntity entity) {
        if (!((dto.getMarkup() == null || dto.getMarkup() == 0L) && entity.getLoadAdditionalInfo() == null)) {
            if (entity.getLoadAdditionalInfo() == null) {
                LoadAdditionalInfoEntity loadAddlInfo = new LoadAdditionalInfoEntity();
                loadAddlInfo.setMarkup(dto.getMarkup());
                entity.setLoadAdditionalInfo(loadAddlInfo);
                loadAddlInfo.setLoad(entity);
            } else {
                entity.getLoadAdditionalInfo().setMarkup(dto.getMarkup());
            }
        }
    }

    private void createAdditionalFields(ShipmentDTO dto, LoadEntity entity) {
            if (entity.getLoadAdditionalFields() == null) {
                LoadAdditionalFieldsEntity loadAddfields = new LoadAdditionalFieldsEntity();
                loadAddfields.setCargoValue(dto.getCargoValue());
                entity.setLoadAdditionalFields(loadAddfields);
                loadAddfields.setLoad(entity);
            } else {
                entity.getLoadAdditionalFields().setCargoValue(dto.getCargoValue());
            }
    }

    private void setFreightBillDate(ShipmentDTO dto, LoadEntity entity) {
        if (entity.getVendorBillDetails() != null) {
            entity.getVendorBillDetails().setFrtBillRecvDate(dto.getFreightBillDate());
        }
    }

    private void updateOrganizationAndLocation(LoadEntity entity, Long organizationId, Long locationId) {
        if (entity.getOrganization() == null || !entity.getOrganization().getId().equals(organizationId)) {
            CustomerEntity organization = new CustomerEntity();
            organization.setId(organizationId);
            organization.setPrintBarcode(dataProvider.isPrintBarcode(organizationId));
            entity.setOrganization(organization);
        }

        if (!ObjectUtils.equals(entity.getLocationId(), locationId)) {
            entity.setLocationId(locationId);
        }
    }

    private void buildAccessorialsEntity(LoadEntity load, ShipmentDTO dto) {
        if (load.getLtlAccessorials() == null) {
            load.setLtlAccessorials(new HashSet<LtlLoadAccessorialEntity>());
        }
        if (!load.getLtlAccessorials().isEmpty()) {
            load.getLtlAccessorials().clear();
        }
        addAccessorials(load, dto.getOriginDetails().getAccessorials(), LtlAccessorialGroup.PICKUP);
        addAccessorials(load, dto.getDestinationDetails().getAccessorials(), LtlAccessorialGroup.DELIVERY);
    }

    private void addAccessorials(LoadEntity load, Collection<String> accessorials, LtlAccessorialGroup accessorialGroup) {
        if (accessorials != null) {
            for (String accessorial : accessorials) {
                LtlLoadAccessorialEntity entity = new LtlLoadAccessorialEntity();
                entity.setLoad(load);
                AccessorialTypeEntity accessorialType = new AccessorialTypeEntity(accessorial);
                accessorialType.setAccessorialGroup(accessorialGroup);
                entity.setAccessorial(accessorialType);
                load.getLtlAccessorials().add(entity);
            }
        }
    }

    private void buildFreightBillPayTo(final LoadEntity entity, ShipmentDTO dto) {
        if (dto.getFreightBillPayTo() == null
                || FreightBillPayToDaoImpl.DEFAULT_FREIGHT_BILL_PAY_TO_ID.equals(dto.getFreightBillPayTo().getId())) {
            entity.setFreightBillPayTo(dataProvider.getDefaultFreightBillPayTo());
        } else {
            FreightBillPayToDTOBuilder builder = new FreightBillPayToDTOBuilder();
            builder.setDataProvider(new FreightBillPayToDTOBuilder.DataProvider() {
                @Override
                public FreightBillPayToEntity getFreightBillPayTo() {
                    return entity.getFreightBillPayTo();
                }
            });
            entity.setFreightBillPayTo(builder.buildEntity(dto.getFreightBillPayTo()));
        }
    }

    private void buildFreightBillPayToDTO(ShipmentDTO dto, LoadEntity entity) {
        if (entity.getFreightBillPayTo() == null) {
            entity.setFreightBillPayTo(dataProvider.getDefaultFreightBillPayTo());
        }
        dto.setFreightBillPayTo(freightBillPayToDTOBuilder.buildDTO(entity.getFreightBillPayTo()));
    }

    private void updateVersionOfCostDetails(Long id, Integer version, Set<LoadCostDetailsEntity> costDetails) {
        if (id != null && version != null && costDetails != null && !costDetails.isEmpty()) {
            for (LoadCostDetailsEntity costDetail : costDetails) {
                if (id.equals(costDetail.getId())) {
                    costDetail.setVersion(version);
                    break;
                }
            }
        }
    }

    private LoadEntity buildLoadEntity(ShipmentDTO dto) {
        LoadEntity entity = null;
        if (dto.getId() != null) {
            entity = dataProvider.getLoadById(dto.getId());
        }
        if (entity == null) {
            entity = new LoadEntity();
        } else {
            entity.setVersion(dto.getVersion());
        }
        return buildFinishOrder(dto, entity);
    }

    private LoadEntity buildFinishOrder(final ShipmentDTO dto, final LoadEntity entity) {
        ShipmentFinishOrderDTOBuilder builder = new ShipmentFinishOrderDTOBuilder();
        builder.setDataProvider(new ShipmentFinishOrderDTOBuilder.DataProvider() {
            @Override
            public LoadEntity getLoadEntity() {
                return entity;
            }

            @Override
            public AddressBookEntryDTO getOriginAddress() {
                return dto.getOriginDetails().getAddress();
            }

            @Override
            public AddressBookEntryDTO getDestinationAddress() {
                return dto.getDestinationDetails().getAddress();
            }

            @Override
            public AddressEntity getAddress(Long id) {
                return dataProvider.getAddress(id);
            }

            @Override
            public PackageTypeEntity findPackageType(String id) {
                return dataProvider.findPackageType(id);
            }
        });
        return builder.buildEntity(dto.getFinishOrder());
    }

    private void buildCustomsBrokerDTO(ShipmentDTO dto, LoadEntity entity) {
        if (entity.getCustomsBroker() != null || entity.getCustomsBrokerPhone() != null) {
            CustomsBrokerDTO customsBrokerDTO = new CustomsBrokerDTO();
            customsBrokerDTO.setName(entity.getCustomsBroker());
            customsBrokerDTO.setPhone(PhoneUtils.parse(entity.getCustomsBrokerPhone()));
            dto.setCustomsBroker(customsBrokerDTO);
        }
    }

    private void buildCustomsBrokerEntity(LoadEntity entity, CustomsBrokerDTO dto) {
        entity.setCustomsBroker(dto == null ? null : dto.getName());
        entity.setCustomsBrokerPhone(dto == null || dto.getPhone() == null ? null : PhoneUtils.format(dto.getPhone()));
    }

    private void buildProposalDTO(LoadEntity entity, ShipmentDTO dto) {
        ShipmentProposalBO proposal;
        if (entity.getActiveCostDetail() != null) {
         // load may not have cost details for example when we need to get a copy of load without costs information
            proposal = buildProposalFromCost(entity);
            dto.setGuaranteedBy(entity.getActiveCostDetail().getGuaranteedBy());
        } else {
            proposal = new ShipmentProposalBO();
        }

        LoadDetailsEntity destination = entity.getDestination();
        if (destination != null) {
            proposal.setEstimatedTransitDate(destination.getArrival());
        }
        proposal.setEstimatedTransitTime(entity.getTravelTime());

        // some loads may not have carriers yet (for example loads in status Open) or when we need to get a
        // copy of load without carrier
        if (entity.getCarrier() != null) {
            proposal.setCarrier(carrierBuilder.buildDTO(entity.getCarrier()));
            if (entity.getSpecialMessage() != null && proposal.getCarrier() != null) {
                proposal.getCarrier().setSpecialMessage(entity.getSpecialMessage().getNote());
            }
        }
        proposal.setMileage(entity.getMileage());
        proposal.setCarrierQuoteNumber(entity.getNumbers().getCarrierQuoteNumber());
        proposal.setServiceLevelCode(entity.getNumbers().getServiceLevelCode());
        proposal.setServiceLevelDescription(entity.getNumbers().getServiceLevelDescription());
        if (entity.getVolumeQuoteId() != null) {
            proposal.setShipmentType(ShipmentType.VLTL);
        }
        dto.setSelectedProposition(proposal);
    }

    private ShipmentProposalBO buildProposalFromCost(LoadEntity entity) {
        ShipmentProposalBO proposal = new ShipmentProposalBO();
        LoadCostDetailsEntity activeCostDetails = entity.getActiveCostDetail();
        proposal.setId(activeCostDetails.getId());
        proposal.setVersion(activeCostDetails.getVersion());
        proposal.setServiceType(activeCostDetails.getServiceType());
        proposal.setNewLiability(activeCostDetails.getNewLiability());
        proposal.setUsedLiability(activeCostDetails.getUsedLiability());
        proposal.setProhibited(activeCostDetails.getProhibitedCommodities());
        proposal.setGuaranteedNameForBOL(activeCostDetails.getGuaranteedNameForBOL());
        proposal.setPricingProfileId(activeCostDetails.getPricingProfileDetailId());
        proposal.setCostOverride(activeCostDetails.getCostOverride());
        proposal.setRevenueOverride(activeCostDetails.getRevenueOverride());
        proposal.setCostDetailItems(costDetailItemBuilder.buildList(activeCostDetails.getCostDetailItems()));
        if (activeCostDetails.getGuaranteedBy() != null) {
            updateGuaranteedCostDetailItems(proposal.getCostDetailItems(), activeCostDetails.getGuaranteedBy());
        }
        proposal.setTotalCarrierAmt(activeCostDetails.getTotalCost());
        proposal.setTotalShipperAmt(activeCostDetails.getTotalRevenue());
        proposal.setRatingCarrierType(isLTLLCIntegrationType(entity) ? CarrierIntegrationType.LTLLC.name() : null);
        return proposal;
    }
    
    private boolean isLTLLCIntegrationType(LoadEntity load) {
        if (load.getActiveCostDetail() != null && load.getActiveCostDetail().getPricingProfileDetailId() != null) {
            Long pricingProfileDetailId = load.getActiveCostDetail().getPricingProfileDetailId();

            LtlPricingProfileEntity profile = dataProvider.getProfileById(pricingProfileDetailId);

            if (profile != null) {
                LtlPricingProfileDetailsEntity profileDetails = profile.getProfileDetails().stream()
                        .filter(i -> i.getPricingDetailType() == null).findFirst().orElse(null);

                if (profileDetails != null && "LTLLC".equals(profileDetails.getCarrierType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateGuaranteedCostDetailItems(List<CostDetailItemBO> items, Long guaranteedBy) {
        for (CostDetailItemBO item : items) {
            if (ShipmentService.GUARANTEED_SERVICE_REF_TYPE.equals(item.getRefType())) {
                item.setGuaranteedBy(guaranteedBy);
            }
        }
    }

    private AddressBookEntryDTO prepareAddress(LoadDetailsEntity loadDetails) {
        AddressBookEntryDTO result = null;
        if (loadDetails != null) {
            result = addressBookEntryDTOBuilder.buildDTO(loadDetails.getAddress());
            result.setContactName(loadDetails.getContactName());
            result.setAddressName(loadDetails.getContact());
            result.setAddressCode(loadDetails.getAddressCode());
            result.setEmail(loadDetails.getContactEmail());
            result.setPhone(PhoneUtils.parse(loadDetails.getContactPhone()));
            result.setFax(PhoneUtils.parse(loadDetails.getContactFax()));
            if (loadDetails.getPointType() == PointType.ORIGIN) {
                result.setPickupNotes(loadDetails.getNotes());
                result.setInternalPickupNotes(loadDetails.getInternalNotes());
            } else {
                result.setDeliveryNotes(loadDetails.getNotes());
                result.setInternalDeliveryNotes(loadDetails.getInternalNotes());
            }
        }
        return result;
    }

    private ShipmentDetailsDTO buildShipmentDetailsDTO(LoadEntity load, boolean isOrigin) {
        ShipmentDetailsDTO dto = new ShipmentDetailsDTO();
        if (CollectionUtils.isNotEmpty(load.getLtlAccessorials())) {
            dto.setAccessorials(buildAccessorials(isOrigin ? LtlAccessorialGroup.PICKUP : LtlAccessorialGroup.DELIVERY, load.getLtlAccessorials()));
        }
        LoadDetailsEntity loadDetails = isOrigin ? load.getOrigin() : load.getDestination();
        if (loadDetails.getAddress() != null) {
            dto.setZip(zipDTOBuilder.buildDTO(loadDetails.getAddress().getZipCode()));
            dto.setAddress(prepareAddress(loadDetails));
        }

        return dto;
    }

    private Collection<String> buildAccessorials(final LtlAccessorialGroup accessorialGroup, Set<LtlLoadAccessorialEntity> ltlAccessorials) {
        return ltlAccessorials.stream().filter(acc -> acc.getAccessorial().getAccessorialGroup() == accessorialGroup)
                .map(acc -> acc.getAccessorial().getId()).collect(Collectors.toList());
    }

    private void buildQuoteFields(ShipmentDTO dto, Long id) {
        QuotedBO primaryCost = dataProvider.getPrimaryLoadCostDetail(id);
        if (primaryCost != null) {
            dto.setQuotedTotalCost(primaryCost.getTotalCost().toString());
            dto.setQuotedTotalRevenue(primaryCost.getTotalRevenue().toString());
        }
    }

    /**
     * Data provider to build entity.
     */
    public interface DataProvider {
        /**
         * Get bill to by id.
         * 
         * @param id
         *            {@link BillToEntity#getId()}
         * @return {@link BillToEntity}
         */
        BillToEntity getBillTo(Long id);

        /** 
         * Get pricing profile by id.
         * @param pricingProfileDetailId
         * @return
         */
        LtlPricingProfileEntity getProfileById(Long pricingProfileDetailId);

        /**
         * Check should we add a barcode to the BOL document.
         * 
         * @param customerId
         *            id of customer
         * @return true in case when barcode should be added to the BOL document
         */
        Boolean isPrintBarcode(Long customerId);

        /**
         * Get Ltl Address for update.
         * 
         * @param id
         *            {@link AddressEntity#getId()}
         * @return {@link AddressEntity}
         */
        AddressEntity getAddress(Long id);

        /**
         * Get {@link LoadEntity} by id.
         * 
         * @param id
         *            {@link LoadEntity#getId()}
         * @return {@link LoadEntity}
         */
        LoadEntity getLoadById(Long id);

        /**
         * Get instance of default {@link FreightBillPayToEntity}.
         * 
         * @return {@link FreightBillPayToEntity}
         */
        FreightBillPayToEntity getDefaultFreightBillPayTo();

        /**
         * Provider of {@link PackageTypeEntity}.
         *
         * @param id
         *            {@link PackageTypeEntity#getId()}
         * @return entity or <code>null</code>s
         */
        PackageTypeEntity findPackageType(String id);

        /**
         * Get LoadCostDetail.
         * 
         * @param id
         *            {@link LoadEntity#getId()}
         * @return {@link QuotedBO}
         */
        QuotedBO getPrimaryLoadCostDetail(Long id);
    }

}
