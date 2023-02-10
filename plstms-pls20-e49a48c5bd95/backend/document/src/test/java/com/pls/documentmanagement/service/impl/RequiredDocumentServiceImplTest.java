package com.pls.documentmanagement.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.document.LoadDocumentTypeEntity;
import com.pls.core.domain.enums.DocRequestType;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.core.shared.Status;
import com.pls.documentmanagement.dao.RequiredDocumentDao;
import com.pls.documentmanagement.domain.RequiredDocumentEntity;

/**
 * Test cases for {@link RequiredDocumentServiceImpl}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@RunWith(MockitoJUnitRunner.class)
public class RequiredDocumentServiceImplTest {

    private static final long ORG_ID = 1L;

    private static final long PERSON_ID = 1L;

    private static final Long TEST_BILL_TO_ID = 2L;

    private static final long TEST_REQ_DOC_ID = 1L;

    @InjectMocks
    private RequiredDocumentServiceImpl sut;

    @Mock
    private RequiredDocumentDao requiredDocumentDao;

    @Before
    public void setUp() {
        SecurityTestUtils.login("USER", PERSON_ID, ORG_ID);
    }
    @After
    public void tierDown() {
        SecurityTestUtils.logout();
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testSaveRequiredDocuments() {
        ArrayList<RequiredDocumentEntity> requiredDocuments = new ArrayList<RequiredDocumentEntity>();
        RequiredDocumentEntity requiredDocument = new RequiredDocumentEntity();
        requiredDocument.setCustomerRequestType(DocRequestType.REQUIRED);

        requiredDocuments.add(requiredDocument);

        sut.saveRequiredDocuments(requiredDocuments, TEST_BILL_TO_ID);

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        Mockito.verify(requiredDocumentDao).saveOrUpdateBatch(argument.capture());
        List<RequiredDocumentEntity> argumentEntities = argument.getValue();
        Assert.assertTrue(checkEquality(requiredDocuments, argumentEntities));
    }

    @Test
    public void testGetRequiredDocumentsOfShipmentTypes() {
        Map<LoadDocumentTypeEntity, RequiredDocumentEntity> map = new HashMap<LoadDocumentTypeEntity, RequiredDocumentEntity>();
        LoadDocumentTypeEntity documentType = new LoadDocumentTypeEntity();
        documentType.setDocTypeString("TestType");
        RequiredDocumentEntity requiredDocument = new RequiredDocumentEntity();
        map.put(documentType, requiredDocument);

        Mockito.when(requiredDocumentDao.getDocumentsForShipmentTypes(TEST_BILL_TO_ID)).thenReturn(map);

        List<RequiredDocumentEntity> actualList = sut.getRequiredDocumentsOfShipmentTypes(TEST_BILL_TO_ID);
        Mockito.verify(requiredDocumentDao).getDocumentsForShipmentTypes(Matchers.eq(TEST_BILL_TO_ID));

        Assert.assertFalse(actualList.isEmpty());
        RequiredDocumentEntity actual = actualList.get(0);
        Assert.assertEquals(requiredDocument, actual);
        Assert.assertEquals(documentType, actual.getDocumentType());
    }

    @Test
    public void testGetRequiredDocument() {
        RequiredDocumentEntity expected = new RequiredDocumentEntity();
        Mockito.when(requiredDocumentDao.find(TEST_REQ_DOC_ID)).thenReturn(expected);

        RequiredDocumentEntity actual = sut.getRequiredDocument(TEST_REQ_DOC_ID);
        Mockito.verify(requiredDocumentDao).find(Matchers.eq(TEST_REQ_DOC_ID));

        Assert.assertEquals(expected, actual);
    }

    private boolean checkEquality(List<RequiredDocumentEntity> expected, List<RequiredDocumentEntity> actual) {
        if (!expected.equals(actual)) {
            return false;
        }

        Map<Long, RequiredDocumentEntity> map = new HashMap<Long, RequiredDocumentEntity>();
        for (RequiredDocumentEntity requiredDocument : actual) {
            map.put(requiredDocument.getId(), requiredDocument);
        }

        for (RequiredDocumentEntity requiredDocument : expected) {
            RequiredDocumentEntity actualRequiredDocument = map.get(requiredDocument.getId());
            if (actualRequiredDocument == null || actualRequiredDocument.getCustomerRequestType() != requiredDocument.getCustomerRequestType()) {
                return false;
            }

            if (actualRequiredDocument.getStatus() != Status.ACTIVE) {
                return false;
            }

            if (!TEST_BILL_TO_ID.equals(actualRequiredDocument.getBillTo().getId())) {
                return false;
            }
        }

        return true;
    }
}
