package com.pls.shipment.domain;

import java.util.Date;

import org.hibernate.HibernateException;
import org.junit.Assert;
import org.junit.Test;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.service.impl.security.util.SecurityTestUtils;

/**
 * Test for hibernate mapping of {@link LoadEntity}.
 * 
 * @author Maxim Medvedev
 */
public class LoadEntityMappingIT extends AbstractDaoTest {

    private static final Long VALID_LOAD_ID = -100L;

    @Test(expected = HibernateException.class)
    public void testDestinationWithMultipleLoadDetailsRow() {
        // This is not normal situation and real DB does not have duplicates of LOAD_DETAIL_ID, LOAD_ID,
        // LOAD_ACTION and POINT_TYPE. At least one for LTL loads.
        executeScript("addLoadWithDuplicatedDetails.sql");

        LoadEntity result = getEntity(LoadEntity.class, VALID_LOAD_ID);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getDestination());
        Assert.assertEquals(Long.valueOf(-98), result.getDestination().getId());
        Assert.assertNotNull(result.getOrigin());
        Assert.assertEquals(Long.valueOf(-97), result.getOrigin().getId());
    }

    @Test
    public void testDestinationWithNormalCase() {
        executeScript("addLoadFull.sql");

        LoadEntity result = getEntity(LoadEntity.class, VALID_LOAD_ID);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getDestination());
        Assert.assertEquals(Long.valueOf(-100), result.getDestination().getId());
        Assert.assertNotNull(result.getOrigin());
        Assert.assertEquals(Long.valueOf(-99), result.getOrigin().getId());
    }

    @Test
    public void testDestinationWithoutLoadDetailsRow() {
        executeScript("addLoadWithoutDetails.sql");

        LoadEntity result = getEntity(LoadEntity.class, VALID_LOAD_ID);

        Assert.assertNotNull(result);
        Assert.assertNull(result.getDestination());
        Assert.assertNull(result.getOrigin());
    }

    @Test
    public void testDestinationWithUpdating() {
        SecurityTestUtils.logout();
        SecurityTestUtils.login("test");

        executeScript("addLoadFull.sql");

        LoadEntity entity = getEntity(LoadEntity.class, VALID_LOAD_ID);

        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getDestination());

        Date newDate = new Date();
        entity.getDestination().setScheduledArrival(newDate);
        entity.getDestination().setDeparture(newDate);
        save(entity);
        flushAndClearSession();

        LoadEntity result = getEntity(LoadEntity.class, VALID_LOAD_ID);
        Assert.assertNotSame(entity, result);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getDestination());
        Assert.assertEquals(newDate, entity.getDestination().getScheduledArrival());
        Assert.assertEquals(newDate, entity.getDestination().getDeparture());
    }

    @Test
    public void testOriginWithUpdating() {
        SecurityTestUtils.logout();
        SecurityTestUtils.login("test");

        executeScript("addLoadFull.sql");

        LoadEntity entity = getEntity(LoadEntity.class, VALID_LOAD_ID);

        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getDestination());

        Date newDate = new Date();
        entity.getOrigin().setScheduledArrival(newDate);
        entity.getOrigin().setDeparture(newDate);
        save(entity);
        flushAndClearSession();

        LoadEntity result = getEntity(LoadEntity.class, VALID_LOAD_ID);
        Assert.assertNotSame(entity, result);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getOrigin());
        Assert.assertEquals(newDate, entity.getOrigin().getScheduledArrival());
        Assert.assertEquals(newDate, entity.getOrigin().getDeparture());
    }

}
