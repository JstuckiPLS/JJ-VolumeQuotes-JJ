package com.pls.shipment.service.impl.edi.sterling.jms.consumer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.XmlSerializationException;
import com.pls.core.service.IntegrationAuditService;
import com.pls.core.service.impl.security.PlsUserDetails;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.xml.JaxbService;
import com.pls.shipment.domain.sterling.AcknowledgementJaxbBO;
import com.pls.shipment.domain.sterling.LoadAcknowledgementJaxbBO;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.LoadTrackingJaxbBO;
import com.pls.shipment.domain.sterling.LoadUpdateJaxbBO;
import com.pls.shipment.service.impl.LoadUpdateIntegrationServiceImpl;
import com.pls.shipment.service.impl.edi.AcknowledgementServiceImpl;
import com.pls.shipment.service.impl.edi.CarrierInvoiceIntegrationServiceImpl;
import com.pls.shipment.service.impl.edi.CustomerLoadTenderServiceImpl;
import com.pls.shipment.service.impl.edi.LoadTenderAckServiceImpl;
import com.pls.shipment.service.impl.edi.LoadTrackingServiceImpl;

/**
 * Listens for SterlingIntegrationMessageBO objects on the shipment integration
 * message queue and then delegates the message to service for processing the
 * message based on message type.
 *
 * @author Pavani Challa
 */
@Component("shipmentIntegrationMessageListener")
@Profile("JMSServer")
@Transactional
public class SterlingEDIIncomingJMSMessageListener implements SessionAwareMessageListener<Message> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SterlingEDIIncomingJMSMessageListener.class);
    private static final Character INBOUND_MESSAGE = 'I';

    @Autowired
    @Qualifier("carrierInvoiceIntegrationService")
    private CarrierInvoiceIntegrationServiceImpl carrierInvoiceIntegrationService;

    @Autowired
    @Qualifier("customerTenderService")
    private CustomerLoadTenderServiceImpl customerLoadTenderService;

    @Autowired
    @Qualifier("acknowledgement")
    private AcknowledgementServiceImpl acknowledgementService;

    @Autowired
    @Qualifier("loadTenderAckService")
    private LoadTenderAckServiceImpl loadTenderAckService;

    @Autowired
    @Qualifier("loadTrackingService")
    private LoadTrackingServiceImpl loadTrackingService;
    
    @Autowired
    @Qualifier("loadUpdateIntegrationService")
    private LoadUpdateIntegrationServiceImpl loadUpdateIntegrationService;

    @Autowired
    private IntegrationAuditService auditService;

    @Autowired
    private JaxbService jaxbService;

    private ConcurrentMap<Long, Long> loadIds = new ConcurrentHashMap<Long, Long>();

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        Long loadId = null;
        AuditEntity audit = null;
        try {
            if (!(message instanceof ObjectMessage)) {
                throw new Exception("The message type is required to be an instance of ObjectMessage not: " + message.getClass().getName());
            }

            SterlingIntegrationMessageBO sterlingMessage = extractMessage((ObjectMessage) message);
            authenticateUser(sterlingMessage.getPayload());
            audit = populateAudit(sterlingMessage.getPayload(), sterlingMessage.getType());

            loadId = sterlingMessage.getPayload().getLoadId();
            acquireLock(sterlingMessage.getPayload().getLoadId());

            processMessage(sterlingMessage);
            markRequestCompleted(audit);
        } catch (Exception e) {
            markRequestErrored(audit);
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (loadId != null) {
                loadIds.remove(loadId);
            }
        }
    }

    private void processMessage(SterlingIntegrationMessageBO sterlingMessage) throws ApplicationException {
        switch (sterlingMessage.getType()) {
            case EDI210_STERLING_MESSAGE_TYPE:
                carrierInvoiceIntegrationService.processMessage((LoadMessageJaxbBO) sterlingMessage.getPayload());
                break;
            case EDI204_STERLING_MESSAGE_TYPE:
                customerLoadTenderService.processMessage((LoadMessageJaxbBO) sterlingMessage.getPayload());
                break;
            case EDI990_STERLING_MESSAGE_TYPE:
                loadTenderAckService.processMessage((LoadAcknowledgementJaxbBO) sterlingMessage.getPayload());
                break;
            case EDI214_STERLING_MESSAGE_TYPE:
                loadTrackingService.processMessage((LoadTrackingJaxbBO) sterlingMessage.getPayload());
                break;
            case EDI997_STERLING_MESSAGE_TYPE:
                acknowledgementService.processMessage((AcknowledgementJaxbBO) sterlingMessage.getPayload());
                break;
            case LOAD_UPDATE_MESSAGE_TYPE:
                loadUpdateIntegrationService.processMessage((LoadUpdateJaxbBO) sterlingMessage.getPayload());
                break;
            default:
                break;
        }
    }

    private void acquireLock(Long id) {
        if (id == null) {
            return;
        }
        Long putIfAbsent = loadIds.putIfAbsent(id, id);
        while (putIfAbsent != null) {
            try {
                Thread.sleep(500L);
                putIfAbsent = loadIds.putIfAbsent(id, id);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private SterlingIntegrationMessageBO extractMessage(ObjectMessage om) throws Exception {
        Serializable serializableObj = om.getObject();
        if (serializableObj == null) {
            throw new Exception("The ObjectMessage payload is null.");
        }
        if (!(serializableObj instanceof SterlingIntegrationMessageBO)) {
            throw new Exception("The object that the ObjectMessage is carrying is required to be an InboundMessage not a: "
                    + serializableObj.getClass().getName());
        }
        return (SterlingIntegrationMessageBO) serializableObj;
    }

    private void authenticateUser(IntegrationMessageBO payload) {
        PlsUserDetails user = new PlsUserDetails("dummy", "dummy", true, new HashSet<String>(), payload.getPersonId(), null, false, null);
        SecurityUtils.setupNewAuthentication(user);
    }

    private AuditEntity populateAudit(IntegrationMessageBO message, EDIMessageType messageType) {
        String xml = convertMessage(message);

        AuditEntity audit = new AuditEntity();
        audit.setInbOtb(INBOUND_MESSAGE);
        audit.setMessageType(messageType);
        audit.setScac(message.getScac());
        audit.setShipmentNum(message.getShipmentNo());
        audit.setShipperOrgId(message.getCustomerOrgId());
        audit.setLoadID(message.getLoadId());
        audit.setBol(message.getBol());
        audit.setStatus("P");

        if (StringUtils.isNotEmpty(xml)) {
            AuditDetailEntity detail = new AuditDetailEntity();
            detail.setAudit(audit);
            detail.setMessage(xml);
            audit.setAuditDetail(detail);
        }

        return auditService.saveLog(audit);
    }

    private void markRequestCompleted(AuditEntity audit) {
        audit.setStatus("C");
        auditService.saveLog(audit);
    }

    private void markRequestErrored(AuditEntity audit) {
        if (audit != null) {
            audit.setStatus("E");
            auditService.saveLog(audit);
        }
    }

    private String convertMessage(IntegrationMessageBO message) {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            jaxbService.write(message, outputStream);
            return outputStream.toString();
        } catch (XmlSerializationException ex) {
            LOGGER.error("Caught exception while attempting to serialize an instance of IntegrationMessageBO: %s", ex.getMessage());
            return "";
        }
    }
}
