package com.pls.shipment.service.impl.edi;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToDao;
import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.enums.EdiType;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.EdiSettingsEntity;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.InternalJmsCommunicationException;
import com.pls.core.exception.ShipmentNotFoundException;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.integration.producer.SterlingEDIOutboundJMSMessageProducer;
import com.pls.location.dao.OrganizationLocationDao;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadTrackingEntity;
import com.pls.shipment.domain.sterling.AddlRefNumberJaxbBO;
import com.pls.shipment.domain.sterling.LoadAcknowledgementJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.service.edi.LoadTenderAckService;
import com.pls.shipment.service.impl.email.EDIEmailSender;

/**
 * This Service is provided to create EDI 990 or receive and process it.
 *
 * @author Yasaman Honarvar
 *
 */

@Service("loadTenderAckService")
@Transactional(readOnly = true)
public class LoadTenderAckServiceImpl implements LoadTenderAckService {

    private static final String PICKUP_CONFIRMATION_QUALIFIER = "P8";

    @Autowired
    private CarrierDao carrierDao;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private LoadTrackingDao loadTrackingDao;

    @Autowired
    private EDIEmailSender ediEmailSender;

    @Autowired
    private BillToDao billToDao;

    @Autowired
    private OrganizationLocationDao locationDao;

    @Autowired
    private SterlingEDIOutboundJMSMessageProducer sterlingMessageProducer;


    protected static List<String> sendStatus = Arrays.asList(new String[] { "D", "R", "B", "C", "E", "N", "S", "V" });

    private static final String ACCEPTED_CODE = "A"; //Accepted Reservation

    private static final String REJECTED_CODE = "E"; //Declined Reservation

    /**
     * processes The message whenever a new 990 acknowledgement is received.
     *
     * @param acknowledgement
     *            is the Message object that holds the EDI 990 values.
     * @throws ApplicationException when the shipment isn't found
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void processMessage(LoadAcknowledgementJaxbBO acknowledgement) throws ApplicationException {
        List<LoadEntity> loads = ltlShipmentDao.findShipmentsByScacAndBolNumber(acknowledgement.getScac(), acknowledgement.getBol());
        if (loads.size() == 1) {
            LoadTrackingEntity loadTracking = createLoadTracking(acknowledgement, loads.get(0));
            saveLoadTracking(loadTracking);

            informResults(loadTracking);
        } else {
            String errorMsg = String.format("Saving Shipment Tracking failed. Unable to find unique Shipment by BOL# '%s' for Carrier '%s'. "
                    + "Actually %d shipments were found.", acknowledgement.getBol(), acknowledgement.getScac(), loads.size());
            ediEmailSender.loadTrackingFailed(acknowledgement.getB2biFileName(), errorMsg, EDI_990.toString(), null);
            throw new ShipmentNotFoundException(errorMsg);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void sendMessage(LoadEntity load, LoadMessageJaxbBO loadTender, boolean accept) throws InternalJmsCommunicationException {
        if (shouldSendEdi(load, loadTender.getCustomerBillToId(), loadTender.getCustomerLocationId())) {
            LoadAcknowledgementJaxbBO acknowledgement = createAcknowledgment(load, loadTender, accept);
            if (load != null) {
                LoadTrackingEntity trackingEntity = createLoadTracking(acknowledgement, load);
                saveLoadTracking(trackingEntity);
            }

            try {
                SterlingIntegrationMessageBO sterlingMessage = new SterlingIntegrationMessageBO(acknowledgement,
                        EDIMessageType.EDI990_STERLING_MESSAGE_TYPE);
                sterlingMessageProducer.publishMessage(sterlingMessage);
            } catch (Exception ex) {
                throw new InternalJmsCommunicationException(
                        "Exception occurred while publishing message" + "to external integration message queue", ex);
            }
        }
    }

    /* Sends Email to informs object owner and account Executive the stage of the tracking process */
    private void informResults(LoadTrackingEntity loadTracking) {
        LoadEntity load = loadTracking.getLoad();
        if (load != null && sendStatus.contains(loadTracking.getStatusCode())) {
            if (loadTracking.getLoad().getOrganization().getNetworkId() == LTL_NETWORK) {
                ediEmailSender.forLTLDistributionList(load, loadTracking.getFreeMessage());
            } else {
                // Sends an Email to the Account Executive.
                ediEmailSender.forCreatedUser(load.getLocation().getActiveAccountExecutive().getUser().getEmail(),
                        load, loadTracking.getFreeMessage());

                // Sends an Email to the Creator of the object.
                ediEmailSender.forCreatedUser(load.getModification().getCreatedUser().getEmail(), load, loadTracking.getFreeMessage());
            }
        }
    }

