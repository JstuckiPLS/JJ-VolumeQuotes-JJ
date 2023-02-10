package com.pls.documentmanagement.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.common.MimeTypes;
import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.domain.LoadDocumentEntity;
import com.pls.documentmanagement.domain.bo.ShipmentDocumentInfoBO;
import com.pls.documentmanagement.domain.enums.DocumentTypes;

/**
 * Test cases for LoadDocumentDao implementation.
 *
 * @author Denis Zhupinsky (Team International)
 * @author Viacheslav Vasianovych
 *
 */
public class LoadDocumentDaoIT extends AbstractDaoTest {

    public static final long TEMP_DOC_ID = 777L;

    private static final Long SHIPMENT_ID = 1L;

    private static final Long MANUAL_BOL_ID = 1L;

    private static final Long MANUAL_BOL_ID_WITHOUT_DOCS = 3L;

    private static final long SHIPMENT_ID_WITHOUT_DOCS = 2L;

    private static final List<Long> DOCUMENTS_IDS = Arrays.asList(500L, 501L, 502L, 503L);

    private static final Long SHIPMENT_NOT_EXIST_ID = -1L;

    @Autowired
    private LoadDocumentDao sut;

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Creates detached {@link LoadDocumentEntity} object.
     *
     * @return - detached {@link LoadDocumentEntity} object.
     */
    private LoadDocumentEntity createImage() {
        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setDocumentType("BOL");
        document.setFileType(MimeTypes.PDF);
        document.setDocumentPath("Test path");
        document.setDocFileName("Test file name");
        document.setLoadId(1L);

        return document;
    }

    @Test
    public void testGetImageByIdNormalCase() {
        LoadDocumentEntity entity = createImage();
        Session session = sessionFactory.getCurrentSession();
        session.save(entity);
        flushAndClearSession();
        Long generatedId = entity.getId();
        Assert.assertNotNull(generatedId);
        LoadDocumentEntity savedEntity = sut.find(generatedId);
        Assert.assertNotNull(savedEntity);
        Assert.assertNotNull(savedEntity.getDownloadToken());
    }

    @Test
    public void testGetImageByIdWorngId() {
        LoadDocumentEntity entity = createImage();
        Session session = sessionFactory.getCurrentSession();
        session.save(entity);
        flushAndClearSession();
        Long generatedId = entity.getId();
        Assert.assertNotNull(generatedId);
        LoadDocumentEntity savedEntity = sut.find(++generatedId);
        Assert.assertNull(savedEntity);
    }

    @Test
    public void testGetDocumentsInfoForShipmentNormalCase() throws EntityNotFoundException {
        List<ShipmentDocumentInfoBO> list = sut.getDocumentsInfoForShipment(SHIPMENT_ID);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertTrue(list.size() >= 4);
    }

