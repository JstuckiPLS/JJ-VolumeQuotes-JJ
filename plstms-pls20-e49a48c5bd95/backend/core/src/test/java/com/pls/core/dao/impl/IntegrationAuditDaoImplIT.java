package com.pls.core.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.dao.IntegrationAuditDao;
import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.integration.AuditBO;

/**
 * Unit test for testing operations on IntegrationAuditDao with different scenarios.
 *
 * @author Yasaman Palumbo
 *
 */
public class IntegrationAuditDaoImplIT extends AbstractDaoTest {

    private static final String BOL = "12345";
    private static final char IN = 'I';
    private static final char OUT = 'O';
    private static final String MESSAGE = "Test Message";
    private static final String SCAC = "RLCA";
    private static final String SHIPMENT_NUM = "S7485485";
    private static final Long SHIPPER_ORG_ID = 12525L;
    private static final Calendar FROM_DATE = new GregorianCalendar(2013, 11, 8);
    private static final Calendar TO_DATE = new GregorianCalendar(2013, 11, 14);

    @Autowired
    private IntegrationAuditDao sut;

    private AuditEntity createAudit() {
        AuditEntity audit = new AuditEntity();
        audit.setId(1L);
        audit.setMessageType(EDIMessageType.EDI990_STERLING_MESSAGE_TYPE);
        audit.setLoadID(234567L);
        audit.setShipperOrgId(23L);
        audit.setBol("555");
        audit.setInbOtb('I');
        audit.setViewedBy(2L);
        Calendar cal = new GregorianCalendar(2014, 11, 12);
        Date viewDate = cal.getTime();
        audit.setViewedDate(viewDate);
        AuditDetailEntity auditDetail = new AuditDetailEntity();
        auditDetail.setId(3L);
        auditDetail.setAudit(audit);
        auditDetail.setMessage("I am Message 1");
        audit.setAuditDetail(auditDetail);
        return audit;
    }

    @Test
    public void testGetLogDetailsById() {
        assertNotNull(sut.getLogDetailsByAuditId(1L));
        assertEquals(createAudit().getAuditDetail().getMessage(), sut.getLogDetailsByAuditId(1L).getMessage());
    }

    /**
     * This test assures that you can find logs by only giving the dates.
     */
    @Test
    public void testReturnLogsByDate() {
        List<AuditBO> logs = sut.getLogsByCriteria(FROM_DATE.getTime(), TO_DATE.getTime(), null, null, null, null, null, null);
        assertEquals(logs.size(), 4);
    }

    @Test
    public void testReturnLogsByDateAndMessageType() {
        List<AuditBO> logs =
                sut.getLogsByCriteria(FROM_DATE.getTime(), TO_DATE.getTime(),
                        null, null, null, null, null, EDIMessageType.EDI990_STERLING_MESSAGE_TYPE);
        assertEquals(logs.size(), 2);
    }

    @Test
    public void testReturnLogsByLoadId() {
        List<AuditBO> logs = sut.getLogsByCriteria(null, null, null, null, null, null, 234568L, null);
        assertEquals(logs.size(), 2);
    }

    @Test
    public void testReturnLogsByBOL() {
        List<AuditBO> logs = sut.getLogsByCriteria(null, null, null, null, "3456", null, null, null);
        assertEquals(logs.size(), 1);
    }

    @Test
    public void testReturnLogsByShipmentNumber() {
        List<AuditBO> logs = sut.getLogsByCriteria(null, null, null, null, null, "444", null, null);
        assertEquals(logs.size(), 1);
    }

    @Test
    public void testReturnLogsByDateAndLoadId() {
        List<AuditBO> logs = sut.getLogsByCriteria(FROM_DATE.getTime(), TO_DATE.getTime(), null, null, null, null, 234568L, null);
        assertEquals(logs.size(), 2);
    }

    @Test
    public void testReturnLogsByDateAndBOL() {
        List<AuditBO> logs = sut.getLogsByCriteria(FROM_DATE.getTime(), TO_DATE.getTime(), null, null, "3456", null, null, null);
        assertEquals(logs.size(), 1);
    }

