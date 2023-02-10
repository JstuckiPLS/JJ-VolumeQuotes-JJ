package com.pls.shipment.service.impl.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pls.core.dao.CarrierDao;
import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.ShipmentDirection;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.email.EmailTemplateRenderer;
import com.pls.email.producer.EmailMessageProducer;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.sterling.AddressJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.enums.AddressType;
import com.pls.shipment.service.ShipmentUtils;

import freemarker.template.TemplateException;

/**
 * Class for sending emails related to EDI messages functionality.
 *
 * @author Mikhail Boldinov, 11/04/14
 */
@Service
@Transactional
public class EDIEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EDIEmailSender.class);

    private static final String LOAD_TENDER_FAILED_EMAIL_SUBJECT = "Unable to send EDI 204 to carrier.";
    private static final String LOAD_TRACKING_FAILED_EMAIL_SUBJECT = "Unable to process EDI 214.";

    @Value("${email.failNotification.recipients}")
    private String recipients;

    @Value("${email.ltlDistributionlist}")
    private String ltlRecipients;

    @Value("${pls.client.index.url}")
    private String clientUrl;

    @Autowired
    private EmailMessageProducer emailMessageProducer;

    @Autowired
    private EmailTemplateRenderer emailTemplateRenderer;

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    private CarrierDao carrierDao;

    /**
     * Sends email if load tender via EDI failed.
     *
     * @param carrierId id of carrier which is EDI recipient
     * @param loadIds   list of tendered load id
     */
    public void loadTenderFailed(Long carrierId, List<Long> loadIds) {
        try {
            LoadEntity load = getLoad(loadIds);
            if (load == null) {
                LOGGER.warn(String.format("No loads exist with specified ID: %s", loadIds));
                return;
            }
            List<String> recipients = Arrays.asList(this.recipients.split(";"));
            String content = getTemplateForLoadTenderFailedEmail(carrierId, loadIds, load);
            emailMessageProducer.sendEmail(recipients, LOAD_TENDER_FAILED_EMAIL_SUBJECT, content, EmailType.EDI, null, null, loadIds);
        } catch (IOException e) {
            LOGGER.error(String.format("Cannot send EDI 204 notification email. Load ID:%s, Carrier ID:%s. ", loadIds, carrierId), e);
        } catch (TemplateException e) {
            LOGGER.error(String.format("Cannot send EDI 204 notification email. Load ID:%s, Carrier ID:%s. ", loadIds, carrierId), e);
        }
    }

    private String getTemplateForLoadTenderFailedEmail(Long carrierId, List<Long> loadIds, LoadEntity load) throws IOException,
            TemplateException {
        CarrierEntity carrier = getCarrier(carrierId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("load", load == null ? loadIds.get(0) : load);
        params.put("carrier", carrier == null ? carrierId : carrier);
        params.put("clientUrl", clientUrl);
        params.put("isBlindBol", load == null ? false : ShipmentUtils.isBlindBol(load));

        return emailTemplateRenderer.renderEmailTemplate("sendingEdi204FailedTemplate.ftl", params);
    }

    /**
     * Sends email if receiving load tracking information via EDI failed.
     *
     * @param ediFileName EDI file name
     * @param errorMsg    error message
     * @param ediNumber   number of the EDI
     * @param loadIds load ids
     */
    public void loadTrackingFailed(String ediFileName, String errorMsg, String ediNumber, Collection<Long> loadIds) {
        try {
            List<String> recipients = Arrays.asList(this.recipients.split(";"));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ediFileName", ediFileName);
            params.put("errorMsg", errorMsg);
            params.put("clientUrl", clientUrl);
            String content = emailTemplateRenderer.renderEmailTemplate("loadTrackingFailedTemplate.ftl", params);
            emailMessageProducer.sendEmail(recipients, LOAD_TRACKING_FAILED_EMAIL_SUBJECT, content, EmailType.EDI, null, null,
                    loadIds);
        } catch (IOException e) {
            LOGGER.error(String.format("Cannot send EDI %s notification email.", ediNumber), e);
        } catch (TemplateException e) {
            LOGGER.error(String.format("Cannot send EDI %s notification email.", ediNumber), e);
        }
    }
    
    /**
     * Sends email if receiving load tracking information via LTL-Lifecycle failed.
     *
     * @param errorMsg    error message
     * @param loadIds load ids
     */
    public void loadTrackingLTLLCFailed(String errorMsg, Collection<Long> loadIds) {
        try {
            List<String> recipients = Arrays.asList(this.recipients.split(";"));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("errorMsg", errorMsg);
            params.put("clientUrl", clientUrl);
            String content = emailTemplateRenderer.renderEmailTemplate("loadTrackingLTLLCFailedTemplate.ftl", params);
            emailMessageProducer.sendEmail(recipients, "Unable to process LTL-Lifecycle tracking update.", content, EmailType.EDI, null, null,
                    loadIds);
        } catch (IOException e) {
            LOGGER.error(String.format("Cannot send LTLLC notification email."), e);
        } catch (TemplateException e) {
            LOGGER.error(String.format("Cannot send LTLLC notification email."), e);
        }
    }

    /**
     * Send email if receiving Vendor Bill via EDI failed.
     *
     * @param ediFileName
     *            EDI file name
     */
    public void vendorBillFailed(String ediFileName) {
        try {
            List<String> recipients = Arrays.asList(this.recipients.split(";"));
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ediFileName", ediFileName);
            params.put("clientUrl", clientUrl);
            String content = emailTemplateRenderer.renderEmailTemplate("vendorBillFailedTemplate.ftl", params);
            emailMessageProducer.sendEmail(recipients, LOAD_TRACKING_FAILED_EMAIL_SUBJECT, content, EmailType.EDI, null, null,
                    Collections.<Long>emptyList());
        } catch (IOException e) {
            LOGGER.error("Cannot send EDI 210 notification email.", e);
        } catch (TemplateException e) {
            LOGGER.error("Cannot send EDI 210 notification email.", e);
        }
    }

    private LoadEntity getLoad(List<Long> loadIds) {
        LoadEntity loadEntity = null;
        if (!loadIds.isEmpty() && loadIds.get(0) != null) {
            loadEntity = ltlShipmentDao.find(loadIds.get(0));
        }
        return loadEntity;
    }

    private CarrierEntity getCarrier(Long carrierId) {
        CarrierEntity carrierEntity = null;
        if (carrierId != null) {
            carrierEntity = carrierDao.find(carrierId);
        }
        return carrierEntity;
    }

    /**
     * Sends email for LTL distribution list.
     *
     * @param loadEntity
     *            LoadEntity
     * @param trackingStatus
     *            Load Tracking Status type
     *
     */
    public void forLTLDistributionList(LoadEntity loadEntity, String trackingStatus) {
        List<String> recipients = Arrays.asList(this.ltlRecipients.split(";"));
        sendTrackingInfo(loadEntity, trackingStatus, recipients);
    }

    /**
     * Sends email for created user.
     *
     * @param email
     *            email address
     * @param loadEntity
     *            LoadEntity
     * @param trackingStatus
     *            Load Tracking Status type
     *
     */
    public void forCreatedUser(String email, LoadEntity loadEntity, String trackingStatus) {
        List<String> recipients = new ArrayList<String>();
        recipients.add(email);
        sendTrackingInfo(loadEntity, trackingStatus, recipients);
    }

    private void sendTrackingInfo(LoadEntity loadEntity, String trackingStatus,
            List<String> recipients) {
        try {
            String subject = String.format("Notification of Carrier Response to EDI 204, PLS PRO BOL %s, %s ", loadEntity.getNumbers()
                    .getBolNumber(), trackingStatus);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("load", loadEntity);
            params.put("clientUrl", clientUrl);
            params.put("status", trackingStatus);
            params.put("isBlindBol", ShipmentUtils.isBlindBol(loadEntity));

            String content = emailTemplateRenderer.renderEmailTemplate("loadTracking990Template.ftl", params);
            Long loadId = loadEntity == null ? null : loadEntity.getId();
            emailMessageProducer.sendEmail(recipients, subject, content, EmailType.EDI, null, null, Collections.singletonList(loadId));
        } catch (IOException e) {
            LOGGER.error(
                    String.format("Cannot send EDI 990 notification email for %s.",
                            StringUtils.collectionToCommaDelimitedString(recipients)), e);
        } catch (TemplateException e) {
            LOGGER.error(
                    String.format("Cannot send EDI 990 notification email for %s.",
                            StringUtils.collectionToCommaDelimitedString(recipients)), e);
        }
    }

    /**
     * Sends email when an update is received for a load which it's status is in transit, delivered...
     *
     * @param load
     *            the related load we are making the update for.
     * @param loadTender
     *            EDI request for updating the kiad
     * @param recipient
     *            recipient to whom the email has to be sent.
     * @param reopened
     *            boolean flag to indicate if the load is reopened (moved from CANCELLED status to OPEN status)
     * @param materialsUpdated
     *            boolean flag indicating whether materials has changed or not
     *
     */
    public void ediUpdateNotProcessed(LoadEntity load, LoadMessageJaxbBO loadTender, String recipient, boolean reopened, boolean materialsUpdated) {
        List<String> recipients = Collections.singletonList(recipient);
        String subject = String.format("PLS PRO BOL %s : EDI update is not accepted", load.getNumbers().getBolNumber());
        try {
            if (reopened) {
                subject = String.format("PLS PRO BOL %s : EDI update is received to reopen a cancelled shipment", load.getNumbers().getBolNumber());
            }
            Map<String, Object> params = new HashMap<String, Object>();
            Date pickupDate = null;
            Date deliveryDate = null;

            if (loadTender.getAddress(AddressType.ORIGIN).getTransitDate() != null) {
                pickupDate = loadTender.getAddress(AddressType.ORIGIN).getTransitDate().getFromDate();
            }

            if (loadTender.getAddress(AddressType.DESTINATION).getTransitDate() != null) {
                deliveryDate = loadTender.getAddress(AddressType.DESTINATION).getTransitDate().getFromDate();
            }

            params.put("clientUrl", clientUrl);
            params.put("puDate", pickupDate);
            params.put("delDate", deliveryDate);
            params.put("load", load);
            params.put("loadTender", loadTender);
            params.put("reopened", reopened);
            params.put("materialsUpdated", materialsUpdated);
            params.put("directionChanged", load.getShipmentDirection() != ShipmentDirection.getByCode(loadTender.getInboundOutbound()));
            params.put("pickupDateChanged", isPickupDateModified(loadTender, load));
            params.put("originZipChanged", zipChanged(load.getOrigin(), loadTender.getAddress(AddressType.ORIGIN)));
            params.put("destZipChanged", zipChanged(load.getDestination(), loadTender.getAddress(AddressType.DESTINATION)));


            String content = emailTemplateRenderer.renderEmailTemplate("ediLoadUpdateFailed.ftl", params);
            Long loadId = load == null ? null : load.getId();
            emailMessageProducer.sendEmail(recipients, subject, content, EmailType.EDI, null, null, Collections.singletonList(loadId));
        } catch (IOException e) {
            LOGGER.error(String.format("Cannot send EDI 204 update not processed notification email to %s.", recipient), e);
        } catch (TemplateException e) {
            LOGGER.error(String.format("Cannot send EDI 204 update not processed notification email to %s.", recipient), e);
        }
    }

    private boolean zipChanged(LoadDetailsEntity loadDetail, AddressJaxbBO address) {
        boolean addressChanged = false;
        if (!loadDetail.getAddressCode().equals(address.getAddressCode())) {
            addressChanged = addressChanged
                    || !loadDetail.getAddress().getZip().equals(address.getPostalCode());
        }
        return addressChanged;
    }

    private boolean isPickupDateModified(LoadMessageJaxbBO loadTender, LoadEntity load) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        Date pickupDate = loadTender.getAddress(AddressType.ORIGIN).getTransitDate().getFromDate();
        calendar.setTime(load.getOrigin().getEarlyScheduledArrival());

        Calendar puCalendar = Calendar.getInstance(Locale.US);
        puCalendar.setTime(pickupDate);

        return calendar.get(Calendar.DAY_OF_MONTH) != puCalendar.get(Calendar.DAY_OF_MONTH)
                || calendar.get(Calendar.MONTH) != puCalendar.get(Calendar.MONTH) || calendar.get(Calendar.YEAR) != puCalendar.get(Calendar.YEAR);
    }
}
