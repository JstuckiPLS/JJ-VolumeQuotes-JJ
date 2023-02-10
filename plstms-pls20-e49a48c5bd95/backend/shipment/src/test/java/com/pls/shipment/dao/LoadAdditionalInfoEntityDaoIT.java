package com.pls.shipment.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.shipment.domain.LoadAdditionalInfoEntity;

/**
 * Test class for {@link LoadAdditionalInfoEntityDao}.
 * 
 * @author Sergii Belodon
 */
public class LoadAdditionalInfoEntityDaoIT extends AbstractDaoTest {
    @Autowired
    private LoadAdditionalInfoEntityDao loadAdditionalInfoEntityDao;

    @Test
    public void testUpdateMarkup() throws EntityNotFoundException {
        LoadAdditionalInfoEntity entity = loadAdditionalInfoEntityDao.get(1L);
        entity.setMarkup(10L);
        loadAdditionalInfoEntityDao.saveOrUpdate(entity);
        LoadAdditionalInfoEntity entityUpdated = loadAdditionalInfoEntityDao.get(1L);
        assertEquals(10L, (long) entityUpdated.getMarkup());
    }
}
