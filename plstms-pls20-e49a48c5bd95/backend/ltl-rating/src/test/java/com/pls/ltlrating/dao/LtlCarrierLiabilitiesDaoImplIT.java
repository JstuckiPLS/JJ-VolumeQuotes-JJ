package com.pls.ltlrating.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.ltlrating.dao.impl.LtlCarrierLiabilitiesDaoImpl;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;

/**
 * Test cases for {@link LtlCarrierLiabilitiesDaoImpl}.
 *
 * @author Artem Arapov
 *
 */
public class LtlCarrierLiabilitiesDaoImplIT extends AbstractDaoTest {

    private static final Long PROFILE_ID = 1L;
    private static final Long CREATED_BY = 1L;

    @Autowired
    private LtlCarrierLiabilitiesDao sut;

    @Test
    public void testFindByPrimaryKey() throws Exception {
        LtlCarrierLiabilitiesEntity result = sut.find(1L);

        assertNotNull(result);
        assertSame(1L, result.getId());
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
        LtlCarrierLiabilitiesEntity entity = createEntity();

        LtlCarrierLiabilitiesEntity afterSave = sut.saveOrUpdate(entity);

        assertNotNull(afterSave);
        assertNotNull(afterSave.getId());

        flushAndClearSession();

        LtlCarrierLiabilitiesEntity actualEntity = sut.get(afterSave.getId());

        assertNotNull(actualEntity);
        assertEntity(afterSave, actualEntity);
    }

    @Test
    public void testSaveList() throws Exception {
        List<LtlCarrierLiabilitiesEntity> list = new ArrayList<LtlCarrierLiabilitiesEntity>();
        list.add(createEntity());
        list.add(createEntity());

        List<LtlCarrierLiabilitiesEntity> afterSave = sut.saveCarrierLiabilitiesList(list);

        assertNotNull(afterSave);
        assertFalse(afterSave.isEmpty());

        for (LtlCarrierLiabilitiesEntity item : afterSave) {
            assertNotNull(item);
            assertNotNull(item.getId());
        }
    }

    @Test
    public void testFindByProfileId() throws Exception {
        List<LtlCarrierLiabilitiesEntity> result = sut.findCarrierLiabilitiesByProfileId(PROFILE_ID);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testDeleteLiabilities() throws Exception {
        sut.deleteLiabilities(PROFILE_ID);

        List<LtlCarrierLiabilitiesEntity> result = sut.findCarrierLiabilitiesByProfileId(PROFILE_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private LtlCarrierLiabilitiesEntity createEntity() {
        LtlCarrierLiabilitiesEntity entity = new LtlCarrierLiabilitiesEntity();

        entity.setPricingProfileId(PROFILE_ID);
        entity.setFreightClass(CommodityClass.CLASS_100);
        entity.setMaxNewProdLiabAmt(new BigDecimal("1.00"));
        entity.setMaxUsedProdLiabAmt(new BigDecimal("1.00"));
        entity.setNewProdLiabAmt(new BigDecimal("1.00"));
        entity.setUsedProdLiabAmt(new BigDecimal("1.00"));
        entity.getModification().setCreatedBy(CREATED_BY);

        return entity;
    }

    private void assertEntity(LtlCarrierLiabilitiesEntity expected, LtlCarrierLiabilitiesEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getPricingProfileId(), actual.getPricingProfileId());
        assertEquals(expected.getFreightClass(), actual.getFreightClass());
        assertEquals(expected.getMaxNewProdLiabAmt(), actual.getMaxNewProdLiabAmt());
        assertEquals(expected.getMaxUsedProdLiabAmt(), actual.getMaxUsedProdLiabAmt());
        assertEquals(expected.getNewProdLiabAmt(), actual.getNewProdLiabAmt());
        assertEquals(expected.getUsedProdLiabAmt(), actual.getUsedProdLiabAmt());
    }
}
