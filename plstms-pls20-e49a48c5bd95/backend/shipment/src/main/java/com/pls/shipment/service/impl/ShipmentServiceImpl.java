package com.pls.shipment.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OrganizationNotificationsDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.domain.bo.AuditReasonBO;
import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.core.domain.enums.OrganizationStatus;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.organization.OrganizationNotificationsEntity;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.domain.usertype.LoadStatusUserType;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.service.CustomerService;
//import com.pls.core.exception.XmlSerializationException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.pdf.exception.PDFGenerationException;
import com.pls.documentmanagement.dao.RequiredDocumentDao;
import com.pls.documentmanagement.domain.enums.DocumentTypes;
import com.pls.extint.shared.TrackingResponseVO;
import com.pls.organization.domain.bo.PaperworkEmailBO;
import com.pls.shipment.dao.LoadAdditionalInfoEntityDao;
import com.pls.shipment.dao.LoadPricingDetailsDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadAdditionalInfoEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadMaterialEntity;
import com.pls.shipment.domain.LoadNotificationsEntity;
import com.pls.shipment.domain.LoadPricingDetailsEntity;
import com.pls.shipment.domain.LtlLoadAccessorialEntity;
import com.pls.shipment.domain.bo.LocationDetailsReportBO;
import com.pls.shipment.domain.bo.LocationLoadDetailsReportBO;
import com.pls.shipment.domain.bo.QuotedBO;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.domain.bo.ShipmentMissingPaperworkBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBO;
import com.pls.shipment.service.LoadTenderService;
import com.pls.shipment.service.ShipmentAlertService;
import com.pls.shipment.service.ShipmentDocumentService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.ShipmentUtils;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

/**
 * Shipment Service implementation.
 * @author Gleb Zgonikov
 */
