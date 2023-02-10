package com.pls.shipment.service.impl;

import static com.pls.shipment.service.impl.edi.utils.EDIUtils.PHONE_FORBIDDEN_SYMBOLS;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.refineString;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.PhoneNumber;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.bo.proposal.CostDetailOwner;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.EdiType;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.core.service.util.OutboundEdiQueueMappingUtils;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.integration.ltllifecycle.LtlLifecycleClientException;
import com.pls.ltlrating.service.LtlProfileDetailsService;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadAdditionalFieldsEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.LtlLoadAccessorialEntity;
import com.pls.shipment.domain.enums.EDITransactionSet;
import com.pls.shipment.domain.enums.LoadEventType;
import com.pls.shipment.domain.enums.ShipmentOperationType;
import com.pls.shipment.domain.sterling.AccessorialJaxbBO;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.BaseRateJaxbBO;
import com.pls.shipment.domain.sterling.HazmatInfoJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.MaterialJaxbBO;
import com.pls.shipment.domain.sterling.TransitDateJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.domain.sterling.enums.OperationType;
import com.pls.shipment.domain.sterling.enums.RateType;
import com.pls.shipment.domain.sterling.enums.YesNo;
import com.pls.shipment.integration.ltllifecycle.service.LTLLifecycleIntegrationService;
import com.pls.shipment.service.LoadTenderService;
import com.pls.shipment.service.ShipmentUtils;
import com.pls.shipment.service.audit.LoadEventBuilder;
import com.pls.shipment.service.edi.EDIService;
import com.pls.shipment.service.impl.edi.EDIHelper;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

/**
 * {@link LoadTenderService} implementation.
 *
 * @author Mikhail Boldinov, 22/01/14
 */
@Service
@Transactional
public class LoadTenderServiceImpl implements LoadTenderService {

    public static final String EDI_OPERATION_TYPE_KEY = "EDI_OPERATION_TYPE_KEY";
    public static final String PRODUCTION_MODE_KEY = "PRODUCTION_MODE";

    private static final String HAZMAT_WARNING_MESSAGE = "THERE IS ONE OR MORE PRODUCTS THAT ARE HAZARDOUS. ";

    private static final String TIMEZONE_CODE = "ET";

    public static final String TRACKING_STATUS_CODE = "EC";

    public static final Long EDI204 = 204L;

    public static final Long EDI211 = 211L;

    private static final String DATE_TIME_FORMAT = "MM/dd/yy h:mm a";

    private static final Logger LOG = LoggerFactory.getLogger(LoadTenderServiceImpl.class);

    @Value("${mode.production}")
    private Boolean productionMode;
    
    @Value("${ltlLifecycle.allowDispatches:false}")
    private String allowDispatchViaLtlLc;

    @Autowired
    private OutboundEdiQueueMappingUtils outboundEdiQueueUtils;

    @Autowired
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Autowired
    private EDIService ediService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private LoadTrackingDao loadTrackingDao;

    @Autowired
    private ShipmentEmailSender shipmentEmailSender;

    @Autowired
    private ShipmentEventDao eventDao;

    @Autowired
    private EDIHelper ediHelper;
    
    @Autowired
    private LtlProfileDetailsService ltlProfileDetailsService;
    
    @Autowired
    private LTLLifecycleIntegrationService ltlLifecycleIntegrationService;

