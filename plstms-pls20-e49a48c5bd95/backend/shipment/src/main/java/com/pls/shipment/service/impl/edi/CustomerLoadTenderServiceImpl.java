package com.pls.shipment.service.impl.edi;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToDao;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.address.UserAddressBookEntity;
import com.pls.core.domain.bo.PhoneBO;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.PhoneType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.enums.ShipmentFinancialStatus;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.EdiSettingsEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.EdiProcessingException;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.exception.IdentifiersUnavailableException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.exception.ShipmentNotFoundException;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.DBUtilityService;
import com.pls.core.service.address.AddressService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.util.PhoneUtils;
import com.pls.core.service.validation.ValidationException;
import com.pls.core.shared.AddressVO;
import com.pls.core.shared.Status;
import com.pls.location.dao.OrganizationLocationDao;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.dao.ShipmentNoteDao;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.ShipmentNoteEntity;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.domain.sterling.enums.OperationType;
import com.pls.shipment.service.LoadTenderService;
import com.pls.shipment.service.ShipmentSavingService;
import com.pls.shipment.service.ShipmentService;
import com.pls.shipment.service.audit.LoadEventBuilder;
import com.pls.shipment.service.edi.IntegrationService;
import com.pls.shipment.service.edi.LoadTenderAckService;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * Service for processing the LoadTender EDI messages. Existing shipment is updated/cancelled based on operation type
 * and a new shipment is created if it doesn't exist.
 *
 * @author Yasaman Honarvar
 *
 */
@Service("customerTenderService")
@Transactional(readOnly = true)
public class CustomerLoadTenderServiceImpl implements IntegrationService<LoadMessageJaxbBO> {

    @Autowired
    private ShipmentSavingService shipmentSavingService;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private LoadTrackingDao loadTrackingDao;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private LoadBuilderService loadBuilder;

    @Autowired
    private ShipmentEventDao shipmentEventDao;

    @Autowired
    private ShipmentNoteDao noteDao;

    @Autowired
    private EDIEmailSender ediEmailSender;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ContactInfoService contactInfoService;

    @Autowired
    private LoadTenderService loadTenderService;

    @Autowired
    private DBUtilityService dbUtilityService;

    @Autowired
    private BillToDao billToDao;

    @Autowired
    private OrganizationLocationDao locationDao;

    @Autowired
    @Qualifier("loadTenderAckService")
    private LoadTenderAckService loadTenderAckService;

    private static final String EDI_CREATE_CODE = "CE";

    private static final String EDI_UPDATE_CODE = "EU";

    private static final String EDI_CANCEL_CODE = "CN";

    private static final Logger LOG = LoggerFactory.getLogger(CustomerLoadTenderServiceImpl.class);