@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private static final String CREDIT_LIMIT_EXCEPTION_MSG = "We are unable to successfully book this shipment. "
            + "Please contact an Accounts Receivable representative for assistance:  ar@plslogistics.com";

    private static final ShipmentStatus[] SHIPMENT_CONFIRMED_STATUSES = new ShipmentStatus[] { ShipmentStatus.DISPATCHED,
            ShipmentStatus.IN_TRANSIT, ShipmentStatus.OUT_FOR_DELIVERY, ShipmentStatus.DELIVERED };

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private RequiredDocumentDao requiredDocumentDao;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Autowired
    private ShipmentEmailSender shipmentEmailSender;

    @Autowired
    private LoadTenderService loadTenderService;

    @Autowired
    private ShipmentAlertService shipmentAlertService;

    @Autowired
    private BillingAuditService billingAuditService;

    @Autowired
    private ShipmentDocumentService shipmentDocumentService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private LoadAdditionalInfoEntityDao loadAdditionalInfoEntityDao;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoadPricingDetailsDao loadPricingDetailsDao;
    
    @Autowired
    private OrganizationNotificationsDao organizationNotificationsDao;

    @Override
    public LoadEntity findById(Long shipmentId) {
        return ltlShipmentDao.find(shipmentId);
    }

    @Override
    public List<ShipmentListItemBO> findLastShipments(Long customerId, Long userId, int count) {
        return ltlShipmentDao.findLastNShipments(customerId, userId, count);
    }

    @Override
    public LoadEntity getCopyOfShipment(Long loadId) {
        LoadEntity copy = ltlShipmentDao.find(loadId);

        copy.setId(null);
        Date pickupDate = new Date();
        for (LoadDetailsEntity details : copy.getLoadDetails()) {
            details.setId(null);
            details.setDeparture(null);

            Set<LoadMaterialEntity> materials = details.getLoadMaterials();
            if (materials != null && !materials.isEmpty()) {
                for (LoadMaterialEntity material : materials) {
                    material.setId(null);
                    material.setPickupDate(pickupDate);
                }
            }
        }
        copy.setCarrier(null);
        copy.setCostDetails(null);
        copy.setActiveCostDetails(null);
        copy.getNumbers().setProNumber(null);
        copy.getNumbers().setPoNumber(null);
        copy.getNumbers().setPuNumber(null);
        copy.getNumbers().setRefNumber(null);
        copy.getNumbers().setBolNumber(null);
        copy.getNumbers().setGlNumber(null);
        copy.getNumbers().setOpBolNumber(null);
        copy.getNumbers().setPartNumber(null);
        copy.getNumbers().setTrailerNumber(null);
        copy.getNumbers().setSoNumber(null);
        copy.setPrepaidDetails(null);
        copy.setBillTo(null);
        copy.setLocation(null);
        copy.setLocationId(null);
        copy.getOrigin().setEarlyScheduledArrival(new Date());
        copy.setDeliveryNotes(null);
        copy.setSpecialInstructions(null);
        copy.setBolInstructions(null);
        copy.setMileage(null);
        copy.setVolumeQuoteId(null);
        copy.getModification().setCreatedDate(new Date());
        copy.setStatus(ShipmentStatus.OPEN);
        copy.setFreightBillPayTo(new FreightBillPayToEntity());
        copy.getNumbers().setJobNumbers(null);
        copy.setRequestedBy(null);
        if (copy.getLoadAdditionalFields() != null) {
            copy.getLoadAdditionalFields().setCargoValue(null);
        }

        return copy;
    }

    @Override
    public LoadEntity getShipmentWithAllDependencies(Long shipmentId) {
        return ltlShipmentDao.getShipmentWithAllDependencies(shipmentId);
    }

    @Override
    public void cancelShipment(long shipmentId) throws InternalJmsCommunicationException, EntityNotFoundException, EdiProcessingException {
        LoadEntity shipment = ltlShipmentDao.find(shipmentId);
        if (canBeCancelled(shipment)) {
            ShipmentStatus oldStatus = shipment.getStatus();
            closeLoad(shipment);
            if (oldStatus == ShipmentStatus.DISPATCHED) {
                loadTenderService.tenderLoad(shipment, shipment.getCarrier(), oldStatus, ShipmentStatus.CANCELLED);
            }
        }
    }

    @Override
    public void checkPaperworkRequiredForCustomerInvoice(LoadEntity load) {
        boolean isAllPaperworkPresent = requiredDocumentDao.isAllPaperworkRequiredForBillToInvoicePresent(load.getId());
        if (load.isCustReqDocPresent() ^ isAllPaperworkPresent) {
            load.setCustReqDocPresent(isAllPaperworkPresent);
            ltlShipmentDao.update(load);
        }
    }

    @Override
    public List<ShipmentListItemBO> findDeliveredShipmentsForVendorBill(String bol, String pro,
            String originZip, String destinationZip, Long carrierId, Date actualPickupDate) {

        return ltlShipmentDao.findMatchedShipmentsInfo(bol, pro, originZip, destinationZip, carrierId, actualPickupDate);
    }

    private boolean canBeCancelled(LoadEntity shipment) {
        boolean shipmentInvoiced = shipment.getFinalizationStatus() == ShipmentFinancialStatus.ACCOUNTING_BILLING_RELEASE;
        if (!shipmentInvoiced) {
            if (shipment.getStatus() != ShipmentStatus.BOOKED && shipment.getStatus() != ShipmentStatus.DISPATCHED
                    && shipment.getStatus() != ShipmentStatus.OPEN) {
                userPermissionsService.checkCapability(Capabilities.CAN_CANCEL_ORDER.name());
            }

            return true;
        } else {
            throw new AccessDeniedException(String.format("Cannot cancel shipment with ID: %s that has been invoiced", shipment.getId()));
        }
    }

    @Override
    public List<ShipmentMissingPaperworkBO> getShipmentsWithMissingReqPaperwork(Long shipmentId) {
        return ltlShipmentDao.getShipmentsWithMissingReqPaperwork(shipmentId);
    }

    @Override
    public void dispatchShipment(Long shipmentId)
            throws ApplicationException {
        LoadEntity shipment = ltlShipmentDao.find(shipmentId);
        if (shipment.getStatus() == ShipmentStatus.BOOKED) {
            loadTenderService.tenderLoad(shipment, shipment.getCarrier(), shipment.getStatus(), ShipmentStatus.DISPATCHED);
            
            shipment.setStatus(ShipmentStatus.DISPATCHED);
            ltlShipmentDao.saveOrUpdate(shipment);
            
            //need to regenerate BOL, as tendering might update PU number
            shipmentDocumentService.generateShipmentDocuments(Collections.<DocumentTypes>emptySet(), shipment, false, SecurityUtils.getCurrentPersonId());
            
            shipmentEmailSender.sendLoadStatusChangedNotification(shipment, ShipmentStatus.DISPATCHED);
        } else {
            throw new AccessDeniedException(String.format(
                    "Cannot dispatch shipment with ID: %s. Only 'Booked' shipments are allowed to be dispatched", shipment.getId()));
        }
    }

    @Override
    public List<ShipmentTrackingBO> getShipmentsToTrack() {
        return ltlShipmentDao.getShipmentsToTrack();
    }

    @Override
    public void updateLoadStatus(TrackingResponseVO load, Long personId) {
        if (ShipmentStatus.IN_TRANSIT == LoadStatusUserType.getStatusFromPLS1Status(load.getCurrentStatus())) {
            ltlShipmentDao.confirmPickup(load, personId);
        } else if (ShipmentStatus.DELIVERED == LoadStatusUserType.getStatusFromPLS1Status(load.getCurrentStatus())) {
            ltlShipmentDao.confirmDelivery(load, personId);
        }
    }

    @Override
    public QuotedBO getPrimaryLoadCostDetail(Long loadId) {
        return ltlShipmentDao.getPrimaryLoadCostDetail(loadId);
    }

    @Override
    public void closeLoad(long shipmentId) throws EntityNotFoundException {
        LoadEntity shipment = ltlShipmentDao.find(shipmentId);
        if (canBeCancelled(shipment)) {
            closeLoad(shipment);
        }
    }

    private void closeLoad(LoadEntity shipment) {
        ltlShipmentDao.updateStatus(shipment.getId(), ShipmentStatus.CANCELLED);
        shipmentAlertService.deactivateAlerts(shipment);
    }

    @Override
    public void overrideDateHold(Long shipmentId) {
        ltlShipmentDao.updateLoadFinancialStatuses(new AuditReasonBO(shipmentId), ShipmentFinancialStatus.ACCOUNTING_BILLING_HOLD);
        billingAuditService.updateBillingAuditReasonForLoad(shipmentId, ShipmentFinancialStatus.NONE);
    }

    @Override
    public void updateFrtBillDate(Date freightBillDate, Long loadId) {
        LoadEntity load = ltlShipmentDao.find(loadId);
        checkPaperworkRequiredForCustomerInvoice(load);
        if (ShipmentUtils.isCanUpdateFrtBillDate(load)) {
            load.getVendorBillDetails().setFrtBillRecvDate(freightBillDate);
        }
        ltlShipmentDao.saveOrUpdate(load);
    }

    @Override
    public Map<DocumentTypes, Long> regenerateDocsForShipment(Long loadId, Long markup, Boolean hideCreatedTime) throws PDFGenerationException {
        LoadAdditionalInfoEntity additionalInfoEntity = loadAdditionalInfoEntityDao.getAdditionalInfoByLoadId(loadId);
        if (additionalInfoEntity == null) {
            additionalInfoEntity = new LoadAdditionalInfoEntity();
        }
        LoadEntity load = findById(loadId);
        additionalInfoEntity.setMarkup(markup);
        additionalInfoEntity.setLoad(load);
        additionalInfoEntity.setLoadId(load.getId());
        loadAdditionalInfoEntityDao.saveOrUpdate(additionalInfoEntity);
        load.setLoadAdditionalInfo(additionalInfoEntity);
        Map<DocumentTypes, Long> resultMap = shipmentDocumentService.generateShipmentDocuments(
                Collections.singleton(DocumentTypes.CONSIGNEE_INVOICE), load, hideCreatedTime, SecurityUtils.getCurrentPersonId());
        return resultMap;
    }

    @Override
    public boolean checkBillToCreditLimitSafe(LoadEntity load) {
        try {
            if (load != null) {
                checkBillToCreditLimit(load);
            }
        } catch (ApplicationException e) {
            return false;
        }
        return true;
    }

    @Override
    public void checkBillToCreditLimit(LoadEntity load) throws ApplicationException {
        Objects.requireNonNull(load);

        if (isCreditLimitRequired(load.getOrganization()) && ShipmentStatus.isActive(load.getStatus()) && load.getBillTo() != null) {
            if (load.getId() == null) {
                BillToEntity billTo = load.getBillTo();
                checkBillToAvailableAmount(billTo, load.getActiveCostDetail().getTotalRevenue());
            } else {
                checkLimitForExistingLoad(load);
            }
        }
    }

    @Override
    public List<BigDecimal> getMatchedLoadsByProAndOrgId(String proNum, Long orgId) {
        String purePro = proNum.replaceAll("[^\\w]", StringUtils.EMPTY).
                replaceAll("[_]", StringUtils.EMPTY).
                replaceFirst("^0+(?!$)", StringUtils.EMPTY);

        return ltlShipmentDao.findMatchedLoadsByProAndOrgId(purePro, orgId);
    }

    @Override
    public void addImplicitOverdimentionalAccessorial(LoadEntity loadEntity) {
        if (loadEntity.getActiveCostDetail().getCostDetailItems().stream()
                .anyMatch(i -> LtlAccessorialType.OVER_DIMENSION.getCode().equals(i.getAccessorialType()))) {
            if (loadEntity.getLtlAccessorials() == null) {
                loadEntity.setLtlAccessorials(new HashSet<LtlLoadAccessorialEntity>());
            }
            if (loadEntity.getLtlAccessorials().stream()
                    .noneMatch(a -> LtlAccessorialType.OVER_DIMENSION.getCode().equals(a.getAccessorial().getId()))) {
                AccessorialTypeEntity accessorialType = new AccessorialTypeEntity(LtlAccessorialType.OVER_DIMENSION.getCode());
                LtlLoadAccessorialEntity accessorialEntity = new LtlLoadAccessorialEntity();
                accessorialEntity.setLoad(loadEntity);
                accessorialEntity.setAccessorial(accessorialType);
                loadEntity.getLtlAccessorials().add(accessorialEntity);
            }
        }
    }

    @Override
    public void updateTimeZoneInfo(LoadEntity load) {
        updateLoadDetailsWithTimeZone(load.getOrigin());
        updateLoadDetailsWithTimeZone(load.getDestination());
    }

    private void updateLoadDetailsWithTimeZone(LoadDetailsEntity loadDetails) {
        TimeZoneEntity timeZone = addressService.findTimeZoneByCountryZip(loadDetails.getAddress().getCountry().getId(),
                loadDetails.getAddress().getZip());
        loadDetails.setArrivalTimeZone(timeZone);
        loadDetails.setDepartureTimeZone(timeZone);
        loadDetails.setEarlyScheduledArrivalTimeZone(timeZone);
        loadDetails.setScheduledArrivalTimeZone(timeZone);
    }

    private boolean isCreditLimitRequired(OrganizationEntity organization) {
        if (organization != null) {
            return customerService.getCreditLimitRequited(organization.getId());
        } else {
            return false;
        }
    }

    private void checkBillToAvailableAmount(BillToEntity billTo, BigDecimal totalRevenue) throws ApplicationException {
        boolean creditHold = BooleanUtils.isTrue(billTo.getCreditHold());
        boolean customerHold = billTo.getOrganization() != null && OrganizationStatus.HOLD == billTo.getOrganization().getStatus();
        boolean autoCreditHold = BooleanUtils.isTrue(billTo.getAutoCreditHold());

        if (creditHold || customerHold) {
            throw new ApplicationException(CREDIT_LIMIT_EXCEPTION_MSG);
        }

        if (!BooleanUtils.isTrue(billTo.getOverrideCreditHold())) {
            autoCreditHold = getAutoCreditHoldSettingsFromCustomerOrNetwork(billTo);
        }
        if (autoCreditHold) {
            checkAvailableAmount(billTo, totalRevenue);
        }
    }

    private boolean getAutoCreditHoldSettingsFromCustomerOrNetwork(BillToEntity billTo) {
        OrganizationEntity billToCustomer = billTo.getOrganization();
        if (billToCustomer != null) {
            if (BooleanUtils.isTrue(billToCustomer.getOverrideCreditHold())) {
                return BooleanUtils.isTrue(billToCustomer.getAutoCreditHold());
            }
            if (billToCustomer.getNetwork() != null) {
                return BooleanUtils.isTrue(billToCustomer.getNetwork().getAutoCreditHold());
            }
        }
        return Boolean.FALSE;
    }

    private void checkAvailableAmount(BillToEntity billTo, BigDecimal totalRevenue) throws ApplicationException {
        BigDecimal availableAmount = billTo.getAvailableCreditAmount();
        if (availableAmount.compareTo(totalRevenue) < 0) {
            throw new ApplicationException(CREDIT_LIMIT_EXCEPTION_MSG);
        }
    }

    private void checkLimitForExistingLoad(LoadEntity load) throws ApplicationException {
        if (isConditionsChanged(load)) {
            BillToEntity newBillTo = load.getBillTo();
            BigDecimal newRevenue = load.getActiveCostDetail().getTotalRevenue();

            checkBillToAvailableAmount(newBillTo, newRevenue);
        }
    }

    private boolean isConditionsChanged(LoadEntity load) {
        Long oldBillToId = ltlShipmentDao.getShipmentBillTo(load.getId());
        BillToEntity newBillTo = load.getBillTo();
        ShipmentStatus oldStatus = ltlShipmentDao.getShipmentStatus(load.getId());
        ShipmentStatus newStatus = load.getStatus();

        return isStatusChanged(oldStatus, newStatus) || isBillToChanged(oldBillToId, newBillTo.getId());
    }

    private boolean isBillToChanged(Long oldBillToId, Long newBillToId) {
        return ObjectUtils.notEqual(oldBillToId, newBillToId);
    }

    private boolean isStatusChanged(ShipmentStatus oldStatus, ShipmentStatus newStatus) {
        return ArrayUtils.contains(SHIPMENT_CONFIRMED_STATUSES, newStatus) && !ArrayUtils.contains(SHIPMENT_CONFIRMED_STATUSES, oldStatus);
    }

    @Override
    public LoadPricingDetailsEntity getShipmentPricingDetails(Long shipmentId) {
        return loadPricingDetailsDao.getShipmentPricingDetails(shipmentId);
    }

    @Override
    public List<PaperworkEmailBO> getPaperworkEmails(int days) {
        return ltlShipmentDao.getPaperworkEmails(days);
    }

    @Override
    public List<LocationDetailsReportBO> getLocationDetails(Long personId) {
        return ltlShipmentDao.getLocationDetails(personId).stream().collect(Collectors.groupingBy(LocationDetailsReportBO::getHashCode)).values()
                .stream().map(list -> {
                    LocationDetailsReportBO firstItem = list.get(0);
                    firstItem.setCount(list.size());
                    return firstItem;
                })
                .sorted(Comparator.comparing(LocationDetailsReportBO::getCount, Comparator.reverseOrder())
                        .thenComparing(LocationDetailsReportBO::getZip))
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationLoadDetailsReportBO> getLocationLoadDetails(String zip, String city, Boolean origin, int dateType, Long personId) {
        return ltlShipmentDao.getLocationLoadDetails(zip, city, origin, dateType, personId);
    }
    
    @Override
    public void addOrganizationNotifications(LoadEntity load) {
        List<OrganizationNotificationsEntity> organizationNotifications = organizationNotificationsDao.findByOrgnId(load.getOrganization().getId());
        if(load.getLoadNotifications()==null) {
            load.setLoadNotifications(new HashSet<LoadNotificationsEntity>());
        }
        load.getLoadNotifications().addAll(organizationNotifications.stream().map(it->{
            LoadNotificationsEntity loadNotification = new LoadNotificationsEntity();
            loadNotification.setEmailAddress(it.getEmail());
            loadNotification.setLoad(load);
            loadNotification.setNotificationType(it.getNotificationType());
            return loadNotification;
        }).collect(Collectors.toList()));
    }

}