    @Override
    public void tenderLoad(LoadEntity load, CarrierEntity prevCarrier, ShipmentStatus oldStatus, ShipmentStatus currentStatus)
            throws InternalJmsCommunicationException, EdiProcessingException {
        if (currentStatus == null) {
            return;
        }

        OperationType operationType = getOperationType(oldStatus, currentStatus);
        
        if (useLtlLifecycleForDispatch(load)) {
            if(isDispatchToCarrierAllowed(operationType)){
                if (operationType == OperationType.TENDER) {
                    try {
                        ltlLifecycleIntegrationService.dispatchLoad(load);

                        ensureLoadAdditionalFields(load);
                        load.getLoadAdditionalFields().setDispatchedVia("LTLLC");
                        load.getLoadAdditionalFields().setTrackedVia("LTLLC");// if dispatched via LTLLC, we need to track via LTLLC
                        createDispatchSuccessEvent(load.getId());
                    } catch (LtlLifecycleClientException ex) {
                        LOG.error("Dispatch via LTL Lifecycle failed. LoadId [{}], reason: [{}]", load.getId(), ex.getMessage());
                        createDispatchFailureEvent(load.getId(), "Dispatch via LTL Lifecycle failed. See logs for details.");
                    }
                }
                if (operationType == OperationType.CANCEL) {
                    try {
                        ltlLifecycleIntegrationService.cancelLoad(load);
                        createCancelSuccessEvent(load.getId());
                    } catch (LtlLifecycleClientException ex) {
                        LOG.error("Shipment cancellation via LTL Lifecycle failed. LoadId [{}], reason: [{}]", load.getId(), ex.getMessage());
                        createCancelFailureEvent(load.getId(), "Shipment cancellation via LTL Lifecycle failed. See logs for details.");
                    }
                }
                if (operationType == OperationType.UPDATE) {
                    // not supported
                }
            }

        } else {
            if (isDispatchToCarrierAllowed(operationType)) {
                if (currentStatus == ShipmentStatus.DISPATCHED && prevCarrier != null && !prevCarrier.getId().equals(load.getCarrier().getId())) {
                    sendEmailOrEDI(load, prevCarrier, OperationType.CANCEL);
                    sendEmailOrEDI(load, load.getCarrier(), OperationType.TENDER);
                } else if (oldStatus == ShipmentStatus.DISPATCHED && (currentStatus == ShipmentStatus.OPEN || currentStatus == ShipmentStatus.BOOKED)) {
                    sendEmailOrEDI(load, prevCarrier, operationType);
                } else {
                    sendEmailOrEDI(load, load.getCarrier(), operationType);
                }
            }
            
            createBOLEDI(load, currentStatus);
            
            if (operationType == OperationType.TENDER && useLtlLifecycleForTracking(load)) {
                try {
                    ltlLifecycleIntegrationService.initTrackLoad(load);

                    ensureLoadAdditionalFields(load);
                    load.getLoadAdditionalFields().setTrackedVia("LTLLC");
                    createTrackSuccessEvent(load.getId());
                } catch (LtlLifecycleClientException ex) {
                    LOG.error("Tracking initialization via LTL Lifecycle failed. LoadId [{}], reason: [{}]", load.getId(), ex.getMessage());
                    createTrackFailureEvent(load.getId(), "Tracking initialization via LTL Lifecycle failed. See logs for details.");
                }
            }
        }

    }

    private boolean useLtlLifecycleForDispatch(LoadEntity load) {
        if (load.getActiveCostDetail() != null && load.getActiveCostDetail().getPricingProfileDetailId() != null) {
            Long pricingProfileDetailId = load.getActiveCostDetail().getPricingProfileDetailId();
            LtlPricingProfileEntity profile = ltlProfileDetailsService.getProfileById(pricingProfileDetailId);
            return (profile != null && Boolean.TRUE.equals(profile.getDispatchWithLTLLC()));
        }
        return false;
    }

    private boolean useLtlLifecycleForTracking(LoadEntity load) {
        if (load.getActiveCostDetail() != null && load.getActiveCostDetail().getPricingProfileDetailId() != null) {
            Long pricingProfileDetailId = load.getActiveCostDetail().getPricingProfileDetailId();
            LtlPricingProfileEntity profile = ltlProfileDetailsService.getProfileById(pricingProfileDetailId);
            return (profile != null && Boolean.TRUE.equals(profile.getTrackWithLTLLC()));
        }
        return false;
    }
    
    private void ensureLoadAdditionalFields(LoadEntity load) {
        if (load.getLoadAdditionalFields() == null) {
            LoadAdditionalFieldsEntity loadAddfields = new LoadAdditionalFieldsEntity();
            load.setLoadAdditionalFields(loadAddfields);
            loadAddfields.setLoad(load);
        }
    }

    private boolean isDispatchToCarrierAllowed(OperationType operationType) {
        return operationType != null && !userPermissionsService.hasCapability(Capabilities.DO_NOT_SEND_DISPATCH_TO_CARRIER.name());
    }

