package com.pls.shipment.service.impl.email;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.domain.enums.EmailType;
import com.pls.core.domain.enums.NotificationTypeEnum;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.user.UserAdditionalContactInfoBO;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.ContactInfoService;
import com.pls.core.service.DictionaryTypesService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.email.EmailTemplateRenderer;
import com.pls.email.dto.EmailAttachmentDTO;
import com.pls.email.producer.EmailInfo;
import com.pls.email.producer.EmailMessageProducer;
import com.pls.location.service.OrganizationLocationService;
import com.pls.ltlrating.service.TerminalService;
import com.pls.ltlrating.shared.GetTerminalsCO;
import com.pls.ltlrating.shared.TerminalsVO;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ManualBolDao;
import com.pls.shipment.domain.LdBillAuditReasonCodeEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadNotificationsEntity;
import com.pls.shipment.domain.ManualBolEntity;
import com.pls.shipment.domain.sterling.enums.OperationType;
import com.pls.shipment.service.ShipmentUtils;

import freemarker.template.TemplateException;

/**
 * Class for sending emails related to shipment functionality.
 * 
 * @author Stas Norochevskiy
 */
@Component
@Transactional
public class ShipmentEmailSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShipmentEmailSender.class);

    private static final String PICKUP = "PICK_UP";

    @Autowired
    private EmailMessageProducer emailMessageProducer;
    @Autowired
    private EmailTemplateRenderer emailTemplateRenderer;
    @Autowired
    private DictionaryTypesService dictionaryTypesService;

    @Autowired
    private LtlShipmentDao loadDao;

    @Autowired
    private ContactInfoService contactInfoService;
    
    @Autowired 
    TerminalService terminalService;

    @Autowired
    private ManualBolDao manualBolDao;

    @Autowired
    private OrganizationLocationService locationService;

    private Long adminPersonId;

    private String clientIndexUrl;

    private String serverUrl;

    @Value("${email.from.invoiceSender}")
    private String invoiceSender;

    @Value("${email.from.billingAuditSender}")
    private String billingAuditSender;

    @Value("${pls.client.app.context.url}")
    private String contextUrl;

    @Value("${email.financeBCC}")
    private String financeBCC;

    /**
     * Send Load document with specified ID via email.
     * 
     * @param recipients
     *            list of recipients
     * @param subject
     *            email subject
     * @param content
     *            email content
     * @param documents
     *            IDs of documents to be sent
     * @param loadId
     *            id of load
     * @param emailTypeString
     *            type of email, document by default
     * @param notificationTypeString
     *            notificationType, null by default
     */
    public void sendDocumentsEmail(String recipients, String subject, String content, List<EmailAttachmentDTO> documents, Long loadId,
            String emailTypeString, String notificationTypeString) {
        EmailType emailType = EmailType.DOCUMENT;
        if (emailTypeString != null) {
            emailType = EmailType.valueOf(emailTypeString);
        }
        NotificationTypeEnum notificationType = null;
        if (notificationTypeString != null) {
            notificationType = NotificationTypeEnum.valueOf(notificationTypeString);
        }
        List<String> recipientsList = Arrays.asList(recipients.split(";"));
        UserAdditionalContactInfoBO contact = contactInfoService.getContactInfoForCurrentUser();
        EmailInfo emailInfo = new EmailInfo(contact.getEmail(), recipientsList, subject, content, documents, emailType, notificationType,
                SecurityUtils.getCurrentPersonId(), Collections.singletonList(loadId));
        emailMessageProducer.sendEmail(emailInfo);
    }

    /**
     * Should be sent on change status to Dispatched, Picked Up, Out For Delivery, Delivered, Details.
     *
     * @param loadEntity
     *            load
     * @param newStatus
     *            new status
     */
    public void sendLoadStatusChangedNotification(LoadEntity loadEntity, ShipmentStatus newStatus) {
        if (!shouldSendNotification(newStatus)) {
            return;
        }
        String notificationName = newStatus == ShipmentStatus.IN_TRANSIT ? PICKUP : newStatus.name();
        NotificationTypeEntity notificationType = dictionaryTypesService.getNotificationTypesById(notificationName);
        String description = " is " + notificationType.getDescription();

        String from = null;
        if (!adminPersonId.equals(SecurityUtils.getCurrentPersonId()) && SecurityUtils.getCurrentPersonId() != null) {
            UserAdditionalContactInfoBO contact = contactInfoService.getContactInfoForCurrentUser();
            from = contact.getEmail();
        }

        NotificationTypeEnum notificationTypeEnum = getNotificationTypeByStatus(newStatus);
        sendLoadNotification(from, loadEntity, notificationName, description, newStatus.getDescription(), notificationTypeEnum);
    }

    private NotificationTypeEnum getNotificationTypeByStatus(ShipmentStatus newStatus) {
        switch (newStatus) {
            case IN_TRANSIT:
                return NotificationTypeEnum.PICK_UP;
            case DISPATCHED:
                return NotificationTypeEnum.DISPATCHED;
            case DELIVERED:
                return NotificationTypeEnum.DELIVERED;
            case OUT_FOR_DELIVERY:
                return NotificationTypeEnum.OUT_FOR_DELIVERY;
            default:
                return NotificationTypeEnum.DETAILS;
        }
    }

    /**
     * Send Load details notification.
     *
     * @param loadEntity
     *            load
     * @param details
     *            details message
     */
    public void sendLoadDetailsNotification(LoadEntity loadEntity, String details) {
        sendLoadNotification(null, loadEntity, "DETAILS", " has Tracking Updates ", details, NotificationTypeEnum.DETAILS);
    }

    private void sendLoadNotification(String from, LoadEntity loadEntity, String notificationName, String subjectDetails, String notificationDetails,
            NotificationTypeEnum notificationType) {
        Set<String> emails = getEmailsForNotification(loadEntity, notificationName);
        // Return if there are no user that should be notified for this load status.
        if (emails.isEmpty()) {
            return;
        }

        try {
            String subject = "PLS PRO Order for BOL " + loadEntity.getNumbers().getBolNumber() + subjectDetails;
            String payload = getPayloadForLoadNotification(loadEntity, notificationDetails);
            EmailInfo emailInfo = new EmailInfo(from, new ArrayList<String>(emails), subject, payload, null, EmailType.NOTIFICATION, notificationType,
                    SecurityUtils.getCurrentPersonId() == null ? adminPersonId : SecurityUtils.getCurrentPersonId(),
                    Collections.singletonList(loadEntity.getId()));
            emailMessageProducer.sendEmail(emailInfo);
        } catch (IOException e) {
            LOGGER.error("Can't send notification email. Load ID: " + loadEntity.getId(), e);
        } catch (TemplateException e) {
            LOGGER.error("Can't send notification email. Load ID: " + loadEntity.getId(), e);
        }
    }

    /**
     * Send Load changes to non EDI Carrier via email.
     * 
     * @param load
     *            {@link LoadEntity}
     * @param carrierToSend
     *            carrier to send email
     * @param operationType
     *            {@link OperationType}
     */
    public void sendCarrierNotification(LoadEntity load, CarrierEntity carrierToSend, OperationType operationType) {
        String subject;
        if (operationType == OperationType.CANCEL) {
            subject = "PLS PRO Order for BOL: " + load.getNumbers().getBolNumber() + " is CANCELLED";
        } else {
            subject = "PLS PRO document email for BOL: " + load.getNumbers().getBolNumber();
        }

        String content = getCarrierNotificationEmail(load, operationType == OperationType.CANCEL ? carrierToSend : load.getCarrier(), operationType);

        List<String> recipients = Arrays.asList(carrierToSend.getOrgServiceEntity().getManualTypeEmail().split(";"));
        EmailInfo emailInfo = new EmailInfo(null, recipients, subject, content, null, EmailType.NOT_AUDITABLE, null,
                SecurityUtils.getCurrentPersonId(), Collections.singletonList(load.getId()));

        emailMessageProducer.sendEmail(emailInfo);
    }

    /**
     * Get content of email for sending document.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @param docName
     *            name of document to be sent
     * @return email content or empty string
     */
    public String getPayloadForSendDocument(Long loadId, String docName) {
        try {
            return getPayloadForSendDocument(loadDao.get(loadId), docName);
        } catch (Exception e) {
            LOGGER.error("Can't get payload for send document. Load ID: " + loadId, e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Get content of email for sending document.
     * 
     * @param load
     *            {@link LoadEntity}
     * @param docName
     *            name of document to be sent
     * @return email content or empty string
     */
    public String getPayloadForSendDocument(LoadEntity load, String docName) {
        UserAdditionalContactInfoBO contact = contactInfoService.getContactInfoForCurrentUser();
        String verb = docName.indexOf(',') != -1 ? "are" : "is";
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("load", load);
            params.put("clientUrl", clientIndexUrl);
            params.put("contact", contact);
            params.put("isBlindBol", ShipmentUtils.isBlindBol(load));
            params.put("docName", docName);
            params.put("verb", verb);
            return emailTemplateRenderer.renderEmailTemplate("loadDocumentTemplate.ftl", params);
        } catch (Exception e) {
            LOGGER.error("Can't get payload for send document.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Get content of email for sending document.
     * 
     * @param manualBolId
     *            {@link ManualBolEntity#getId()}
     * @param docName
     *            name of document to be sent
     * @return email content or empty string
     */
    public String getManualBolPayLoadForSendDocument(Long manualBolId, String docName) {
        try {
            ManualBolEntity manualBol = manualBolDao.get(manualBolId);
            Long customerId = manualBol.getOrganization().getId();
            boolean logoExists = manualBol.getOrganization().getLogoId() != null;
            String verb = docName.indexOf(',') != -1 ? "are" : "is";
            UserAdditionalContactInfoBO contact = contactInfoService.getContactInfoForCurrentUser();

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("load", manualBol);
            params.put("clientUrl", clientIndexUrl);
            params.put("contact", contact);
            params.put("docName", docName);
            params.put("isManualBol", Boolean.TRUE);
            params.put("verb", verb);
            if (logoExists) {
                String logoPath = String.format("%s/restful/customerdocs/%d/logo", serverUrl, customerId);
                params.put("logoPath", logoPath);
            }
            return emailTemplateRenderer.renderEmailTemplate("loadDocumentTemplate.ftl", params);
        } catch (EntityNotFoundException e) {
            LOGGER.error("Can't get payload for send document. Load ID: " + manualBolId, e);
            return StringUtils.EMPTY;
        } catch (Exception e) {
            LOGGER.error("Can't get payload for send document.", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Get content of email for sending document.
     * 
     * @param loadId
     *            {@link LoadEntity#getId()}
     * @param templateName
     *            name of the template to load
     * @return email content or empty string
     */
    public String getPayloadForEmail(Long loadId, String templateName) {
        try {
            LoadEntity load = loadDao.get(loadId);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("load", load);
            params.put("clientUrl", clientIndexUrl);
            params.put("isBlindBol", ShipmentUtils.isBlindBol(load));
            return emailTemplateRenderer.renderEmailTemplate(templateName, params);
        } catch (Exception e) {
            LOGGER.error("Can't get payload for sending email. Load ID: " + loadId, e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Get content for pls pay email.
     * 
     * @param locationId
     *            location Id
     * @return email content or empty string
     */
    public String getPLSPayContent(Long locationId) {
        UserAdditionalContactInfoBO userInfo = locationService.getAccountExecutiveByLocationId(locationId);
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("contact", userInfo);

            return emailTemplateRenderer.renderEmailTemplate("PLSPayRequest.ftl", params);
        } catch (Exception e) {
            LOGGER.error("Can't get template. " + e.getMessage(), e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Send status update email for Go Ship load.
     * 
     * @param load
     *            Go Ship load
     * @param oldStatus
     *            previous load status
     */
    public void sendGoShipTrackingUpdateEmail(LoadEntity load, ShipmentStatus oldStatus) {
        if (isGoShipNotification(load, oldStatus)) {
            try {            	
            	TerminalsVO terminals = getTerminals(load);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("load", load);
                params.put("terminals", terminals);
                String status = load.getStatus() == ShipmentStatus.DELIVERED ? "Delivered" : "Picked up";
                String subject = status + " - GoShip.com Shipment " + load.getId() + ": Your shipment has been " + status;
                params.put("subject", subject);
                String content = emailTemplateRenderer.renderEmailTemplate("GoShipTrackingUpdate.ftl", params);
                EmailInfo emailInfo = new EmailInfo(null, Arrays.asList(load.getOrganization().getContactEmail().split(";")), subject, content,
                        null, EmailType.NOTIFICATION, null, null, Collections.singletonList(load.getId()));

                emailMessageProducer.sendEmail(emailInfo);
            } catch (Exception e) {
                LOGGER.error("Error sending tracking update for go ship load. " + e.getMessage(), e);
            }
        }
    }

    private boolean isGoShipNotification(LoadEntity load, ShipmentStatus oldStatus) {
        return "GS".equals(load.getOriginatingSystem()) && (load.getStatus() == ShipmentStatus.DELIVERED && load.getStatus() != oldStatus
                || ((load.getStatus() == ShipmentStatus.IN_TRANSIT || load.getStatus() == ShipmentStatus.OUT_FOR_DELIVERY)
                        && oldStatus == ShipmentStatus.DISPATCHED));
    }

    private String getCarrierNotificationEmail(LoadEntity load, CarrierEntity carrier, OperationType operationType) {
        UserAdditionalContactInfoBO contactInfo = contactInfoService.getContactInfo(load.getModification().getCreatedUser());
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("load", load);
            params.put("clientUrl", clientIndexUrl);
            params.put("isBlindBol", ShipmentUtils.isBlindBol(load));
            params.put("contact", contactInfo);
            params.put("carrier", carrier);
            params.put("estimatedTransitDay", ShipmentUtils.getEstimatedTransitDaysLabel(load.getTravelTime()));
            params.put("isCanceled", operationType == OperationType.CANCEL);
            params.put("pickupNotes",
                    ShipmentUtils.getAggregatedNotes(load.getLtlAccessorials(), true, load.getOrigin().getNotes(), load.getSpecialInstructions()));
            params.put("deliveryNotes",
                    ShipmentUtils.getAggregatedNotes(load.getLtlAccessorials(), false, load.getDestination().getNotes(), load.getDeliveryNotes()));
            params.put("showCustomsBroker", ShipmentUtils.showCustomsBroker(load));
            return emailTemplateRenderer.renderEmailTemplate("BolEmailTemplate.ftl", params);
        } catch (Exception e) {
            LOGGER.error("Can't get payload for sending email. Load ID: " + load.getId(), e);
            return StringUtils.EMPTY;
        }
    }

    private String getPayloadForLoadNotification(LoadEntity loadEntity, String description) throws IOException, TemplateException {
        UserAdditionalContactInfoBO contact = contactInfoService.getContactInfoForCurrentUser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("load", loadEntity);
        params.put("notificationDescription", description);
        params.put("clientUrl", clientIndexUrl);
        params.put("contact", contact);
        params.put("isBlindBol", ShipmentUtils.isBlindBol(loadEntity));
        return emailTemplateRenderer.renderEmailTemplate("loadStatusChangedTemplate.ftl", params);
    }

    private Set<String> getEmailsForNotification(LoadEntity load, String notificationName) {
        return load.getLoadNotifications().stream().filter((notification) -> notificationName.equals(notification.getNotificationType().getId()))
                .map(LoadNotificationsEntity::getEmailAddress).collect(Collectors.toSet());
    }

    private boolean shouldSendNotification(ShipmentStatus newStatus) {
        // Dispatched, Picked Up, Out For Delivery, Delivered, Details
        return newStatus == ShipmentStatus.DISPATCHED || newStatus == ShipmentStatus.OUT_FOR_DELIVERY || newStatus == ShipmentStatus.DELIVERED
                || newStatus == ShipmentStatus.IN_TRANSIT;
    }

    private NumberFormat getCurrencyFormat() {
        NumberFormat currencyNumberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        if (currencyNumberFormat instanceof DecimalFormat) {
            DecimalFormat decimalFormat = (DecimalFormat) currencyNumberFormat;
            decimalFormat.setNegativePrefix("-$");
            decimalFormat.setNegativeSuffix("");
        }
        return currencyNumberFormat;
    }

    /**
     * Send invoice to customer.
     * 
     * @param recipients
     *            emails separated by semicolon
     * @param subject
     *            subject text
     * @param emailContent
     *            email content
     * @param invoiceDocuments
     *            invoice documents to be sent to customer via email
     * @param notSplitRecipients
     *            if <code>false</code>, then each recipient will get its own copy of email, otherwise one
     *            email for all recipients will be sent
     * @param loadIds
     *            - load ids
     * @param personId
     *            - id of current logged user
     */
    public void sendInvoice(String recipients, String subject, String emailContent, List<EmailAttachmentDTO> invoiceDocuments,
            boolean notSplitRecipients, Collection<Long> loadIds, Long personId) {
        EmailInfo emailInfo = new EmailInfo(invoiceSender, Arrays.asList(recipients.split(";")), subject, emailContent, invoiceDocuments,
                EmailType.INVOICE, null, personId, loadIds);
        emailInfo.setBCC(financeBCC);
        emailMessageProducer.sendEmail(emailInfo, notSplitRecipients);
    }

    @Value("${pls.client.index.url}")
    public void setClientUrl(String clientIndexUrl) {
        this.clientIndexUrl = clientIndexUrl;
    }

    @Value("${admin.personId}")
    public void setAdminPersonId(Long adminPersonId) {
        this.adminPersonId = adminPersonId;
    }

    @Value("${pls.client.url}")
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Send email notification to the account executive.
     * 
     * @param load
     *            {@link LoadEntity}
     * @param reasons
     *            for audit of {@link LdBillAuditReasonCodeEntity}
     */
    public void sendLoadReasonsEmail(LoadEntity load, List<LdBillAuditReasonCodeEntity> reasons) {
        try {
            NumberFormat currencyNumberFormat = getCurrencyFormat();

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("load", load);
            params.put("contextUrl", contextUrl);
            params.put("reasons", reasons);
            params.put("clientUrl", clientIndexUrl);

            BigDecimal totalCost = load.getVendorBillDetails().getFrtBillAmount();
            BigDecimal totalRevenue = load.getActiveCostDetail().getTotalRevenue();
            params.put("originalTotalRevenue", currencyNumberFormat.format(totalRevenue));
            params.put("originalMarginAmt", currencyNumberFormat.format(load.getActiveCostDetail().getMarginAmt()));

            BigDecimal newMarginAmt = totalRevenue.subtract(totalCost);
            BigDecimal newMargin = BigDecimal.ZERO;
            if (!totalRevenue.equals(BigDecimal.ZERO)) {
                newMargin = totalRevenue.subtract(totalCost).multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 2, RoundingMode.HALF_UP);
            }

            params.put("originalTotalCost", currencyNumberFormat.format(load.getActiveCostDetail().getTotalCost()));
            params.put("newTotalCost", currencyNumberFormat.format(totalCost));
            params.put("newMargin", newMargin);
            params.put("newMarginAmt", newMarginAmt);
            params.put("newCurrencyMarginAmt", currencyNumberFormat.format(newMarginAmt));

            String payload = emailTemplateRenderer.renderEmailTemplate("invoiceAuditNotificationEmail.ftl", params);
            String subject = String.format("SHIPMENT NOT BILLED:  Invoice Audit Notification for Load ID %s", load.getId());
            UserEntity user = load.getLocation().getActiveAccountExecutive().getUser();
            String accountExecutiveEmail = user == null ? load.getLocation().getLastActiveAccountExecutive().getUser().getEmail() : user.getEmail();
            EmailInfo emailInfo = new EmailInfo(billingAuditSender, Arrays.asList(accountExecutiveEmail), subject, payload, null,
                    EmailType.NOTIFICATION, NotificationTypeEnum.AUDIT,
                    SecurityUtils.getCurrentPersonId() == null ? adminPersonId : SecurityUtils.getCurrentPersonId(),
                    Collections.singletonList(load.getId()));
            emailMessageProducer.sendEmail(emailInfo);
        } catch (Exception e) {
            LOGGER.error("Problem in sending email when load reasons are changed.", e);
        }
    }
    
    private TerminalsVO getTerminals(LoadEntity load) {
    	GetTerminalsCO criteria = new GetTerminalsCO();
    	try {
    		LoadDetailsEntity origin = load.getOrigin();
            if (origin != null) {
                criteria.setOriginAddress(terminalService.buildAddressVO(origin.getAddress()));
            }
            LoadDetailsEntity destination = load.getDestination();
            if (destination != null) {
                criteria.setDestinationAddress(terminalService.buildAddressVO(destination.getAddress()));
            }
            criteria.setScac(load.getCarrier().getActualScac());
            TerminalsVO terminals = terminalService.getTerminalInformation(criteria);
            return terminals;
    	}
    	catch (Exception e) {
    		LOGGER.error("Problem retrieving terminal information for load id: " + load.getId(), e);
    		return null;
    	}
    }
}
