package com.pls.shipment.service.impl.edi;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.dao.IntegrationAuditDao;
import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.exception.ApplicationException;
import com.pls.core.exception.XmlSerializationException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.service.xml.JaxbService;
import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;
import com.pls.shipment.domain.sterling.enums.OperationType;

/**
 * Test cases to check the functionality for EDIHelper implementation.
 * 
 * @author Brichak Aleksandr
 * 
 */
public class EDIHelperTestIT extends BaseServiceITClass {

    @Autowired
    private EDIHelper helper;

    @Autowired
    private IntegrationAuditDao auditDao;

    @Autowired
    private JaxbService jaxbService;

    private static final String INCORRECT_MSG_TYPE = "TRACKING";

    private static final String CORRECT_MSG_TYPE = "LOAD_TENDER";

    @Before
    public void setUp() throws Exception {
        SecurityTestUtils.login("test", 2L, 1L);
    }

    /*@Test
    public void testEdi204WithOperationTypeCreate() {
        Boolean isXMLChanged = helper.isEdi204Changed(getFullLoadMessageJaxbBO());
        Assert.assertTrue(isXMLChanged);
    }

    @Test
    public void testEdi204WithOperationTypeUpdate() throws Exception {
        LoadMessageJaxbBO loadMessageJaxbBO = getFullLoadMessageJaxbBO();
        auditDao.saveOrUpdate(
                getAuditEntityWithEdi204SterlingMessageType(loadMessageJaxbBO, convertMessage(loadMessageJaxbBO)));
        loadMessageJaxbBO.setOperationType(OperationType.UPDATE);
        loadMessageJaxbBO.setPersonId(2L);
        Boolean isXMLChanged = helper.isEdi204Changed(loadMessageJaxbBO);
        Assert.assertFalse(isXMLChanged);
    }

    @Test
    public void testEdi204WithOperationTypeCANCEL() throws Exception {
        LoadMessageJaxbBO loadMessageJaxbBO = getFullLoadMessageJaxbBO();
        auditDao.saveOrUpdate(
                getAuditEntityWithEdi204SterlingMessageType(loadMessageJaxbBO, convertMessage(loadMessageJaxbBO)));
        loadMessageJaxbBO.setOperationType(OperationType.CANCEL);
        Boolean isXMLChanged = helper.isEdi204Changed(loadMessageJaxbBO);
        Assert.assertTrue(isXMLChanged);
    }

    @Test
    public void testEdi204WithIncorrectMsgType() throws ApplicationException {
        LoadMessageJaxbBO loadMessageJaxbBO = getFullLoadMessageJaxbBO();
        auditDao.saveOrUpdate(
                getAuditEntityWithEdi204SterlingMessageType(loadMessageJaxbBO, convertMessage(loadMessageJaxbBO)));
        loadMessageJaxbBO.setMessageType(INCORRECT_MSG_TYPE);
        Boolean isXMLChanged = helper.isEdi204Changed(loadMessageJaxbBO);
        Assert.assertNull(isXMLChanged);
    }

    @Test
    public void testEdi204WithCorrectMsgType() throws ApplicationException {
        LoadMessageJaxbBO loadMessageJaxbBO = getFullLoadMessageJaxbBO();
        auditDao.saveOrUpdate(
                getAuditEntityWithEdi204SterlingMessageType(loadMessageJaxbBO, convertMessage(loadMessageJaxbBO)));
        loadMessageJaxbBO.setMessageType(CORRECT_MSG_TYPE);
        Boolean isXMLChanged = helper.isEdi204Changed(loadMessageJaxbBO);
        Assert.assertTrue(isXMLChanged);
    }

    @Test
    public void testEdi204IfSomeFieldChaged() throws ApplicationException {
        LoadMessageJaxbBO loadMessageJaxbBO = getFullLoadMessageJaxbBO();
        auditDao.saveOrUpdate(
                getAuditEntityWithEdi204SterlingMessageType(loadMessageJaxbBO, convertMessage(loadMessageJaxbBO)));
        loadMessageJaxbBO.setBol("newBol");
        Boolean isXMLChanged = helper.isEdi204Changed(loadMessageJaxbBO);
        Assert.assertTrue(isXMLChanged);
    }

    @Test
    public void testEdi204IfPreviousNotFound() throws ApplicationException {
        LoadMessageJaxbBO loadMessageJaxbBO = getFullLoadMessageJaxbBO();
        loadMessageJaxbBO.setOperationType(OperationType.UPDATE);
        Boolean isXMLChanged = helper.isEdi204Changed(loadMessageJaxbBO);
        Assert.assertNull(isXMLChanged);
    }
*/
    @Test
    public void testEdi204IfCarrierChanged() throws ApplicationException {
        LoadMessageJaxbBO loadMessageJaxbBO = getFullLoadMessageJaxbBO();
        loadMessageJaxbBO.setOperationType(OperationType.UPDATE);
        auditDao.saveOrUpdate(
                getAuditEntityWithEdi204SterlingMessageType(loadMessageJaxbBO, convertMessage(loadMessageJaxbBO)));
        loadMessageJaxbBO.setScac("Diff");
        Boolean isXMLChanged = helper.isEdi204Changed(loadMessageJaxbBO);
        Assert.assertNull(isXMLChanged);
    }