    private void sendEmailOrEDI(LoadEntity load, CarrierEntity carrier, OperationType operationType)
            throws InternalJmsCommunicationException, EdiProcessingException {
        if (isCarrierManualCapable(carrier)) {
            shipmentEmailSender.sendCarrierNotification(load, carrier, operationType);
            if (operationType != OperationType.CANCEL) {
                trackEmailEvent(load.getId());
            }
        } else if (isCarrierEdiCapable(carrier)) {
            createLoadTenderEDI(load, carrier, operationType);
        }
    }

    private OperationType getOperationType(ShipmentStatus oldStatus, ShipmentStatus currentStatus) {
        OperationType operationType = null;
        if (oldStatus == ShipmentStatus.DISPATCHED) {
            if (currentStatus == ShipmentStatus.DISPATCHED) {
                operationType = OperationType.UPDATE;
            } else if (currentStatus == ShipmentStatus.CANCELLED || currentStatus == ShipmentStatus.BOOKED || currentStatus == ShipmentStatus.OPEN) {
                operationType = OperationType.CANCEL;
            }
        } else if (currentStatus == ShipmentStatus.DISPATCHED) {
            operationType = OperationType.TENDER;
        }
        return operationType;
    }

    private void createLoadTenderEDI(LoadEntity load, CarrierEntity carrier, OperationType operationType) throws InternalJmsCommunicationException,
        EdiProcessingException {

        if (!outboundEdiQueueUtils.isQueueEnabled(carrier.getActualScac())) {
            sendEDIThroughFTP(load.getId(), carrier.getId(), getShipmentOperationType(operationType));
        } else {
            LoadMessageJaxbBO loadMessageBO = createEDIMessage(load, carrier, operationType);
            loadMessageBO.setFileType(EDIMessageType.EDI204_STERLING_MESSAGE_TYPE.getEdiTransaction());
            loadMessageBO.setMessageType(EDIMessageType.EDI204_STERLING_MESSAGE_TYPE.getCode());

            if (BooleanUtils.isNotFalse(ediHelper.isEdi204Changed(loadMessageBO))) {
                try {
                    SterlingIntegrationMessageBO sterlingMessage = new SterlingIntegrationMessageBO(loadMessageBO,
                            EDIMessageType.EDI204_STERLING_MESSAGE_TYPE);
                    sterlingMessageProducer.publishMessage(sterlingMessage);

                    addTrackingEvent(load, carrier);
                } catch (Exception ex) {
                    throw new InternalJmsCommunicationException(
                            "Exception occurred while publishing message to external integration message queue", ex);
                }
            }
        }
    }

    private void createBOLEDI(LoadEntity load, ShipmentStatus currentStatus) throws InternalJmsCommunicationException {
        if (currentStatus == ShipmentStatus.DISPATCHED && load.getBillTo().getEdiSettings() != null
                && load.getBillTo().getEdiSettings().getEdiType() != null
                && load.getBillTo().getEdiSettings().getEdiType().contains(EdiType.EDI_211)) {
            LoadMessageJaxbBO loadMessageBO = createEDIMessage(load, load.getCarrier(), null);
            loadMessageBO.setFileType(EDIMessageType.EDI211_STERLING_MESSAGE_TYPE.getEdiTransaction());
            loadMessageBO.setMessageType(EDIMessageType.EDI211_STERLING_MESSAGE_TYPE.getCode());

            try {
                SterlingIntegrationMessageBO sterlingMessage =
                        new SterlingIntegrationMessageBO(loadMessageBO, EDIMessageType.EDI211_STERLING_MESSAGE_TYPE);
                sterlingMessageProducer.publishMessage(sterlingMessage);

                addBolTrackingEvent(load);
            } catch (Exception ex) {
                throw new InternalJmsCommunicationException("Exception occurred while publishing message to external integration message queue", ex);
            }
        }
    }