    @Test
    public void testReturnLogsByDateAndShipmentNumber() {
        List<AuditBO> logs = sut.getLogsByCriteria(FROM_DATE.getTime(), TO_DATE.getTime(), null, null, null, "444", null, null);
        assertEquals(logs.size(), 1);
    }

    @Test
    public void testReturnLogsByDateAndCustomer() {
        List<AuditBO> logs = sut.getLogsByCriteria(FROM_DATE.getTime(), TO_DATE.getTime(), 23L, null, null, null, null, null);
        assertEquals(logs.size(), 1);
    }

    @Test
    public void testReturnLogsByDateAndCarrier() {
        List<AuditBO> logs = sut.getLogsByCriteria(FROM_DATE.getTime(), TO_DATE.getTime(), null, "TST1", null, null, null, null);
        assertEquals(logs.size(), 3);
    }

    /**
     * This Test assures that the not nullable fields cannot be null.
     */
    @Test(expected = Exception.class)
    public void testNullMessage() {
        AuditEntity audit = new AuditEntity();
        AuditDetailEntity detail = new AuditDetailEntity();
        detail.setAudit(audit);
        audit.setAuditDetail(detail);
        sut.saveOrUpdate(audit);
        flushAndClearSession();
    }

    /**
     * This test assures that we can insert and read from our database with minimal data.
     *
     * @throws EntityNotFoundException
     */
    @Test
    public void testCreateAuditLog() {
        AuditEntity audit = getAuditEntityWithMinimalData(IN);
        audit.setBol(BOL);
        sut.saveOrUpdate(audit);
        flushAndClearSession();
        verifyResult(audit, sut.find(audit.getId()));
    }

    /**
     * All inbound messages received by PLSPRO (for eg., Load messages from customer and carriers) are logged
     * into the audit tables. For messages received from carrier, fields - Shipment Num, Load ID, and Shipper
     * Org ID may not be available. Logging such messages to Audit tables should be allowed even when all
     * fields are not available.
     *
     * This test assures that we can leave Shipment Number, Shipper Org ID and Load ID as null while adding an
     * entry to the database (This is the case when we receive an inbound message from carrier).
     */
    @Test
    public void testCreateforCarrierInbound() {
        AuditEntity audit = getAuditEntityWithMinimalData(IN);
        audit.setBol(BOL);
        audit.setScac(SCAC);
        sut.saveOrUpdate(audit);
        flushAndClearSession();
        verifyResult(audit, sut.find(audit.getId()));
    }

    /**
     * All inbound messages received by PLSPRO (for eg., Load messages from customer and carriers) are logged
     * into the audit tables. For messages received from customer, fields - BOL, Load ID, and SCAC may not be
     * available. Logging such messages to Audit tables should be allowed even when all fields are not
     * available.
     *
     * This test assures that we can leave BOL, SCAC and Load ID as null while adding an entry to the database
     * (This is the case when we receive an inbound message from customer).
     */
    @Test
    public void testCreateforCustomerInbound() {
        AuditEntity audit = getAuditEntityWithMinimalData(IN);
        audit.setShipmentNum(SHIPMENT_NUM);
        audit.setShipperOrgId(SHIPPER_ORG_ID);
        sut.saveOrUpdate(audit);
        flushAndClearSession();
        verifyResult(audit, sut.find(audit.getId()));
    }

    /**
     * All outbound messages sent from PLSPRO (for eg., Load messages and Finance messages) are logged into
     * the audit tables. For finance messages, fields - BOL, Shipment Num, Load ID, SCAC and Shipper Org id
     * are not always available. Logging such messages to Audit tables should be allowed even when all fields
     * are not available.
     *
     * This test assures that we can leave BOL, SCAC id, shipment number and Load ID as null while adding an
     * entry to the database (This is case for customer create/update to finance system).
     */
    @Test
    public void testCreateforOutbound() {
        AuditEntity audit = getAuditEntityWithMinimalData(OUT);
        audit.setShipperOrgId(SHIPPER_ORG_ID);
        sut.saveOrUpdate(audit);
        flushAndClearSession();
        verifyResult(audit, sut.find(audit.getId()));
    }

