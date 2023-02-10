package com.pls.extint.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.OutboundEdiQueueMapEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.ExternalJmsCommunicationException;
import com.pls.core.exception.XmlSerializationException;
import com.pls.core.integration.AuditBO;
import com.pls.core.service.IntegrationAuditService;
import com.pls.core.service.util.OutboundEdiQueueMappingUtils;
import com.pls.core.service.util.SpringApplicationContext;
import com.pls.core.service.xml.JaxbService;
import com.pls.extint.service.SterlingService;
import com.pls.extint.sterling.producer.SterlingMessageProducer;

/**
 * Implementation of {@link SterlingService}.
 *
 * @author Pavani Challa
 *
 */
@Service
public class SterlingServiceImpl implements SterlingService {
    private static final Logger LOG = LoggerFactory.getLogger(SterlingServiceImpl.class);
    private static final Character OUTBOUND_MESSAGE = 'O';

    private static final Set<EDIMessageType> TENDER_MSG_TYPES =
            EnumSet.of(EDIMessageType.CUSTOMER_INVOICE_MESSAGE_TYPE, EDIMessageType.EDI214_STERLING_MESSAGE_TYPE,
                    EDIMessageType.EDI990_STERLING_MESSAGE_TYPE, EDIMessageType.EDI211_STERLING_MESSAGE_TYPE);
    private static final Set<EDIMessageType> FINANCE_MSG_TYPES = EnumSet.of(EDIMessageType.AR, EDIMessageType.AP, EDIMessageType.CUSTOMER);

    @Autowired
    private JaxbService jaxbService;

    @Autowired
    private SterlingMessageProducer sterlingProducer;

    @Autowired
    private IntegrationAuditService auditService;

    @Autowired
    private OutboundEdiQueueMappingUtils outboundEdiQueueMappingUtils;

    @Override
    public void sendMessage(SterlingIntegrationMessageBO message) throws ApplicationException {
        String xml = convertMessage(message.getPayload());
        AuditEntity audit = createAudit(message.getPayload(), xml, message.getType(), OUTBOUND_MESSAGE);
        publishMessageToExternalQueue(message.getType(), xml, audit, message.getPayload().getCustomerOrgId(), message.getPayload().getScac());
    }

    @Override
    public void resubmit(Long auditId, String xml) throws ApplicationException {
        IntegrationMessageBO integrationMessage = parseMessage(xml);
        AuditBO audit = auditService.getLogById(auditId);
        AuditEntity auditEntity = createAudit(integrationMessage, xml, audit.getMessageType(), audit.getInbOtb());
        publishMessageToExternalQueue(audit.getMessageType(), xml, auditEntity, audit.getShipperOrgId(), audit.getScac());
    }

    private IntegrationMessageBO parseMessage(String xml) throws ApplicationException {
        Preconditions.checkNotNull(xml, "XML message is null.");

        try {
            InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
            return (IntegrationMessageBO) jaxbService.read(stream);
        } catch (XmlSerializationException ex) {
            String errorMsg = String.format("Caught exception while attempting to de-serialize"
                    + "the inbound message from Sterling: %s", ex.getMessage());
            throw new ApplicationException(errorMsg, ex);
        }
    }

    private String convertMessage(IntegrationMessageBO message) throws ApplicationException {
        Preconditions.checkNotNull(message, "Message is null.");

        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            jaxbService.write(message, outputStream);
            return outputStream.toString();
        } catch (XmlSerializationException ex) {
            String errorMsg = String
                    .format("Caught exception while attempting to serialize an instance of IntegrationMessageBO: %s", ex.getMessage());
            throw new ApplicationException(errorMsg, ex);
        }
    }

    private void publishMessageToExternalQueue(EDIMessageType messageType, String xml, AuditEntity audit, Long customerOrgId, String scac)
            throws ExternalJmsCommunicationException {
        LOG.info("Sending {} message to Sterling for carrier='{}', load='{}', customer='{}'",
                ObjectUtils.defaultIfNull(messageType.getEdiTransaction(), messageType.getCode()), scac, audit.getLoadId(), customerOrgId);
        if (messageType.equals(EDIMessageType.EDI204_STERLING_MESSAGE_TYPE)) {
            OutboundEdiQueueMapEntity outboundEdiQueueMapEntityByScac = outboundEdiQueueMappingUtils.getOutboundEdiQueueMapEntityByScac(scac);
            sterlingProducer.publishEdiMessage(xml, outboundEdiQueueMapEntityByScac.getQueueName(), outboundEdiQueueMapEntityByScac.getPriority());
        } else if (TENDER_MSG_TYPES.contains(messageType)) {
            OutboundEdiQueueMapEntity outboundEdiQueueMapEntityById = outboundEdiQueueMappingUtils.getOutboundEdiQueueMapEntityById(customerOrgId);
            sterlingProducer.publishEdiMessage(xml, outboundEdiQueueMapEntityById.getQueueName(), outboundEdiQueueMapEntityById.getPriority());
        } else if (FINANCE_MSG_TYPES.contains(messageType)) {
            sterlingProducer.publishFinanceMessage(xml);
        }
        audit.setStatus("C");
        auditService.saveLog(audit);
    }

    private AuditEntity createAudit(IntegrationMessageBO message, String xml, EDIMessageType messageType, Character inbOtb) {
        AuditEntity audit = new AuditEntity();
        audit.setInbOtb(inbOtb);
        audit.setMessageType(messageType);
        audit.setStatus("P");

        if (message != null) {
            audit.getModification().setCreatedBy(message.getPersonId() != null ? message.getPersonId() : SpringApplicationContext.getAdminUserId());
            audit.getModification().setModifiedBy(message.getPersonId() != null ? message.getPersonId() : SpringApplicationContext.getAdminUserId());
            audit.setLoadID(message.getLoadId());
            audit.setBol(message.getBol());
            audit.setScac(message.getScac());
            audit.setShipmentNum(message.getShipmentNo());
            audit.setShipperOrgId(message.getCustomerOrgId());
        }

        AuditDetailEntity detail = new AuditDetailEntity();
        detail.setMessage(xml);
        detail.setAudit(audit);
        audit.setAuditDetail(detail);

        return audit;
    }
}