    private Set<LtlLoadAccessorialEntity> getSortedAccessorials(Set<LtlLoadAccessorialEntity> accessorials) {
        if (!ObjectUtils.isEmpty(accessorials)) {
            return accessorials.stream().filter(e -> e.getAccessorial() != null && e.getAccessorial().getDescription() != null)
                    .sorted(Comparator.comparing(e -> e.getAccessorial().getDescription()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return accessorials;
    }

    private LoadMessageJaxbBO createEDIMessage(LoadEntity load, CarrierEntity carrier, OperationType operationType) {
        LoadMessageJaxbBO loadMessageBO = new LoadMessageJaxbBO();
        loadMessageBO.setLoadId(load.getId());
        loadMessageBO.setCustomerName(load.getOrganization().getName());
        loadMessageBO.setCustomerOrgId(load.getOrganization().getId());
        loadMessageBO.setShipmentNo(load.getNumbers().getRefNumber());
        loadMessageBO.setProNumber(load.getNumbers().getProNumber());
        loadMessageBO.setInboundOutbound(load.getShipmentDirection().getCode());
        loadMessageBO.setScac(carrier.getActualScac());
        loadMessageBO.setBol(load.getNumbers().getBolNumber());
        loadMessageBO.setPayTerms(load.getPayTerms().getPaymentTermsCode());
        loadMessageBO.setOperationType(operationType);
        loadMessageBO.setGlNumber(load.getNumbers().getGlNumber());
        loadMessageBO.setPickupNum(load.getNumbers().getPuNumber());
        loadMessageBO.setPoNum(load.getNumbers().getPoNumber());
        loadMessageBO.setSoNum(load.getNumbers().getSoNumber());
        loadMessageBO.setTrailerNum(load.getNumbers().getTrailerNumber());
        loadMessageBO.setTotalMiles(load.getMileage());
        loadMessageBO.setTotalWeight(new BigDecimal(load.getWeight()));
        loadMessageBO.setPersonId(load.getPersonId());
        loadMessageBO.setCustomerLocationId(load.getLocationId());
        loadMessageBO.setCustomerBillToId(load.getBillTo().getId());
        
        // send this only if needed to do so (e.g. multi currency carrier).
        // Not all carriers are prepared for the extra info in the EDI messages, so we need to be selective, and contact carrier before we start sending this.
        if(carrier.getOrgServiceEntity() != null && Boolean.TRUE.equals(carrier.getOrgServiceEntity().getEdiSendCurrencyOnDispatch())) {
            loadMessageBO.setBillingCurrencyCode(carrier.getCurrencyCode().name());
        }

        setMaterials(load, loadMessageBO);
        setAddresses(load, loadMessageBO);

        setCostDetails(loadMessageBO, load.getActiveCostDetail());

        loadMessageBO.setPickupNotes(getPickupNotes(load));
        loadMessageBO.setDeliveryNotes(ShipmentUtils.getAggregatedNotes(getSortedAccessorials(load.getLtlAccessorials()),
                        false, load.getDestination().getNotes(), load.getDeliveryNotes()));

        return loadMessageBO;
    }

    private String getPickupNotes(LoadEntity load) {
        StringBuilder result = new StringBuilder();
        if (hasHazmat(load.getOrigin().getLoadMaterials())) {
            result.append(HAZMAT_WARNING_MESSAGE);
        }

        result.append(ShipmentUtils.getAggregatedNotes(getSortedAccessorials(load.getLtlAccessorials()), true,
                load.getOrigin().getNotes(), load.getSpecialInstructions()));
        return result.toString().trim();
    }

    private void setCostDetails(LoadMessageJaxbBO loadMessageBO, LoadCostDetailsEntity loadCostDetailsEntity) {
        if (loadCostDetailsEntity != null) {
            loadMessageBO.setTotalCost(loadCostDetailsEntity.getTotalCost());
            if (loadCostDetailsEntity.getGuaranteedBy() != null) {
                loadMessageBO.setGuaranteedBy(Long.toString(loadCostDetailsEntity.getGuaranteedBy()));
            }

            loadMessageBO.setProhibitedCommodities(loadCostDetailsEntity.getProhibitedCommodities());

            Iterator<CostDetailItemEntity> costDetailItemEntityIterator = loadCostDetailsEntity.getCostDetailItems().iterator();
            while (costDetailItemEntityIterator.hasNext()) {
                CostDetailItemEntity costDetailItemEntity = costDetailItemEntityIterator.next();
                if (CostDetailOwner.C.equals(costDetailItemEntity.getOwner())) {
                    if ("CRA".equals(costDetailItemEntity.getAccessorialType())) {
                        loadMessageBO.addBaseRate(setBaseRate(costDetailItemEntity));
                    } else {
                        loadMessageBO.addAccessorial(setAccessorial(costDetailItemEntity));
                    }
                }
            }
            if (loadMessageBO.getAccessorials() != null) {
                loadMessageBO.getAccessorials().sort(Comparator.comparing(AccessorialJaxbBO::getCode));
            }
        }
    }

    private AccessorialJaxbBO setAccessorial(CostDetailItemEntity costDetailItemEntity) {
        AccessorialJaxbBO accessorial = new AccessorialJaxbBO();
        accessorial.setCode(costDetailItemEntity.getAccessorialDictionary().getId());
        accessorial.setDescription(costDetailItemEntity.getAccessorialDictionary().getDescription());
        accessorial.setRateType(RateType.CARRIER);
        accessorial.setUnitCost(costDetailItemEntity.getUnitCost());
        accessorial.setUnitType(costDetailItemEntity.getUnitType());
        accessorial.setQuantity(costDetailItemEntity.getQuantity());
        accessorial.setSubTotal(costDetailItemEntity.getSubtotal());
        accessorial.setComment(costDetailItemEntity.getNote());

        if (LtlAccessorialGroup.PICKUP.equals(costDetailItemEntity.getAccessorialDictionary().getAccessorialGroup())
                || LtlAccessorialGroup.DELIVERY.equals(costDetailItemEntity.getAccessorialDictionary().getAccessorialGroup())) {
            accessorial.setAutoApplied(YesNo.NO);
            accessorial.setGroup(costDetailItemEntity.getAccessorialDictionary().getAccessorialGroup().name());
        } else {
            accessorial.setAutoApplied(YesNo.YES);
        }
        return accessorial;
    }

    private BaseRateJaxbBO setBaseRate(CostDetailItemEntity costDetailItemEntity) {
        BaseRateJaxbBO baseRate = new BaseRateJaxbBO();
        baseRate.setRateType(RateType.CARRIER);
        baseRate.setUnitCost(costDetailItemEntity.getUnitCost());
        baseRate.setUnitType(costDetailItemEntity.getUnitType());
        baseRate.setQuantity(costDetailItemEntity.getQuantity());
        baseRate.setSubTotal(costDetailItemEntity.getSubtotal());
        baseRate.setComment(costDetailItemEntity.getNote());

        return baseRate;
    }

    private void setAddresses(LoadEntity load, LoadMessageJaxbBO loadMessageBO) {
        loadMessageBO.addAddress(setFreightChargeTo(load.getFreightBillPayTo(), AddressType.FREIGHT_CHARGE_TO));
        loadMessageBO.addAddress(setLoadPoint(load.getOrigin(), AddressType.ORIGIN));
        loadMessageBO.addAddress(setLoadPoint(load.getDestination(), AddressType.DESTINATION));
        loadMessageBO.getAddresses().sort(Comparator.comparing(AddressJaxbBO::getAddressType));
    }

    private AddressJaxbBO setLoadPoint(LoadDetailsEntity loadDetailsEntity, AddressType addressType) {
        AddressJaxbBO address = new AddressJaxbBO();
        address.setAddressType(addressType);
        address.setName(loadDetailsEntity.getContact());
        address.setAddressCode(loadDetailsEntity.getAddressCode());
        setAddress(loadDetailsEntity.getAddress(), address);
        address.setContactName(loadDetailsEntity.getContactName());
        address.setContactPhone(refineString(loadDetailsEntity.getContactPhone(), PHONE_FORBIDDEN_SYMBOLS));
        address.setContactFax(loadDetailsEntity.getContactFax());
        address.setContactEmail(loadDetailsEntity.getContactEmail());
        address.setNotes(loadDetailsEntity.getNotes());
        setTransitDate(address, addressType, loadDetailsEntity);
        return address;
    }

    private void setTransitDate(AddressJaxbBO address, AddressType pointType, LoadDetailsEntity loadDetailsEntity) {
        TransitDateJaxbBO transitDate = new TransitDateJaxbBO();
        if (AddressType.ORIGIN == pointType) {
            transitDate.setToDate(loadDetailsEntity.getArrivalWindowEnd());
            transitDate.setFromDate(loadDetailsEntity.getArrivalWindowStart());
        } else if (loadDetailsEntity.getScheduledArrival() != null) {
            Date scheduledArrival = loadDetailsEntity.getScheduledArrival();
            transitDate.setToDate(getTransitDateForDest(scheduledArrival, loadDetailsEntity.getArrivalWindowEnd()));
            transitDate.setFromDate(getTransitDateForDest(scheduledArrival, loadDetailsEntity.getArrivalWindowStart()));
        }
        address.setTransitDate(transitDate);
    }

    private Date getTransitDateForDest(Date scheduledArrival, Date hourWindow) {
        Calendar arrivalCalendar = Calendar.getInstance(Locale.US);
        arrivalCalendar.setTime(scheduledArrival);
        if (hourWindow != null) {
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.setTime(hourWindow);
            arrivalCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            arrivalCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        }
        return arrivalCalendar.getTime();
    }

    private AddressJaxbBO setFreightChargeTo(FreightBillPayToEntity freightBillPayToEntity, AddressType addressType) {
        AddressJaxbBO address = new AddressJaxbBO();
        address.setAddressType(addressType);
        address.setName(freightBillPayToEntity.getCompany());
        setAddress(freightBillPayToEntity.getAddress(), address);
        address.setContactName(freightBillPayToEntity.getContactName());
        address.setContactPhone(refineString(getPhoneNumber(freightBillPayToEntity.getPhone()), PHONE_FORBIDDEN_SYMBOLS));
        address.setContactEmail(freightBillPayToEntity.getEmail());
        address.setAccountNum(freightBillPayToEntity.getAccountNum());

        return address;
    }

    private void setAddress(AddressEntity entity, AddressJaxbBO address) {
        address.setAddress1(entity.getAddress1()); // N3 01
        address.setAddress2(entity.getAddress2()); // N3 01
        address.setCity(entity.getCity()); // N4 01
        address.setStateCode(entity.getStateCode()); // N4 02
        address.setPostalCode(entity.getZip()); // N4 03
        address.setCountryCode(entity.getCountry().getId()); // N4 04
    }

    private int compareMaterials(MaterialJaxbBO m1, MaterialJaxbBO m2) {
        return new CompareToBuilder().append(m1.getProductDesc(), m2.getProductDesc())
                .append(m1.getAddToProducts(), m2.getAddToProducts())
                .append(m1.getCommodityClassCode(), m2.getCommodityClassCode())
                .append(m1.getDimensionsUOM(), m2.getDimensionsUOM()).append(m1.getHazmat(), m2.getHazmat())
                .append(m1.getHeight(), m2.getHeight()).append(m1.getLength(), m2.getLength())
                .append(m1.getNmfc(), m2.getNmfc()).append(m1.getOrderNum(), m2.getOrderNum())
                .append(m1.getPackagingType(), m2.getPackagingType()).append(m1.getPieces(), m2.getPieces())
                .append(m1.getProductCode(), m2.getProductCode()).append(m1.getQuantity(), m2.getQuantity())
                .append(m1.getSubtotal(), m2.getSubtotal()).append(m1.getUnitCost(), m2.getUnitCost())
                .append(m1.getWeight(), m2.getWeight()).append(m1.getWeightUOM(), m2.getWeightUOM())
                .append(m1.getWidth(), m2.getWidth()).toComparison();
    };

    private void setMaterials(LoadEntity load, LoadMessageJaxbBO loadMessageBO) {
        if (load.getOrigin().getLoadMaterials() != null) {
            Iterator<LoadMaterialEntity> loadMaterialIterator = load.getOrigin().getLoadMaterials().iterator();
            while (loadMaterialIterator.hasNext()) {
                loadMessageBO.addMaterial(setMaterial(loadMaterialIterator.next()));
            }
            loadMessageBO.getMaterials().sort(this::compareMaterials);
        }
    }

    private MaterialJaxbBO setMaterial(LoadMaterialEntity loadMaterialEntity) {
        MaterialJaxbBO material = new MaterialJaxbBO();
        material.setWeight(loadMaterialEntity.getWeight());
        material.setCommodityClassCode(loadMaterialEntity.getCommodityClass().getDbCode());
        material.setProductCode(loadMaterialEntity.getProductCode());
        material.setProductDesc(loadMaterialEntity.getProductDescription());
        material.setLength(loadMaterialEntity.getLength());
        material.setWidth(loadMaterialEntity.getWidth());
        material.setHeight(loadMaterialEntity.getHeight());
        material.setQuantity(StringUtils.isNotBlank(loadMaterialEntity.getQuantity()) ? Integer.parseInt(loadMaterialEntity.getQuantity()) : null);
        material.setPackagingType(loadMaterialEntity.getPackageType() != null ? loadMaterialEntity.getPackageType().getId() : "");
        material.setStackable(loadMaterialEntity.isStackable() ? YesNo.YES : YesNo.NO);
        material.setPieces(loadMaterialEntity.getPieces());
        material.setNmfc(loadMaterialEntity.getNmfc());
        setHazmatInfo(material, loadMaterialEntity);

        return material;
    }

    private void setHazmatInfo(MaterialJaxbBO material, LoadMaterialEntity loadMaterialEntity) {
        material.setHazmat(loadMaterialEntity.isHazmat() ? YesNo.YES : YesNo.NO);
        if (loadMaterialEntity.isHazmat()) {
            HazmatInfoJaxbBO hazmatInfo = new HazmatInfoJaxbBO();
            hazmatInfo.setUnNum(loadMaterialEntity.getUnNumber());
            hazmatInfo.setPackagingGroupNum(loadMaterialEntity.getPackingGroup());
            hazmatInfo.setEmergencyCompany(loadMaterialEntity.getEmergencyCompany());
            hazmatInfo.setEmergencyPhone(getPhoneNumber(loadMaterialEntity.getEmergencyPhone()));
            hazmatInfo.setEmergencyContractNum(loadMaterialEntity.getEmergencyContract());
            hazmatInfo.setEmergencyInstr(loadMaterialEntity.getHazmatInstruction());
            hazmatInfo.setHzClass(loadMaterialEntity.getHazmatClass());
            material.setHazmatInfo(hazmatInfo);
        }
    }

    private void trackEmailEvent(Long loadId) {
        String[] value = ArrayUtils.toArray("Dispatch email sent " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(new Date()));
        LoadEventEntity loadEvent = LoadEventBuilder.buildEventEntity(loadId, LoadEventType.TRK_EMAIL, value);
        eventDao.saveOrUpdate(loadEvent);
    }
    
    private void createDispatchSuccessEvent(Long loadId) {
        String[] params = ArrayUtils.toArray("LTL Lifecycle");
        LoadEventEntity loadEvent = LoadEventBuilder.buildEventEntity(loadId, LoadEventType.LD_DIS_OK, params);
        eventDao.saveOrUpdate(loadEvent);
    }
    
    private void createDispatchFailureEvent(Long loadId, String reason) {
        String[] params = ArrayUtils.toArray("LTL Lifecycle", reason);
        LoadEventEntity loadEvent = LoadEventBuilder.buildEventEntity(loadId, LoadEventType.LD_DIS_ERR, params);
        eventDao.saveOrUpdate(loadEvent);
    }
    
    private void createCancelSuccessEvent(Long loadId) {
        String[] params = ArrayUtils.toArray("LTL Lifecycle");
        LoadEventEntity loadEvent = LoadEventBuilder.buildEventEntity(loadId, LoadEventType.LD_CNC_OK, params);
        eventDao.saveOrUpdate(loadEvent);
    }
    
    private void createCancelFailureEvent(Long loadId, String reason) {
        String[] params = ArrayUtils.toArray("LTL Lifecycle", reason);
        LoadEventEntity loadEvent = LoadEventBuilder.buildEventEntity(loadId, LoadEventType.LD_CNC_ERR, params);
        eventDao.saveOrUpdate(loadEvent);
    }
    
    private void createTrackSuccessEvent(Long loadId) {
        String[] params = ArrayUtils.toArray("LTL Lifecycle");
        LoadEventEntity loadEvent = LoadEventBuilder.buildEventEntity(loadId, LoadEventType.LD_TRK_OK, params);
        eventDao.saveOrUpdate(loadEvent);
    }
    
    private void createTrackFailureEvent(Long loadId, String reason) {
        String[] params = ArrayUtils.toArray("LTL Lifecycle", reason);
        LoadEventEntity loadEvent = LoadEventBuilder.buildEventEntity(loadId, LoadEventType.LD_TRK_ERR, params);
        eventDao.saveOrUpdate(loadEvent);
    }

    private void addTrackingEvent(LoadEntity load, CarrierEntity carrier) {
        try {
            LoadTrackingEntity loadTracking = new LoadTrackingEntity();
            loadTracking.setSource(EDI204);
            loadTracking.setLoad(load);
            loadTracking.setFreeMessage("EDI 204 Created");
            loadTracking.setCarrier(carrier);
            loadTracking.setStatusCode(TRACKING_STATUS_CODE);
            loadTracking.setTrackingDate(new Date());
            loadTracking.setTimezoneCode(TIMEZONE_CODE);
            loadTracking.setCreatedBy(SecurityUtils.getCurrentPersonId());
            loadTracking.setCreatedDate(new Date());
            loadTrackingDao.saveOrUpdate(loadTracking);
        } catch (Exception ex) {
            LOG.debug("Adding 204 event to load tracking failed: " + ex.getMessage(), ex);
        }
    }

    private void addBolTrackingEvent(LoadEntity load) {
        try {
            LoadTrackingEntity loadTracking = new LoadTrackingEntity();
            loadTracking.setSource(EDI211);
            loadTracking.setLoad(load);
            loadTracking.setCarrier(load.getCarrier());
            loadTracking.setStatusCode(TRACKING_STATUS_CODE);
            loadTracking.setTrackingDate(new Date());
            loadTracking.setTimezoneCode(TIMEZONE_CODE);
            loadTracking.setCreatedBy(SecurityUtils.getCurrentPersonId());
            loadTracking.setCreatedDate(new Date());
            loadTrackingDao.saveOrUpdate(loadTracking);
        } catch (Exception ex) {
            LOG.debug("Adding 211 event to load tracking failed: " + ex.getMessage(), ex);
        }
    }

    private void sendEDIThroughFTP(Long loadId, Long carrierId, ShipmentOperationType operationType) throws EdiProcessingException {
        List<Long> entityIds = Arrays.asList(loadId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EDI_OPERATION_TYPE_KEY, operationType);
        params.put(PRODUCTION_MODE_KEY, productionMode);
        ediService.sendEDI(carrierId, entityIds, EDITransactionSet._204, params);
    }

    private ShipmentOperationType getShipmentOperationType(OperationType operationType) {
        if (operationType.equals(OperationType.CANCEL)) {
            return ShipmentOperationType.CANCELLATION;
        } else if (operationType.equals(OperationType.TENDER)) {
            return ShipmentOperationType.TENDER;
        } else if (operationType.equals(OperationType.UPDATE)) {
            return ShipmentOperationType.UPDATE;
        }
        return null;
    }

    private String getPhoneNumber(PhoneNumber phone) {
        return phone != null ? (phone.getCountryCode() + phone.getAreaCode() + phone.getNumber()) : "";
    }

    private boolean isCarrierManualCapable(CarrierEntity carrier) {
        return carrier.getOrgServiceEntity() != null && CarrierIntegrationType.MANUAL == carrier.getOrgServiceEntity().getPickup()
                    && carrier.getOrgServiceEntity().getManualTypeEmail() != null;
    }

    private boolean isCarrierEdiCapable(CarrierEntity carrier) {
        return carrier.getOrgServiceEntity() == null || CarrierIntegrationType.EDI == carrier.getOrgServiceEntity().getPickup();
    }

    private boolean hasHazmat(Set<LoadMaterialEntity> materials) {
        return materials.stream().anyMatch(LoadMaterialEntity::isHazmat);
    }
}
