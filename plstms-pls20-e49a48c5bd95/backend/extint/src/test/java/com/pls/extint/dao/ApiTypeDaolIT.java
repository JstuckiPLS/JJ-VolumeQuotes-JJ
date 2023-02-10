package com.pls.extint.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.extint.domain.ApiTypeEntity;

/**
 * Test for {@link ApiTypeDao}.
 * 
 * @author Aleksandr Leshchenko
 */
public class ApiTypeDaolIT extends AbstractDaoTest {
    @Autowired
    private ApiTypeDao dao;

    @Test
    // TODO add few API_TYPES for testing and fix this test
    public void shouldFindDocumentApiTypesForLoad() {
        List<ApiTypeEntity> documentApiTypes = dao.findDocumentApiTypesForLoad(0L, 1L, 2L, "CARRIER");
        assertNotNull(documentApiTypes);
        assertEquals(0, documentApiTypes.size());
    }
}
