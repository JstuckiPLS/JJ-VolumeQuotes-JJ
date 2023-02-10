package com.pls.ltlrating.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.ltlrating.domain.DotRegionFuelEntity;

/**
 * Test for {@link DotRegionFuelEntity}.
 * @author Stas Norochevskiy
 *
 */
public class DotRegionFuelDaoImplTestIT extends AbstractDaoTest {

    @Autowired
    private DotRegionFuelDao sut;

    @Test
    public void testGetCurrentRegionsFuels() {
        List<DotRegionFuelEntity> actualResults = sut.getActiveRegionRates();
        Assert.assertNotNull(actualResults);
    }

    private DotRegionFuelEntity getFirstDotRegionFuelEntity() {
        return (DotRegionFuelEntity) getSession().createQuery("from DotRegionFuelEntity order by id").setMaxResults(1).uniqueResult();
    }

    @Test
    public void testSaveAll() {
        DotRegionFuelEntity entity = getFirstDotRegionFuelEntity();
        entity.setFuelCharge(new BigDecimal("11230.123"));
        List<DotRegionFuelEntity> list = new ArrayList<DotRegionFuelEntity>();
        list.add(entity);
        sut.saveAll(list);

        entity = getFirstDotRegionFuelEntity();
        Assert.assertEquals(new BigDecimal("11230.123"), entity.getFuelCharge());
    }

    @Test
    public void testExpirate() {
        sut.expirateRates();

        List<DotRegionFuelEntity> actualResults = sut.getActiveRegionRates();
        Assert.assertNotNull(actualResults);
        Assert.assertTrue(actualResults.isEmpty());

    }
}
