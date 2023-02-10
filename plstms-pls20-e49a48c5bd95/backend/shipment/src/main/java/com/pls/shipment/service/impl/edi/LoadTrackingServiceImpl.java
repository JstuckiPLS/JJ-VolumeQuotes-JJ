package com.pls.shipment.service.impl.edi;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.EdiType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.EdiSettingsEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.exception.ShipmentNotFoundException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentUpdateMessage;
import com.pls.ltlrating.integration.ltllifecycle.dto.message.ShipmentUpdateMessage.ShipmentUpdate;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.LoadTrackingJaxbBO;
import com.pls.shipment.domain.sterling.TrackingStatusJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.service.ShipmentAlertService;
import com.pls.shipment.service.edi.IntegrationService;
import com.pls.shipment.service.edi.LoadTrackingService;
import com.pls.shipment.service.impl.email.EDIEmailSender;
import com.pls.shipment.service.impl.email.ShipmentEmailSender;

/**
 * This Service is provided to facilitates EDI 214 processing. System receives one or more EDI 214 from carrier.
 *
 * @author Jasmin Dhamelia
 */
@Service("loadTrackingService")
@Transactional(readOnly = true)
public class LoadTrackingServiceImpl implements IntegrationService<LoadTrackingJaxbBO>, LoadTrackingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadTrackingServiceImpl.class);

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private LoadTrackingDao loadTrackingDao;

    @Autowired
    private ShipmentEmailSender shipmentEmailSender;

    @Autowired
    private ShipmentAlertService shipmentAlertService;

    @Autowired
    private EDIEmailSender ediEmailSender;

    @Autowired
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;

    @Value("${admin.personId}")
    private Long systemUserId;

    /**
     * This handler dispatches the LoadTracking entity to the right dao in order to handle EDI214 and updates the load accordingly.
     *
     * @param loadTrackingBO
     *            the object holding the load tracking information.
     * @throws ApplicationException
     *             application exception.
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void processMessage(LoadTrackingJaxbBO loadTrackingBO) throws ApplicationException {
        try {
            List<LoadEntity> loads = ltlShipmentDao.findShipmentsByScacAndBolNumber(loadTrackingBO.getScac(), loadTrackingBO.getBol());
            if (loads.size() == 1) {
                LoadEntity load = loads.get(0);
                if (load.getLoadAdditionalFields() != null && "LTLLC".equals(load.getLoadAdditionalFields().getTrackedVia())) {
                    LOGGER.info("Received tracking event from EDI, but load is tracked via LTLLC in the system. LoadId [{}]", load.getId());
                    return;
                }
                
                ShipmentStatus loadStatus = load.getStatus();
                boolean saveLoad = createTrackingEntity(load, loadTrackingBO);
                if (saveLoad) {
                    ltlShipmentDao.saveOrUpdate(load);
                }
                // Load Status has changed. Send load status changed notification emails.
                if (loadStatus != load.getStatus()) {
                    shipmentEmailSender.sendLoadStatusChangedNotification(load, load.getStatus());
                    shipmentEmailSender.sendGoShipTrackingUpdateEmail(load, loadStatus);
                    shipmentAlertService.processShipmentAlerts(load);
                }
            } else {
                String errorMsg = String.format("Saving Shipment Tracking failed. Unable to find unique Shipment by BOL# '%s' for Carrier '%s'. "
                        + "Actually %d shipments were found.", loadTrackingBO.getBol(), loadTrackingBO.getScac(), loads.size());
                throw new ShipmentNotFoundException(errorMsg);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            ediEmailSender.loadTrackingFailed(loadTrackingBO.getB2biFileName(), e.getMessage(), EDI_214.toString(), null);
            throw e;
        }
    }
    
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void processLtllcTrackingMessage(ShipmentUpdateMessage shipmentUpdateMessage) throws ApplicationException {
        try {
            List<LoadEntity> loads = ltlShipmentDao.findShipmentsByScacAndBolNumber(shipmentUpdateMessage.getCarrierCode(), shipmentUpdateMessage.getBolNumber());
            if (loads.size() == 1) {
                LoadEntity load = loads.get(0);
                if (load.getLoadAdditionalFields() == null || !"LTLLC".equals(load.getLoadAdditionalFields().getTrackedVia())) {
                    LOGGER.info("Received tracking event from LTLLC, but load is not tracked via LTLLC in the system. LoadId [{}]", load.getId());
                    return;
                }
                
                ShipmentStatus loadStatus = load.getStatus();
                boolean saveLoad = createTrackingEntityLTLLC(load, shipmentUpdateMessage);
                if (saveLoad) {
                    ltlShipmentDao.saveOrUpdate(load);
                }
                // Load Status has changed. Send load status changed notification emails.
                if (loadStatus != load.getStatus()) {
                    shipmentEmailSender.sendLoadStatusChangedNotification(load, load.getStatus());
                    shipmentEmailSender.sendGoShipTrackingUpdateEmail(load, loadStatus);
                    shipmentAlertService.processShipmentAlerts(load);
                }
            } else {
                String errorMsg = String.format("Saving Shipment Tracking failed. Unable to find unique Shipment by BOL# '%s' for Carrier '%s'. "
                        + "Actually %d shipments were found.", shipmentUpdateMessage.getBolNumber(), shipmentUpdateMessage.getCarrierCode(), loads.size());
                throw new ShipmentNotFoundException(errorMsg);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            ediEmailSender.loadTrackingLTLLCFailed(e.getMessage(), null);
            throw e;
        }
    }
    
    /** Handle the updates in the updateMessage. */
    private boolean createTrackingEntityLTLLC(LoadEntity load, ShipmentUpdateMessage updateMessage) {
        boolean loadChanged = updateLoadDetailsFromUpdate(load, updateMessage);
        
        for (ShipmentUpdate shipmentUpdate : updateMessage.getShipmentUpdates()) {
            LoadTrackingEntity loadTrackingEntity = transformToLoadTrackingEntity(updateMessage, shipmentUpdate, load.getCarrier());
            loadTrackingEntity.setLoad(load);
            
            loadChanged |= saveLoadTracking(loadTrackingEntity, load, shipmentUpdate.getLoadStatus().getPls20Code());
        }
        return loadChanged;
    }
    
    /** Set details of load using information from the update (not overwriting existing values). */
    private boolean updateLoadDetailsFromUpdate(LoadEntity load, ShipmentUpdateMessage updateMessage) {
        boolean loadChanged = false;
        if(load.getNumbers().getProNumber()==null && updateMessage.getProNumber()!=null) {
            load.getNumbers().setProNumber(updateMessage.getProNumber());
            loadChanged = true;
        }
        if(load.getNumbers().getPuNumber()==null && updateMessage.getPuNumber()!=null) {
            load.getNumbers().setPuNumber(updateMessage.getPuNumber());
            loadChanged = true;
        }
        if(updateMessage.getLastReportedPickupDateTime()!=null) {
            loadChanged |= setDepartureTime(toDate(updateMessage.getLastReportedPickupDateTime()), load.getOrigin());
        }
        if(updateMessage.getLastReportedDeliveryDateTime()!=null) {
            loadChanged |= setDepartureTime(toDate(updateMessage.getLastReportedDeliveryDateTime()), load.getDestination());
        }
        return loadChanged;
    }

    /** Translates from ZonedDateTime to Date. */
    private Date toDate(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : Date.from(zonedDateTime.toInstant());
    }

    /** Build a LoadTrackingEntity from LTLLC shipment update message&shipment update */
    private LoadTrackingEntity transformToLoadTrackingEntity(ShipmentUpdateMessage updateMessage, ShipmentUpdate shipmentUpdate, CarrierEntity carrier) {
        LoadTrackingEntity loadTracking = new LoadTrackingEntity();
        loadTracking.setScac(updateMessage.getCarrierCode());
        loadTracking.setCarrier(carrier);
        loadTracking.setBol(updateMessage.getBolNumber());
        loadTracking.setCreatedBy(systemUserId);
        loadTracking.setPro(updateMessage.getProNumber());
        loadTracking.setTrackingDate(shipmentUpdate.getTransactionDate() != null ? Date.from(shipmentUpdate.getTransactionDate().toInstant()) : new Date());
        loadTracking.setTimezoneCode(TIME_ZONE);
        loadTracking.setSource(LTL_LIFECYCLE);

        loadTracking.setStatusCode(shipmentUpdate.getStatus().name());
        if (shipmentUpdate.getLoadStatus() != null) {
            switch (shipmentUpdate.getLoadStatus()) {
            case IN_TRANSIT:
                loadTracking.setDepartureTime(toDate(updateMessage.getLastReportedPickupDateTime()));
                break;
            case DELIVERED:
                loadTracking.setDepartureTime(toDate(updateMessage.getLastReportedDeliveryDateTime()));
                break;
            default: // NOOP
            }
        }
        
        loadTracking.setFreeMessage(shipmentUpdate.getNotes());

        if (shipmentUpdate.getAddress() != null) {
            loadTracking.setCity(shipmentUpdate.getAddress().getCity());
            loadTracking.setState(shipmentUpdate.getAddress().getState());
            loadTracking.setCountry(shipmentUpdate.getAddress().getCountry());
            loadTracking.setPostalCode(shipmentUpdate.getAddress().getZip());
        }

        return loadTracking;
    }

    private boolean createTrackingEntity(LoadEntity load, LoadTrackingJaxbBO loadTrackingBO) {
        boolean saveLoad = false;
        for (TrackingStatusJaxbBO trackingStatus : loadTrackingBO.getTrackingStatuses()) {
            LoadTrackingEntity loadTracking = setLoadInfo(loadTrackingBO, load.getCarrier());
            setTrackStatusInfo(trackingStatus, loadTracking);
            loadTracking.setLoad(load);
            saveLoad = saveLoad | saveLoadTracking(loadTracking, load, trackingStatus.getLoadStatus());
        }
        return saveLoad;
    }

    private LoadTrackingEntity setLoadInfo(LoadTrackingJaxbBO loadTrackingBO, CarrierEntity carrier) {
        LoadTrackingEntity loadTracking = new LoadTrackingEntity();
        loadTracking.setScac(loadTrackingBO.getScac());
        loadTracking.setCarrier(carrier);
        loadTracking.setBol(loadTrackingBO.getBol());
        loadTracking.setCreatedBy(loadTrackingBO.getPersonId());
        loadTracking.setPro(loadTrackingBO.getProNumber());
        loadTracking.setEdiAccount(loadTrackingBO.getEdiAccountNum());
        loadTracking.setTrackingDate(loadTrackingBO.getRecvDateTime());
        loadTracking.setTimezoneCode(TIME_ZONE);
        loadTracking.setSource(EDI_214);

        return loadTracking;
    }

    private void setTrackStatusInfo(TrackingStatusJaxbBO trackingStatus, LoadTrackingEntity loadTracking) {
        loadTracking.setStatusCode(StringUtils.trimToNull(trackingStatus.getStatus()));
        loadTracking.setDepartureTime(trackingStatus.getTransactionDate());
        loadTracking.setStatusReasonCode(trackingStatus.getStatusReason());
        loadTracking.setFreeMessage(trackingStatus.getNotes());

        if (trackingStatus.getTrackingStatusAddress() != null) {
            loadTracking.setCity(trackingStatus.getTrackingStatusAddress().getCity());
            loadTracking.setState(trackingStatus.getTrackingStatusAddress().getState());
            loadTracking.setCountry(trackingStatus.getTrackingStatusAddress().getCountry());
            loadTracking.setPostalCode(trackingStatus.getTrackingStatusAddress().getPostalCode());
        }
    }

    private boolean saveLoadTracking(LoadTrackingEntity loadTracking, LoadEntity load, String loadStatus) {
        LoadTrackingEntity entity = loadTrackingDao.saveOrUpdate(loadTracking);
        if (entity.getStatus() != null) {
            shipmentEmailSender.sendLoadDetailsNotification(load, entity.getStatus().getDescription());
        }

        // If the load is already in Delivered status, don’t update any information on load from the tracking update
        if (load.getStatus() != ShipmentStatus.DELIVERED) {
            return updateLoadFromTrackingData(load, entity, loadStatus);
        }

        return false;
    }

    private boolean updateLoadFromTrackingData(LoadEntity load, LoadTrackingEntity loadTracking, String loadStatus) {
        boolean loadChanged = !StringUtils.equals(loadStatus, load.getStatus().name());
        if (load.getNumbers().getProNumber() == null && StringUtils.isNotEmpty(loadTracking.getPro())) {
            load.getNumbers().setProNumber(loadTracking.getPro());
            loadChanged = true;
        }
        // confirm delivery
        if (StringUtils.equals(loadStatus, ShipmentStatus.DELIVERED.name())) {
            setDepartureTime(loadTracking.getDepartureTime(), load.getDestination());
            load.setStatus(ShipmentStatus.DELIVERED);
        } else if (StringUtils.equals(loadStatus, ShipmentStatus.IN_TRANSIT.name())) {
            // confirm pickup
            if (load.getStatus() != ShipmentStatus.DELIVERED && load.getStatus() != ShipmentStatus.OUT_FOR_DELIVERY) {
                load.setStatus(ShipmentStatus.IN_TRANSIT);
            }
            loadChanged = setDepartureTime(loadTracking.getDepartureTime(), load.getOrigin()) || loadChanged;

        } else if (StringUtils.equals(loadStatus, ShipmentStatus.OUT_FOR_DELIVERY.name()) && load.getStatus() != ShipmentStatus.DELIVERED) {
            // out for delivery
            load.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);
        }
        return loadChanged;
    }

    private boolean setDepartureTime(Date transactionDate, LoadDetailsEntity detail) {
        if (transactionDate != null && detail.getDeparture() == null) {
            detail.setDeparture(transactionDate);
            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void sendMessage(LoadEntity load) throws InternalJmsCommunicationException {

        EdiSettingsEntity ediSettings = load.getBillTo().getEdiSettings();
        if (ediSettings != null && ediSettings.getEdiType() != null && ediSettings.getEdiStatus() != null
                && shouldSendEdi(load.getStatus(), ediSettings.getEdiType(), ediSettings.getEdiStatus())) {
            LoadTrackingJaxbBO loadTrackingBO = new LoadTrackingJaxbBO();
            loadTrackingBO.setLoadId(load.getId());
            loadTrackingBO.setBol(load.getNumbers().getBolNumber());
            loadTrackingBO.setCustomerOrgId(load.getOrganization().getId());
            loadTrackingBO.setPersonId(SecurityUtils.getCurrentPersonId());
            loadTrackingBO.setProNumber(load.getNumbers().getProNumber());
            loadTrackingBO.setRecvDateTime(new Date());
            loadTrackingBO.setMessageType(EDIMessageType.EDI214_STERLING_MESSAGE_TYPE.name());
            loadTrackingBO.setEdiAccountNum(load.getOrganization().getEdiAccount());
            loadTrackingBO.setShipmentNo(load.getNumbers().getRefNumber());

            if (load.getCarrier() != null) {
                loadTrackingBO.setScac(load.getCarrier().getScac());
            }

            loadTrackingBO.setTrackingStatuses(new ArrayList<TrackingStatusJaxbBO>(1));
            TrackingStatusJaxbBO trackingStatus = new TrackingStatusJaxbBO();
            trackingStatus.setLoadStatus(load.getStatus().toString());
            if (load.getStatus().equals(ShipmentStatus.IN_TRANSIT)) {
                trackingStatus.setTransactionDate(load.getOrigin().getDeparture());
            } else if (load.getStatus().equals(ShipmentStatus.DELIVERED)) {
                trackingStatus.setTransactionDate(load.getDestination().getDeparture());
            } else {
                trackingStatus.setTransactionDate(new Date());
            }
            trackingStatus.setTransactionDateTz(TIME_ZONE);
            loadTrackingBO.getTrackingStatuses().add(trackingStatus);

            setAddresses(load, loadTrackingBO);
            createTrackingEntity(load, loadTrackingBO);

            try {
                SterlingIntegrationMessageBO sterlingMessage =
                        new SterlingIntegrationMessageBO(loadTrackingBO, EDIMessageType.EDI214_STERLING_MESSAGE_TYPE);
                sterlingMessageProducer.publishMessage(sterlingMessage);
            } catch (Exception ex) {
                throw new InternalJmsCommunicationException("Exception occurred while publishing 214 message to external integration message queue",
                        ex);
            }
        }
    }

    private void setAddresses(LoadEntity load, LoadTrackingJaxbBO loadTrackingBO) {
        loadTrackingBO.addAddress(setLoadPoint(load.getOrigin(), AddressType.ORIGIN));
        loadTrackingBO.addAddress(setLoadPoint(load.getDestination(), AddressType.DESTINATION));
    }

    private AddressJaxbBO setLoadPoint(LoadDetailsEntity loadDetailsEntity, AddressType addressType) {
        AddressJaxbBO address = new AddressJaxbBO();
        AddressEntity addressEntity = loadDetailsEntity.getAddress();
        address.setAddressType(addressType);
        address.setAddress1(addressEntity.getAddress1());
        address.setAddress2(addressEntity.getAddress2());
        address.setCity(addressEntity.getCity());
        address.setStateCode(addressEntity.getStateCode());
        address.setPostalCode(addressEntity.getZip());
        address.setCountryCode(addressEntity.getCountry().getId());
        return address;
    }

    private boolean shouldSendEdi(ShipmentStatus newStatus, List<EdiType> ediTypes, List<ShipmentStatus> ediStatus) {
        return ediTypes.contains(EdiType.EDI_214) && ediStatus.contains(newStatus);
    }
}
