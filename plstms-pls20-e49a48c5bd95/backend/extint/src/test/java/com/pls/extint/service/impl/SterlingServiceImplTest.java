package com.pls.extint.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.OutboundEdiQueueMapEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.domain.sterling.SterlingIntegrationMessageBO;
import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.IntegrationAuditService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.util.OutboundEdiQueueMappingUtils;
import com.pls.core.service.xml.JaxbService;
import com.pls.extint.sterling.producer.SterlingMessageProducer;

/**
 * Test cases to check the functionality for SterlingService implementation.
 * 
 * @author Pavani Challa
 */
@RunWith(MockitoJUnitRunner.class)
public class SterlingServiceImplTest {

    @InjectMocks
    private SterlingServiceImpl service;

    @Mock
    private JaxbService jaxbService;

    @Mock
    private SterlingMessageProducer sterlingProducer;

    @Mock
    private IntegrationAuditService auditService;

    @Mock
    private IntegrationMessageBO intMessage;

    @Mock
    private OutboundEdiQueueMappingUtils outboundEdiQueueMappingUtils;

    private static final String SCAC = "SCAC";
    private static final String SHIPMENT_NUM = "Shipment No";
    private static final Long LOAD_ID = 14584485L;
    private static final String BOL = "bol";
    private static final Long CUSTOMER_ORG_ID = 45854L;
    private static final String XML_MESSAGE = "XML Message of the payload";
    private static final String QUEUE_NAME = "EDI20OutboundQueue1";
    private static final Integer PRIORITY = 3;

