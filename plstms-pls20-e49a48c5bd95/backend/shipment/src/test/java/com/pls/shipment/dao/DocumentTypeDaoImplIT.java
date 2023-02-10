package com.pls.shipment.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.document.DocumentTypeEntity;
import com.pls.core.domain.document.LoadDocumentTypeEntity;

/**
 * Test cases for {@link com.pls.shipment.dao.impl.DocumentTypeDaoImpl} class.
 * 
 * @author Alexander Nalapko
 *
 */
public class DocumentTypeDaoImplIT extends AbstractDaoTest {

    public static final String BOL_DOC_TYPE = "BOL";

    public static final Long BOL_DOC_TYPE_ID = 1L;

    @Autowired
    private DocumentTypeDao dao;

    @Test
    public void testGetDocumentType() {
        List<DocumentTypeEntity> list =  dao.getAll();
        assertNotNull(list);
        assertTrue(!list.isEmpty());
    }

    @Test
    public void testAllDocumentTypes() {
        List<DocumentTypeEntity> list =  dao.getAll();
        assertNotNull(list);
        assertTrue(!list.isEmpty());

        String[] expectedTypes = {"BOL", "VENDOR BILL", "UNKNOWN", "POD", "W_AND_I", "CLAIMS"};
        List<String> actualTypes = new ArrayList<String>();
        for (DocumentTypeEntity documnetTypeEntity : list) {
            actualTypes.add(documnetTypeEntity.getDocTypeString());
        }

        for (String expectedType : expectedTypes) {
            assertTrue("Document type " + expectedType + " was not fetched from DB",
                    actualTypes.contains(expectedType));
        }
    }

    @Test
    public void testFindByDocTypeString() {
        LoadDocumentTypeEntity documentTypeEntity = dao.findByDocTypeString(BOL_DOC_TYPE, LoadDocumentTypeEntity.class);
        assertNotNull(documentTypeEntity);
        assertEquals(BOL_DOC_TYPE_ID, documentTypeEntity.getId());
    }

    @Test
    public void testGetLoadDocumentType() {
        List<LoadDocumentTypeEntity> list = dao.getLoadDocumentType();
        assertNotNull(list);
        assertTrue(!list.isEmpty());
    }
}