    /* Sets up the values in LoadTracking */
    private LoadTrackingEntity createLoadTracking(LoadAcknowledgementJaxbBO acknowledgement, LoadEntity load) {
        LoadTrackingEntity loadTracking = new LoadTrackingEntity();
        loadTracking.setTrackingDate(acknowledgement.getRecvDateTime());
        loadTracking.setTimezoneCode(TIME_ZONE);
        loadTracking.setSource(EDI_990);
        loadTracking.setScac(acknowledgement.getScac());
        if (acknowledgement.getScac() != null) {
            loadTracking.setCarrier(getCarrier(loadTracking.getScac()));
        }
        loadTracking.setBol(acknowledgement.getBol());
        loadTracking.setPro(acknowledgement.getProNumber());
        loadTracking.setStatusCode(StringUtils.trimToNull(acknowledgement.getStatus()));
        loadTracking.setStatusReasonCode(StringUtils.trimToNull(acknowledgement.getDeclineReasonCd()));

        StringBuilder freeMessage = new StringBuilder();
        freeMessage.append(acknowledgement.getStatus()).append(':').append(acknowledgement.getStatusNotes());
        if (acknowledgement.getDeclineReasonDesc() != null) {
            freeMessage.append(' ').append(acknowledgement.getDeclineReasonDesc());
        }
        loadTracking.setFreeMessage(freeMessage.toString());

        if (loadTracking.getFreeMessage() == null) {
            loadTracking.setFreeMessage("Shipment" + (acknowledgement.getStatus().equals(ACCEPTED_CODE) ? " Accepted" : " Rejected") + " "
                    + "by Carrier");
        }
        if (loadTracking.getFreeMessage() != null && acknowledgement.getAddlRefNumbers() != null) {
            Optional<AddlRefNumberJaxbBO> pickupConfirmation = acknowledgement.getAddlRefNumbers().stream()
                    .filter(p -> PICKUP_CONFIRMATION_QUALIFIER.equals(p.getQualifier())).findAny();
            if (pickupConfirmation.isPresent()) {
                String pickupConfirmationNumber = pickupConfirmation.get().getNumber();
                loadTracking.setPickupConfirmation(pickupConfirmationNumber);
                loadTracking
                        .setFreeMessage(loadTracking.getFreeMessage() + ". Carrier Pickup Confirmation #: " + pickupConfirmationNumber + ".");
            }
        }

        loadTracking.setLoad(load);

        return loadTracking;
    }

    private void saveLoadTracking(LoadTrackingEntity loadTracking) {
        loadTrackingDao.saveOrUpdate(loadTracking);
    }

    private CarrierEntity getCarrier(String scac) {
        return carrierDao.findByScac(scac);
    }

    private LoadAcknowledgementJaxbBO createAcknowledgment(LoadEntity load, LoadMessageJaxbBO loadTender, boolean accept) {
        LoadAcknowledgementJaxbBO acknowledgement = new LoadAcknowledgementJaxbBO();
        acknowledgement.setPersonId(SecurityUtils.getCurrentPersonId());
        acknowledgement.setCustomerOrgId(loadTender.getCustomerOrgId());
        acknowledgement.setRecvDateTime(new Date());
        acknowledgement.setShipmentNo(loadTender.getShipmentNo());
        if (accept) {
            acknowledgement.setStatus(ACCEPTED_CODE);
            acknowledgement.setStatusNotes("EDI 990 created : Accepted");
        } else {
            acknowledgement.setStatus(REJECTED_CODE);
            acknowledgement.setStatusNotes("EDI 990 created : Rejected");
        }
        setLoadNumbers(acknowledgement, load);
        acknowledgement.setMessageType(EDIMessageType.EDI990_STERLING_MESSAGE_TYPE.getCode());

        return acknowledgement;
    }

    private void setLoadNumbers(LoadAcknowledgementJaxbBO acknowledgement, LoadEntity load) {
        acknowledgement.setBol(load != null ? load.getNumbers().getBolNumber() : null);
        acknowledgement.setLoadId(load != null ? load.getId() : null);
        acknowledgement.setProNumber(load != null ? load.getNumbers().getProNumber() : null);
    }

    private boolean shouldSendEdi(LoadEntity load, Long billToId, Long locationId) {
        BillToEntity billTo = getBillTo(load, billToId, locationId);

        if (billTo != null) {
            EdiSettingsEntity ediSetting = billTo.getEdiSettings();
            if (ediSetting != null && ediSetting.getEdiType() != null) {
                return ediSetting.getEdiType().contains(EdiType.EDI_990);
            }
        }
        return false;
    }

    private BillToEntity getBillTo(LoadEntity load, Long billToId, Long locationId) {
        BillToEntity billTo = null;
        if (load != null) {
            billTo = load.getBillTo();
        } else if (billToId != null) {
            billTo = billToDao.find(billToId);
        } else if (locationId != null) {
            OrganizationLocationEntity location = locationDao.find(locationId);
            if (location != null) {
                billTo = location.getBillTo();
            }
        }
        return billTo;
    }
}