    @Test
    public void testGetDocumentsInfoForShipmentWithoutDocuments() throws EntityNotFoundException {
        List<ShipmentDocumentInfoBO> list = sut.getDocumentsInfoForShipment(SHIPMENT_ID_WITHOUT_DOCS);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testGetDocumentsInfoForManualBolNormalCase() throws EntityNotFoundException {
        List<ShipmentDocumentInfoBO> list = sut.getDocumentsInfoForManualBol(MANUAL_BOL_ID);
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void testGetDocumentsInfoForManualBolWithoutDocuments() throws EntityNotFoundException {
        List<ShipmentDocumentInfoBO> list = sut.getDocumentsInfoForManualBol(MANUAL_BOL_ID_WITHOUT_DOCS);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testGetDocumentIdsForLoad() {
        List<Long> documentsIds = sut.getDocumentIdsForLoad(SHIPMENT_ID);
        Assert.assertFalse(documentsIds.isEmpty());
        for (Long documentId : documentsIds) {
            Assert.assertTrue(DOCUMENTS_IDS.contains(documentId));
        }
    }

    @Test
    public void testDeleteDocuments() {
        sut.deleteDocuments(DOCUMENTS_IDS);
        flushAndClearSession();

        for (Long documentId : DOCUMENTS_IDS) {
            LoadDocumentEntity entity = getEntity(LoadDocumentEntity.class, documentId);
            Assert.assertEquals(Status.INACTIVE, entity.getStatus());
        }
    }

    @Test
    public void testFindDocumentsForLoad() {
        List<LoadDocumentEntity> documents = sut.findDocumentsForLoad(SHIPMENT_ID, DocumentTypes.VENDOR_BILL.getDbValue());
        Assert.assertEquals(1, documents.size());
        LoadDocumentEntity document = documents.get(0);
        Assert.assertEquals(SHIPMENT_ID, document.getLoadId());
        Assert.assertEquals(DocumentTypes.VENDOR_BILL.getDbValue(), document.getDocumentType());
    }

    @Test
    public void testFindDocumentsForManualBol() {
        List<LoadDocumentEntity> documents = sut.findDocumentsForManualBol(MANUAL_BOL_ID, DocumentTypes.BOL.getDbValue());
        Assert.assertEquals(1, documents.size());
        LoadDocumentEntity document = documents.get(0);
        Assert.assertEquals(MANUAL_BOL_ID, document.getManualBolId());
        Assert.assertEquals(DocumentTypes.BOL.getDbValue(), document.getDocumentType());
    }

    @Test
    public void testFindDocumentsForLoadNotExists() {
        List<LoadDocumentEntity> documents = sut.findDocumentsForLoad(SHIPMENT_NOT_EXIST_ID, DocumentTypes.INVOICE.getDbValue());
        Assert.assertTrue(documents.isEmpty());
    }

    @Test
    public void testDeleteTempDocument() {
        executeScript("findTempDocumentsOlderThanSpecifiedDate.sql");

        LoadDocumentEntity document = getEntity(LoadDocumentEntity.class, TEMP_DOC_ID);
        sut.deleteTempDocument(document);
        flushAndClearSession();

        document = (LoadDocumentEntity) getSession().get(LoadDocumentEntity.class, TEMP_DOC_ID);
        Assert.assertNull(document);
    }

    @Test
    public void testFindTempDocumentsOlderThanSpecifiedDate() {
        executeScript("findTempDocumentsOlderThanSpecifiedDate.sql");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2);
        List<LoadDocumentEntity> documents = sut.findTempDocumentsOlderThanSpecifiedDate(calendar.getTime());

        Assert.assertFalse(documents.isEmpty());
        LoadDocumentEntity document = null;
        for (LoadDocumentEntity documentEntity : documents) {
            if (documentEntity.getId() == TEMP_DOC_ID) {
                document = documentEntity;
                break;
            }
        }

        Assert.assertNotNull(document);
    }

    @Test
    public void testFindTempDocumentsOlderThanSpecifiedDateNotFound() {
        executeScript("findTempDocumentsOlderThanSpecifiedDate.sql");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -10);
        List<LoadDocumentEntity> documents = sut.findTempDocumentsOlderThanSpecifiedDate(calendar.getTime());

        Assert.assertTrue(documents.isEmpty());
    }

    @Test
    public void shouldGetMissingPaperworkForLoad() {
        List<String> paperwork = sut.getMissingPaperworkForLoad(116L);
        assertNotNull(paperwork);
        assertEquals(1, paperwork.size());
        assertEquals("VENDOR BILL", paperwork.get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdatePaperworkReceived() {
        Long dayBeforeToday = new Date().getTime() - 24 * 60 * 60 * 1000;
        List<Object[]> testLoad = getSession().createSQLQuery(
                "select cust_req_doc_recv_flag, date_modified, modified_by, version from loads where load_id = 116").list();
        assertNotNull(testLoad);
        assertEquals(1, testLoad.size());
        assertNull("cust_req_doc_recv_flag", testLoad.get(0)[0]);
        assertTrue("date_modified: " + testLoad.get(0)[1] + " is not in the past. Today: " + new Date(),
                ((Date) testLoad.get(0)[1]).getTime() < dayBeforeToday);
        assertEquals("modified_by", BigInteger.ONE, testLoad.get(0)[2]);
        assertEquals("version", 1, testLoad.get(0)[3]);

        LoadDocumentEntity document = new LoadDocumentEntity();
        document.setLoadId(116L);
        document.setDocumentType("VENDOR BILL");
        getSession().save(document);
        getSession().flush();

        sut.updatePaperworkReceived(116L);

        List<String> paperwork = sut.getMissingPaperworkForLoad(116L);
        assertNotNull(paperwork);
        assertEquals(0, paperwork.size());

        testLoad = getSession().createSQLQuery(
                "select cust_req_doc_recv_flag, date_modified, modified_by, version from loads where load_id = 116").list();
        assertNotNull(testLoad);
        assertEquals(1, testLoad.size());
        assertEquals("cust_req_doc_recv_flag", 'Y', testLoad.get(0)[0]);
        assertTrue("date_modified: " + testLoad.get(0)[1] + " is not updated. Today: " + new Date(),
                dayBeforeToday < ((Date) testLoad.get(0)[1]).getTime());
        assertEquals("modified_by", BigInteger.ZERO, testLoad.get(0)[2]);
        assertEquals("version", 2, testLoad.get(0)[3]);
    }

    @Test
    public void shouldFindCustomerLogo() {
        CustomerEntity customer = (CustomerEntity) getSession().get(CustomerEntity.class, 1L);
        assertNull(customer.getDisplayLogoOnBol());
        assertNull(sut.findCustomerLogoForBOL(1L));

        customer.setDisplayLogoOnBol(true);
        getSession().save(customer);
        getSession().flush();
        assertNull(sut.findCustomerLogoForBOL(1L));

        customer.setLogoId(1L);
        getSession().save(customer);
        getSession().flush();
        assertNotNull(sut.findCustomerLogoForBOL(1L));

        customer.setDisplayLogoOnBol(false);
        getSession().save(customer);
        getSession().flush();
        assertNull(sut.findCustomerLogoForBOL(1L));
    }

    @Test
    public void shouldFindCreatedDatesForRequiredDocumentsByLoadId() {
        List<Date> dates = sut.findCreatedDatesForReqDocsByLoadId(1L);
        Assert.assertFalse(dates.isEmpty());
        Assert.assertEquals(1, dates.size());
    }
}
