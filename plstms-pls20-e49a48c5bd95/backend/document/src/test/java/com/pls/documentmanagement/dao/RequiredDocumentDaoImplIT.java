package com.pls.documentmanagement.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;

/**
 * Test cases for {@link com.pls.documentmanagement.dao.impl.RequiredDocumentDaoImpl} class.
 * 
 * @author Alexander Nalapko
 * 
 */
public class RequiredDocumentDaoImplIT extends AbstractDaoTest {

    @Autowired
    private RequiredDocumentDao dao;

    @Test
    public void testGetDocument() throws EntityNotFoundException {
        RequiredDocumentEntity doc = dao.get(1L);
        assertNotNull(doc);
        assertTrue(1L == doc.getId());
    }

    @Test
    public void testUpdateDocument() throws EntityNotFoundException {
        RequiredDocumentEntity documentEntity = updateEntity(dao.get(1L));
        assertNotNull(documentEntity);
        assertTrue(1L == documentEntity.getId());

        dao.update(documentEntity);
        flushAndClearSession();

        compareDocuments(documentEntity, dao.get(1L));
    }

    @Test
    public void testAddRequiredDocument() throws Exception {
        RequiredDocumentEntity createdEntity = createEntity();
        createdEntity.getModification().setCreatedBy(1L);
        createdEntity.getModification().setModifiedBy(1L);
        createdEntity.setStatus(Status.ACTIVE);
        dao.persist(createdEntity);
        Long id = createdEntity.getId();

        flushAndClearSession();

        RequiredDocumentEntity documentEntity = dao.get(id);
        compareDocuments(documentEntity, createdEntity);
    }

    @Test
    public void testGetDocumentsForShipmentTypes() {
        Map<LoadDocumentTypeEntity, RequiredDocumentEntity> map = dao.getDocumentsForShipmentTypes(1L);

        assertNotNull(map);
        int countOfCurrentlyExistingDocTypes = 19;
        assertTrue(map.size() >= countOfCurrentlyExistingDocTypes);
    }

    @Test
    public void shouldCalculateAllPaperworksRequiredForBillToInvoiceFlagWhenNoDocumentsRequired() {
        assertTrue(dao.isAllPaperworkRequiredForBillToInvoicePresent(714L));
    }

    @Test
    public void shouldCalculateAllPaperworksRequiredForBillToInvoiceFlagWhenAllDocumentsPresent() {
        assertTrue(dao.isAllPaperworkRequiredForBillToInvoicePresent(1L));
    }

    @Test
    public void shouldCalculateAllPaperworksRequiredForBillToInvoiceFlagWhenNotAllDocumentsPresent() {
        assertFalse(dao.isAllPaperworkRequiredForBillToInvoicePresent(635L));
    }

    /**
     * Return document entity with data.
     */
    private RequiredDocumentEntity createEntity() throws EntityNotFoundException {
        return updateEntity(new RequiredDocumentEntity());
    }

    /**
     * Update document entity with the new data.
     */
    private RequiredDocumentEntity updateEntity(RequiredDocumentEntity documentEntity)
            throws EntityNotFoundException {
        Criteria criteria = getSession().createCriteria(LoadDocumentTypeEntity.class);
        Criterion condition = Restrictions.eq("docTypeString", "UNKNOWN");
        LoadDocumentTypeEntity unknownType = (LoadDocumentTypeEntity) criteria.add(condition).uniqueResult();
        documentEntity.setDocumentType(unknownType);
        documentEntity.setBillTo((BillToEntity) getSession().get(BillToEntity.class, 1L));
        return documentEntity;
    }

    /**
     * Compare actual and expected document.
     * 
     * @param expected
     * @param actual
     * @return
     */
    private void compareDocuments(RequiredDocumentEntity expected, RequiredDocumentEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDocumentType().getId(), actual.getDocumentType().getId());
        assertEquals(expected.getBillTo().getId(), actual.getBillTo().getId());
    }
}