    @Before
    public void init() throws Exception {
        when(intMessage.getPersonId()).thenReturn(SecurityTestUtils.DEFAULT_PERSON_ID);
        when(intMessage.getBol()).thenReturn(BOL);
        when(intMessage.getScac()).thenReturn(SCAC);
        when(intMessage.getLoadId()).thenReturn(LOAD_ID);
        when(intMessage.getCustomerOrgId()).thenReturn(CUSTOMER_ORG_ID);
        when(intMessage.getShipmentNo()).thenReturn(SHIPMENT_NUM);
        when(outboundEdiQueueMappingUtils
                .getOutboundEdiQueueMapEntityByScac(SCAC))
                        .thenReturn(getQueueCapabilityEntity());
        when(outboundEdiQueueMappingUtils
                .getOutboundEdiQueueMapEntityById(CUSTOMER_ORG_ID))
                        .thenReturn(getQueueCapabilityEntity());
        when(jaxbService.read(any(InputStream.class))).thenReturn(intMessage);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Exception {
                Object[] args = invocation.getArguments();
                OutputStream out = (OutputStream) args[1];
                out.write(XML_MESSAGE.getBytes());

                return null;
            }
        }).when(jaxbService).write(eq(intMessage), any(OutputStream.class));
    }

    @Test
    public void testPublishLoadTender() throws ApplicationException {
        OutboundEdiQueueMapEntity queueCapabilityByScac = getQueueCapabilityEntity();

        service.sendMessage(new SterlingIntegrationMessageBO(intMessage, EDIMessageType.EDI204_STERLING_MESSAGE_TYPE));

        Mockito.verify(outboundEdiQueueMappingUtils, Mockito.times(1))
                .getOutboundEdiQueueMapEntityByScac(intMessage.getScac());
        Mockito.verify(sterlingProducer, Mockito.times(1)).publishEdiMessage(
                XML_MESSAGE, queueCapabilityByScac.getQueueName(),
                queueCapabilityByScac.getPriority());
        ArgumentCaptor<AuditEntity> captor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditService).saveLog(captor.capture());
        verifyResult(captor.getValue(), getLoadOutboundAudit(EDIMessageType.EDI204_STERLING_MESSAGE_TYPE));
    }

    @Test
    public void testPublishTrackingUpdates() throws ApplicationException {
        OutboundEdiQueueMapEntity queueCapabilityById = getQueueCapabilityEntity();

        service.sendMessage(new SterlingIntegrationMessageBO(intMessage, EDIMessageType.EDI214_STERLING_MESSAGE_TYPE));

        Mockito.verify(outboundEdiQueueMappingUtils, Mockito.times(1))
                .getOutboundEdiQueueMapEntityById(
                        intMessage.getCustomerOrgId());
        Mockito.verify(sterlingProducer, Mockito.times(1)).publishEdiMessage(
                XML_MESSAGE, queueCapabilityById.getQueueName(),
                queueCapabilityById.getPriority());
        ArgumentCaptor<AuditEntity> captor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditService).saveLog(captor.capture());
        verifyResult(captor.getValue(), getLoadOutboundAudit(EDIMessageType.EDI214_STERLING_MESSAGE_TYPE));
    }

    @Test
    public void testPublishCustomerInvoice() throws ApplicationException {
        OutboundEdiQueueMapEntity queueCapabilityById = getQueueCapabilityEntity();

        service.sendMessage(new SterlingIntegrationMessageBO(intMessage, EDIMessageType.CUSTOMER_INVOICE_MESSAGE_TYPE));

        Mockito.verify(outboundEdiQueueMappingUtils, Mockito.times(1))
                .getOutboundEdiQueueMapEntityById(
                        intMessage.getCustomerOrgId());
        Mockito.verify(sterlingProducer, Mockito.times(1)).publishEdiMessage(
                XML_MESSAGE, queueCapabilityById.getQueueName(),
                queueCapabilityById.getPriority());
        ArgumentCaptor<AuditEntity> captor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditService).saveLog(captor.capture());
        verifyResult(captor.getValue(), getLoadOutboundAudit(EDIMessageType.CUSTOMER_INVOICE_MESSAGE_TYPE));
    }

    @Test
    public void testPublishLdTenderAcknowledment() throws ApplicationException {
        OutboundEdiQueueMapEntity queueCapabilityById = getQueueCapabilityEntity();

        service.sendMessage(new SterlingIntegrationMessageBO(intMessage, EDIMessageType.EDI990_STERLING_MESSAGE_TYPE));

        Mockito.verify(outboundEdiQueueMappingUtils, Mockito.times(1))
                .getOutboundEdiQueueMapEntityById(
                        intMessage.getCustomerOrgId());
        Mockito.verify(sterlingProducer, Mockito.times(1)).publishEdiMessage(
                XML_MESSAGE, queueCapabilityById.getQueueName(),
                queueCapabilityById.getPriority());
        ArgumentCaptor<AuditEntity> captor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditService).saveLog(captor.capture());
        verifyResult(captor.getValue(), getLoadOutboundAudit(EDIMessageType.EDI990_STERLING_MESSAGE_TYPE));
    }

    @Test
    public void testPublishARMessage() throws ApplicationException {
        service.sendMessage(new SterlingIntegrationMessageBO(intMessage, EDIMessageType.AR));

        verify(sterlingProducer).publishFinanceMessage(XML_MESSAGE);
        ArgumentCaptor<AuditEntity> captor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditService).saveLog(captor.capture());
        verifyResult(captor.getValue(), getLoadOutboundAudit(EDIMessageType.AR));
    }

    @Test
    public void testPublishAPMessage() throws ApplicationException {
        service.sendMessage(new SterlingIntegrationMessageBO(intMessage, EDIMessageType.AP));

        verify(sterlingProducer).publishFinanceMessage(XML_MESSAGE);
        ArgumentCaptor<AuditEntity> captor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditService).saveLog(captor.capture());
        verifyResult(captor.getValue(), getLoadOutboundAudit(EDIMessageType.AP));
    }

    @Test
    public void testPublishCustomerUpdate() throws ApplicationException {
        when(intMessage.getLoadId()).thenReturn(null);
        when(intMessage.getShipmentNo()).thenReturn(null);
        when(intMessage.getBol()).thenReturn(null);
        when(intMessage.getScac()).thenReturn(null);

        service.sendMessage(new SterlingIntegrationMessageBO(intMessage, EDIMessageType.CUSTOMER));

        verify(sterlingProducer).publishFinanceMessage(XML_MESSAGE);
        ArgumentCaptor<AuditEntity> captor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditService).saveLog(captor.capture());
        verifyResult(captor.getValue(), getCustomerUpdateAudit(EDIMessageType.CUSTOMER));
    }

    private AuditEntity getLoadOutboundAudit(EDIMessageType messageType) {
        AuditEntity entity = getAuditEntityWithMinimalData(messageType, 'O');
        entity.setShipmentNum(SHIPMENT_NUM);
        entity.setShipperOrgId(CUSTOMER_ORG_ID);
        entity.setScac(SCAC);
        entity.setBol(BOL);
        entity.setLoadID(LOAD_ID);
        entity.setStatus("C");

        return entity;
    }

    private AuditEntity getCustomerUpdateAudit(EDIMessageType messageType) {
        AuditEntity entity = getAuditEntityWithMinimalData(messageType, 'O');
        entity.setShipperOrgId(CUSTOMER_ORG_ID);
        entity.setStatus("C");

        return entity;
    }

    private AuditEntity getAuditEntityWithMinimalData(EDIMessageType messageType, Character direction) {
        AuditEntity entity = new AuditEntity();
        entity.setInbOtb(direction);
        entity.setMessageType(messageType);
        entity.setStatus("P");
        entity.getModification().setCreatedBy(SecurityTestUtils.DEFAULT_PERSON_ID);
        entity.getModification().setModifiedBy(SecurityTestUtils.DEFAULT_PERSON_ID);

        AuditDetailEntity detail = new AuditDetailEntity();
        detail.setMessage(XML_MESSAGE);
        detail.setAudit(entity);
        entity.setAuditDetail(detail);

        return entity;
    }

    private OutboundEdiQueueMapEntity getQueueCapabilityEntity() {
        OutboundEdiQueueMapEntity queueCapabilityEntity = new OutboundEdiQueueMapEntity();
        queueCapabilityEntity.setQueueName(QUEUE_NAME);
        queueCapabilityEntity.setPriority(PRIORITY);

        return queueCapabilityEntity;
    }

    private void verifyResult(AuditEntity actual, AuditEntity expected) {
        assertNotNull(actual);
        Assert.assertTrue(new EqualsBuilder().append(expected.getBol(), actual.getBol()).append(expected.getViewedBy(), actual.getViewedBy())
                .append(expected.getViewedDate(), actual.getViewedDate()).append(expected.getInbOtb(), actual.getInbOtb())
                .append(expected.getLoadId(), actual.getLoadId()).append(expected.getMessageType(), actual.getMessageType())
                .append(expected.getScac(), actual.getScac()).append(expected.getStatus(), actual.getStatus())
                .append(expected.getShipmentNum(), actual.getShipmentNum()).append(expected.getShipperOrgId(), actual.getShipperOrgId())
                .append(expected.getAuditDetail().getMessage(), actual.getAuditDetail().getMessage()).isEquals());
    }
}