    private AuditEntity getAuditEntityWithEdi204SterlingMessageType(LoadMessageJaxbBO loadMessageJaxbBO, String msg) {
        AuditEntity audit = new AuditEntity();
        audit.setMessageType(EDIMessageType.getEDIMessageByType(loadMessageJaxbBO.getMessageType()));
        audit.setScac(loadMessageJaxbBO.getScac());
        audit.setLoadID(loadMessageJaxbBO.getLoadId());
        audit.setInbOtb(loadMessageJaxbBO.getInboundOutbound().charAt(0));
        audit.setStatus("C");
        AuditDetailEntity auditDetail = new AuditDetailEntity();
        auditDetail.setAudit(audit);
        auditDetail.setMessage(msg);
        audit.setAuditDetail(auditDetail);
        return audit;
    }

    private String convertMessage(LoadMessageJaxbBO message) throws ApplicationException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            jaxbService.write(message, outputStream);
            return outputStream.toString();
        } catch (XmlSerializationException e) {
            throw new ApplicationException(
                    String.format("Caught exception while attempting to serialize an instance of LoadMessageJaxbBO: %s",
                            e.getMessage()),
                    e);
        }
    }

    private LoadMessageJaxbBO getFullLoadMessageJaxbBO() {
        LoadMessageJaxbBO loadMessageJaxbBO = new LoadMessageJaxbBO();
        loadMessageJaxbBO.setLoadId(7001L);
        loadMessageJaxbBO.setScac("TST2");
        loadMessageJaxbBO.setMessageType("LOAD_TENDER");
        loadMessageJaxbBO.setCustomerName("PLS SHIPPER");
        loadMessageJaxbBO.setShipmentNo("5345");
        loadMessageJaxbBO.setInboundOutbound("O");
        loadMessageJaxbBO.setOperationType(OperationType.TENDER);
        loadMessageJaxbBO.setPayTerms("PPD");
        loadMessageJaxbBO.setGlNumber("3453453");
        loadMessageJaxbBO.setBol("3453");
        loadMessageJaxbBO.setPoNum("54");
        loadMessageJaxbBO.setFileType("204");
        loadMessageJaxbBO.setPickupNum("5353");
        loadMessageJaxbBO.setSoNum("534534");
        loadMessageJaxbBO.setTrailerNum("4543");
        loadMessageJaxbBO.setPickupNotes("Test pickup notes.");
        loadMessageJaxbBO.setDeliveryNotes("Test delivery notes.");
        loadMessageJaxbBO.setTotalMiles(182);
        loadMessageJaxbBO.setTotalWeight(new BigDecimal(56));
        loadMessageJaxbBO.setTotalPieces(0);
        loadMessageJaxbBO.setTotalQuantity(1);
        loadMessageJaxbBO.setTotalCost(BigDecimal.valueOf((109.8)));
        loadMessageJaxbBO.setPersonId(1L);
        loadMessageJaxbBO.setCustomerOrgId(1L);
        loadMessageJaxbBO.setCustomerLocationId(1L);
        loadMessageJaxbBO.setCustomerBillToId(1L);
        return loadMessageJaxbBO;
    }
}