    @Test
    public void testGetLastEdi204Xml() {
        String result = sut.getLastEdi204XMLByLoadIdAndScac(7001L, "TST2");
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "I am Message 6");
    }

    @Test
    public void testGetEdi204XmlForInbound() {
        AuditEntity audit = getAuditEntityWithEdi204SterlingMessageType("test1", IN);
        sut.saveOrUpdate(audit);
        flushAndClearSession();
        String result = sut.getLastEdi204XMLByLoadIdAndScac(audit.getLoadId(), audit.getScac());
        Assert.assertNull(result);
    }

    @Test
    public void testGetEdi204XmlForOutbound() {
        AuditEntity audit = getAuditEntityWithEdi204SterlingMessageType("test3", OUT);
        sut.saveOrUpdate(audit);
        flushAndClearSession();
        String result = sut.getLastEdi204XMLByLoadIdAndScac(audit.getLoadId(), audit.getScac());
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "test3");
    }

    @Test
    public void testGetEdi204XmlForStatus_C() {
        AuditEntity audit = getAuditEntityWithEdi204SterlingMessageType("testC", OUT);
        audit.setStatus("C");
        sut.saveOrUpdate(audit);
        flushAndClearSession();
        String result = sut.getLastEdi204XMLByLoadIdAndScac(audit.getLoadId(), audit.getScac());
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "testC");
    }

    @Test
    public void testGetEdi204XmlFromStatus_Not_C() {
        AuditEntity audit = getAuditEntityWithEdi204SterlingMessageType("testC", OUT);
        audit.setStatus("D");
        sut.saveOrUpdate(audit);
        flushAndClearSession();
        String result = sut.getLastEdi204XMLByLoadIdAndScac(audit.getLoadId(), audit.getScac());
        Assert.assertNull(result);
    }

    private AuditEntity getAuditEntityWithMinimalData(Character inbOtb) {
        AuditEntity audit = new AuditEntity();
        audit.setInbOtb(inbOtb);
        audit.setMessageType(EDIMessageType.EDI997_STERLING_MESSAGE_TYPE);
        AuditDetailEntity detail = new AuditDetailEntity();
        detail.setMessage(MESSAGE);
        detail.setAudit(audit);
        audit.setAuditDetail(detail);
        return audit;
    }

    private AuditEntity getAuditEntityWithEdi204SterlingMessageType(String msg, Character inbOtb) {
        AuditEntity audit = new AuditEntity();
        audit.setMessageType(EDIMessageType.EDI204_STERLING_MESSAGE_TYPE);
        audit.setScac("TST2");
        audit.setLoadID(700002L);
        audit.setShipperOrgId(23L);
        audit.setBol("555");
        audit.setInbOtb(inbOtb);
        audit.setViewedBy(2L);
        audit.setStatus("C");
        Calendar cal = new GregorianCalendar(2014, 11, 12);
        Date viewDate = cal.getTime();
        audit.setViewedDate(viewDate);
        AuditDetailEntity auditDetail = new AuditDetailEntity();
        auditDetail.setId(3L);
        auditDetail.setAudit(audit);
        auditDetail.setMessage(msg);
        audit.setAuditDetail(auditDetail);
        return audit;
    }

    private void verifyResult(AuditEntity actual, AuditEntity expected) {
        assertNotNull(actual);
        Assert.assertTrue(new EqualsBuilder().append(expected.getBol(), actual.getBol()).append(expected.getViewedBy(), actual.getViewedBy())
                .append(expected.getViewedDate(), actual.getViewedDate()).append(expected.getInbOtb(), actual.getInbOtb())
                .append(expected.getLoadId(), actual.getLoadId()).append(expected.getMessageType(), actual.getMessageType())
                .append(expected.getScac(), actual.getScac()).append(expected.getStatus(), actual.getStatus())
                .append(expected.getShipmentNum(), actual.getShipmentNum()).append(expected.getShipperOrgId(), actual.getShipperOrgId())
                .append(expected.getAuditDetail().getMessage(), actual.getAuditDetail().getMessage()).isEquals());
        assertNotNull(actual.getAuditDetail().getAudit().getId());
    }
}
