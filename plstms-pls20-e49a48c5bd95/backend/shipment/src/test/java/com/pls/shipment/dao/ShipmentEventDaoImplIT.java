package com.pls.shipment.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.shipment.domain.LoadEventDataEntity;
import com.pls.shipment.domain.LoadEventDataPK;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.bo.ShipmentEventBO;

/**
 * Tests for ShipmentEventDaoImpl.
 * 
 * @author Stas Norochevskiy
 *
 */
public class ShipmentEventDaoImplIT extends AbstractDaoTest {

    // This is a load with status PP (Pending Pick Up)
    public static final Long TEST_LOAD_ID = 4910944L;

    @Autowired
    private ShipmentEventDao shipmentEventDao;

    @Test
    public void testFindShipmentEvents() throws ParseException {
        List<ShipmentEventBO> loadEvents = shipmentEventDao.findShipmentEvents(TEST_LOAD_ID);

        Assert.assertEquals(7, loadEvents.size());

        ShipmentEventBO event = getEventById(loadEvents, 4756090L, (byte) 0);
        Assert.assertNotNull(event);

        Assert.assertEquals(TEST_LOAD_ID, event.getLoadId());
        Assert.assertEquals("Load is manually set missed", event.getDescription());
        Assert.assertEquals("admin", event.getFirstName());
        Assert.assertEquals("sysadmin", event.getLastName());
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2010-01-12"), event.getCreatedDate());
        Assert.assertEquals("2010-01-12 07:00:00.0", event.getData());

        event = getEventById(loadEvents, 4756090L, (byte) 1);
        Assert.assertNotNull(event);

        Assert.assertEquals(TEST_LOAD_ID, event.getLoadId());
        Assert.assertEquals("Load is manually set missed", event.getDescription());
        Assert.assertEquals("admin", event.getFirstName());
        Assert.assertEquals("sysadmin", event.getLastName());
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2010-01-12"), event.getCreatedDate());
        Assert.assertEquals("TEST VALUE", event.getData());

        event = getEventById(loadEvents, 219L, (byte) 0);
        Assert.assertNotNull(event);

        Assert.assertEquals(TEST_LOAD_ID, event.getLoadId());
        Assert.assertEquals("Released from {0} to {1}", event.getDescription());
        Assert.assertEquals("admin", event.getFirstName());
        Assert.assertEquals("sysadmin", event.getLastName());
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2012-12-12"), event.getCreatedDate());
        Assert.assertEquals("2012-12-12 07:00:00.0", event.getData());

        event = getEventById(loadEvents, 219L, (byte) 1);
        Assert.assertNotNull(event);

        Assert.assertEquals(TEST_LOAD_ID, event.getLoadId());
        Assert.assertEquals("Released from {0} to {1}", event.getDescription());
        Assert.assertEquals("admin", event.getFirstName());
        Assert.assertEquals("sysadmin", event.getLastName());
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2012-12-12"), event.getCreatedDate());
        Assert.assertEquals("2012-12-12 12:00:00.0", event.getData());
    }

    private ShipmentEventBO getEventById(List<ShipmentEventBO> loadEvents, Long testLoadEventId, byte ordinal) {
        for (ShipmentEventBO bo : loadEvents) {
            if (bo.getEventId().equals(testLoadEventId) && bo.getOrdinal().equals(ordinal)) {
                return bo;
            }
        }
        return null;
    }

    @Test
    public void testCreate() throws ParseException {
        LoadEventEntity event = new LoadEventEntity();
        event.setLoadId(TEST_LOAD_ID);
        event.setEventTypeCode("FBH.MFB");
        event.setCreatedBy(1L);
        event.setFailure(false);

        LoadEventDataPK dataEntityPK = new LoadEventDataPK();
        dataEntityPK.setOrdinal((byte) 0);
        dataEntityPK.setEvent(event);

        LoadEventDataEntity dataEntity = new LoadEventDataEntity();
        dataEntity.setEventDataPK(dataEntityPK);
        dataEntity.setDataType('S');
        dataEntity.setData("TEST VALUE");
        event.setData(Collections.singletonList(dataEntity));

        event = shipmentEventDao.saveOrUpdate(event);
        Assert.assertNotNull("Event id is null", event.getId());
        Assert.assertNotNull("Event data ordinal is null", dataEntity.getEventDataPK().getOrdinal());
        Assert.assertEquals("Event data is not associated with event", event, dataEntity.getEventDataPK().getEvent());

    }
}