    /**
     * Processes the Load tender EDI messages. Shipment is cancelled/closed if the operation type is CANCEL else
     * shipment is created/updated.
     *
     * @param tender
     *            the loadTender message received by the application.
     * @throws ApplicationException
     *             throws exception in case the methods failed to operate properly.
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void processMessage(LoadMessageJaxbBO tender) throws ApplicationException {
        BillToEntity billTo = getBillTo(tender.getCustomerBillToId(), tender.getCustomerLocationId());
        LoadEntity load = findLoad(tender, billTo);
        if (tender.getOperationType() == OperationType.CANCEL) {
            cancelShipment(tender, load);
        } else {
            boolean ignoreEdiUpdates = (billTo.getEdiSettings() != null && billTo.getEdiSettings().isIgnore204Updates());
            if (load == null || load.getStatus() == ShipmentStatus.CANCELLED || !ignoreEdiUpdates) {
                createOrUpdateShipment(tender, load);
            }
        }
    }

    private void cancelShipment(LoadMessageJaxbBO loadMessage, LoadEntity load) throws InternalJmsCommunicationException, EntityNotFoundException,
    EdiProcessingException, ShipmentNotFoundException {

        if (load != null) {
            addTrackingEvent(load, getTrackingStatusCode(loadMessage.getOperationType()), loadMessage.getRecvDateTime());
            if (load.getStatus() != ShipmentStatus.CANCELLED) {
                shipmentService.cancelShipment(load.getId());
            }
        } else {
            throw new ShipmentNotFoundException(loadMessage.getShipmentNo(), loadMessage.getCustomerOrgId());
        }
    }

    private void createOrUpdateShipment(LoadMessageJaxbBO loadTender, LoadEntity load) throws ApplicationException {
        LoadEntity updatedLoad = load;
        ShipmentStatus oldStatus = (updatedLoad != null ? updatedLoad.getStatus() : null);
        boolean unawardLoad = false;
        CarrierEntity prevCarrier = (updatedLoad != null ? updatedLoad.getCarrier() : null);

        try {
            boolean eventAdded = addTrackingEvent(updatedLoad, getTrackingStatusCode(loadTender.getOperationType()), loadTender.getRecvDateTime());
            UserAddressBookEntity originAddr = getAddressBook(loadTender.getAddress(AddressType.ORIGIN), loadTender.getCustomerOrgId());
            UserAddressBookEntity destAddr = getAddressBook(loadTender.getAddress(AddressType.DESTINATION), loadTender.getCustomerOrgId());
            if (canUpdateLoad(oldStatus, load)) {
                unawardLoad = (oldStatus != null) && unawardLoad(loadTender, updatedLoad);
                updatedLoad = loadBuilder.createOrModifyLoad(loadTender, updatedLoad, originAddr, destAddr);
                if (unawardLoad) {
                    removeCarrierAssigment(updatedLoad);
                }
            }
            saveShipment(loadTender, updatedLoad, oldStatus, prevCarrier);

            eventAdded = eventAdded
                    || addTrackingEvent(updatedLoad, getTrackingStatusCode(loadTender.getOperationType()), loadTender.getRecvDateTime());
            sendAcknowledgment(updatedLoad, loadTender, canUpdateLoad(oldStatus, load));
        } catch (Exception ex) {
            sendAcknowledgment(load, loadTender, false);
            LOG.debug("Exception occurred while creating load from EDI: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    private void saveShipment(LoadMessageJaxbBO loadTender, LoadEntity load, ShipmentStatus oldStatus, CarrierEntity prevCarrier)
            throws ApplicationException, InternalJmsCommunicationException, EdiProcessingException {
        if (canUpdateLoad(oldStatus, load)) {
            shipmentSavingService.save(load, null, SecurityUtils.getCurrentPersonId(), SecurityUtils.getCurrentPersonId());

            ShipmentNoteEntity note = loadBuilder.buildNotes(load, loadTender);
            if (note != null) {
                noteDao.saveOrUpdate(note);
            }

            // This call is specifically needed here as save method is not sending the EDI to carriers upon shipment
            // status changes from Dispatched to booked or open and upon changes to load as the proposal is null;
            tenderLoad(load, oldStatus, prevCarrier);
        } else {
            addEdiIgnoreEvent(load);
        }

        if (oldStatus != null && (!canUpdateLoad(oldStatus, load) || oldStatus == ShipmentStatus.CANCELLED)) {
            UserAdditionalContactInfoBO contactInfo = contactInfoService.getContactInfo(load.getLocation().getActiveAccountExecutive().getUser());
            ediEmailSender.ediUpdateNotProcessed(load, loadTender, contactInfo.getEmail(), oldStatus == ShipmentStatus.CANCELLED,
                    loadBuilder.isMaterialsUpdated(load, loadTender));
        }
    }

    private void removeCarrierAssigment(LoadEntity load) {
        load.setCarrier(null);
        load.setAwardDate(null);
        load.setAwardedBy(null);
        load.setStatus(ShipmentStatus.OPEN);
        for (LoadCostDetailsEntity costDetailEntity : load.getActiveCostDetails()) {
            costDetailEntity.setStatus(Status.INACTIVE);
        }
    }

    private UserAddressBookEntity getAddressBook(AddressJaxbBO address, Long customerOrgId) throws ValidationException {
        UserAddressBookEntity addressBook = addressService.getCustomerAddressByCode(customerOrgId, address.getAddressCode());

        if (addressBook == null) {
            addressBook = new UserAddressBookEntity();
            addressBook.setAddress(buildAddressEntity(address));
            addressBook.setAddressCode(address.getAddressCode());
            addressBook.setAddressName(address.getName());
            addressBook.setContactName(address.getContactName());
            addressBook.setPhone(buildPhoneEntity(address.getContactPhone(), PhoneType.VOICE));
            addressBook.setEmail(address.getContactEmail());
            addressBook.setFax(buildPhoneEntity(address.getContactFax(), PhoneType.FAX));

            if (address.getAddressType() == AddressType.ORIGIN) {
                addressBook.setPickupNotes(address.getNotes());
                addressBook.setPickupFrom(getTime(address.getTransitDate().getFromHour(), address.getTransitDate().getFromMin()));
                addressBook.setPickupTo(getTime(address.getTransitDate().getToHour(), address.getTransitDate().getToMin()));
            } else if (address.getAddressType() == AddressType.DESTINATION) {
                addressBook.setDeliveryNotes(address.getNotes());
                addressBook.setDeliveryFrom(getTime(address.getTransitDate().getFromHour(), address.getTransitDate().getFromMin()));
                addressBook.setDeliveryTo(getTime(address.getTransitDate().getToHour(), address.getTransitDate().getToMin()));
            }
            addressBook.setStatus(Status.ACTIVE);
            addressService.saveOrUpdate(addressBook, customerOrgId, SecurityUtils.getCurrentPersonId(), false);
            dbUtilityService.flushSession();
        }
        return addressBook;
    }

    private Time getTime(String hours, String minutes) {
        if (StringUtils.isEmpty(minutes) || StringUtils.isEmpty(hours)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, Integer.parseInt(minutes));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
        return new Time(calendar.getTimeInMillis());
    }

    private PhoneEntity buildPhoneEntity(String contactPhone, PhoneType type) {
        if (StringUtils.isEmpty(contactPhone)) {
            return null;
        }
        PhoneBO phoneBO = PhoneUtils.parse(contactPhone);

        PhoneEntity phone = new PhoneEntity();
        phone.setNumber(phoneBO.getNumber());
        phone.setCountryCode(phoneBO.getCountryCode());
        phone.setAreaCode(phoneBO.getAreaCode());
        phone.setType(type);
        return phone;
    }

    private AddressEntity buildAddressEntity(AddressJaxbBO address) {
        AddressVO ad = new AddressVO();
        ad.setAddress1(address.getAddress1());
        ad.setAddress2(address.getAddress2());
        ad.setCity(address.getCity());
        ad.setCountryCode(address.getCountryCode());
        ad.setStateCode(address.getStateCode());
        ad.setPostalCode(address.getPostalCode());
        return addressService.getNewOrExistingAddress(ad);
    }

    private boolean unawardLoad(LoadMessageJaxbBO loadTender, LoadEntity load) {
        if (load.getStatus() != ShipmentStatus.BOOKED && load.getStatus() != ShipmentStatus.DISPATCHED) {
            return false;
        }

        if (load.getShipmentDirection() != ShipmentDirection.getByCode(loadTender.getInboundOutbound())) {
            return true;
        }

        if (isAddressChanged(loadTender, load)) {
            return true;
        }

        if (isPickupDateChanged(loadTender, load)) {
            return true;
        }

        return loadBuilder.isMaterialsUpdated(load, loadTender);
    }

    private boolean isAddressChanged(LoadMessageJaxbBO loadTender, LoadEntity load) {
        boolean addressChanged = false;
        if (!load.getOrigin().getAddressCode().equals(loadTender.getAddress(AddressType.ORIGIN).getAddressCode())) {
            addressChanged ^= load.getOrigin().getAddress().getZip().equals(loadTender.getAddress(AddressType.ORIGIN).getPostalCode());
        }

        if (!load.getDestination().getAddressCode().equals(loadTender.getAddress(AddressType.DESTINATION).getAddressCode())) {
            addressChanged = addressChanged
                    || !load.getDestination().getAddress().getZip().equals(loadTender.getAddress(AddressType.DESTINATION).getPostalCode());
        }

        return addressChanged;
    }

    private boolean isPickupDateChanged(LoadMessageJaxbBO loadTender, LoadEntity load) {
        Date pickupDate = loadTender.getAddress(AddressType.ORIGIN).getTransitDate().getFromDate();
        return !DateUtils.isSameDay(load.getOrigin().getEarlyScheduledArrival(), pickupDate);
    }

    private void sendAcknowledgment(LoadEntity load, LoadMessageJaxbBO loadTender, boolean accept) throws InternalJmsCommunicationException {
        loadTenderAckService.sendMessage(load, loadTender, accept);
    }

    private void addEdiIgnoreEvent(LoadEntity load) {
        LoadEventEntity ignoringEvent = LoadEventBuilder.buildEdiIgnoreEvent(load.getId());
        shipmentEventDao.saveOrUpdate(ignoringEvent);
    }

    private boolean canUpdateLoad(ShipmentStatus status, LoadEntity load) {
        if (status == null) {
            return true;
        }
        boolean canUpdateLoad = (load != null && load.getFinalizationStatus() == ShipmentFinancialStatus.NONE);
        switch (status) {
        case OPEN:
        case BOOKED:
        case DISPATCHED:
        case CANCELLED:
            break;
        default:
            canUpdateLoad = false;
            break;
        }

        return canUpdateLoad;
    }

    private LoadEntity findLoad(LoadMessageJaxbBO loadTender, BillToEntity billTo) throws IdentifiersUnavailableException {
        if (loadTender.getShipmentNo() == null) {
            throw new IdentifiersUnavailableException(loadTender.getCustomerOrgId(), "ShipmentNo");
        }

        if (shouldSearchByBolAndRef(billTo)) {
            if (loadTender.getBol() == null) {
                throw new IdentifiersUnavailableException(loadTender.getCustomerOrgId(), "BOL");
            } else {
                return ltlShipmentDao.findShipmentByBolAndShipmentNumber(loadTender.getCustomerOrgId(), loadTender.getShipmentNo(),
                        loadTender.getBol());
            }
        }
        return ltlShipmentDao.findShipmentByShipmentNumber(loadTender.getCustomerOrgId(), loadTender.getShipmentNo());
    }

    private Boolean shouldSearchByBolAndRef(BillToEntity billTo) {
        if (billTo != null) {
            EdiSettingsEntity ediSetting = billTo.getEdiSettings();
            if (ediSetting != null && ediSetting.isUniqueRefAndBol()) {
                return true;
            }
        }
        return false;
    }

    private BillToEntity getBillTo(Long billToId, Long locationId) {
        BillToEntity billTo = null;
        if (billToId != null) {
            billTo = billToDao.find(billToId);
        } else if (locationId != null) {
            OrganizationLocationEntity location = locationDao.find(locationId);
            if (location != null) {
                billTo = location.getBillTo();
            }
        }
        return billTo;
    }

    private String getTrackingStatusCode(OperationType operationType) {
        switch (operationType) {
        case TENDER:
            return EDI_CREATE_CODE;
        case UPDATE:
            return EDI_UPDATE_CODE;
        case CANCEL:
            return EDI_CANCEL_CODE;
        default:
            break;
        }
        return null;
    }

    private void tenderLoad(LoadEntity load, ShipmentStatus oldStatus, CarrierEntity prevCarrier)
            throws EdiProcessingException, InternalJmsCommunicationException {
        if (((oldStatus == ShipmentStatus.DISPATCHED && (load.getStatus() == ShipmentStatus.DISPATCHED || load.getStatus() == ShipmentStatus.BOOKED
                || load.getStatus() == ShipmentStatus.OPEN))) && isCarrierEdiCapable(prevCarrier)) {
            loadTenderService.tenderLoad(load, prevCarrier, oldStatus, load.getStatus());
        }
    }

    private boolean isCarrierEdiCapable(CarrierEntity carrier) {
        return carrier != null && (carrier.getOrgServiceEntity() == null || CarrierIntegrationType.API != carrier.getOrgServiceEntity().getPickup());
    }

    private boolean addTrackingEvent(LoadEntity load, String statusCode, Date recvDate) {
        try {
            LoadTrackingEntity loadTracking = new LoadTrackingEntity();
            loadTracking.setSource(EDI_204);
            loadTracking.setLoadId(load.getId());
            loadTracking.setCarrier(load.getCarrier());
            loadTracking.setStatusCode(statusCode);
            loadTracking.setTrackingDate((recvDate != null) ? recvDate : new Date());
            loadTracking.setTimezoneCode(TIME_ZONE);
            loadTracking.setCreatedBy(SecurityUtils.getCurrentPersonId());
            loadTracking.setCreatedDate(new Date());
            loadTrackingDao.saveOrUpdate(loadTracking);
            return true;
        } catch (Exception ex) {
            LOG.debug("Adding 204 event to load tracking failed: " + ex.getMessage(), ex);
        }
        return false;
    }
}
