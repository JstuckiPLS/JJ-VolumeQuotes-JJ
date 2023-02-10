package com.pls.shipment.domain;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;

/**
 * Test cases for {@link LoadDetailsEntity} class.
 * 
 * @author Aleksandr Leshchenko
 */
public class LoadDetailsEntityTest {
    @Test
    public void shouldCreateValidOriginLoadDetails() {
        LoadDetailsEntity entity = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        Assert.assertEquals(LoadAction.PICKUP, entity.getLoadAction());
        Assert.assertEquals(PointType.ORIGIN, entity.getPointType());
        Assert.assertEquals("SEQ_IN_ROUTE column should be set to 0 for origin", 0, entity.getSeqInRoute());
    }

    @Test
    public void shouldCreateValidDestinationLoadDetails() {
        LoadDetailsEntity entity = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        Assert.assertEquals(LoadAction.DELIVERY, entity.getLoadAction());
        Assert.assertEquals(PointType.DESTINATION, entity.getPointType());
        Assert.assertEquals("SEQ_IN_ROUTE column should be set to 1 for destination", 1, entity.getSeqInRoute());
    }
}
