package com.pls.shipment.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentAlertDao;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardAlertListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardBookedListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardListItemBO;
import com.pls.shipment.domain.enums.ShipmentAlertsStatus;
import com.pls.shipment.service.impl.ShipmentTrackingBoardServiceImpl;

/**
 * Tests for {@link com.pls.shipment.service.impl.ShipmentTrackingBoardServiceImpl}.
 * 
 * @author Viacheslav Krot
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTrackingBoardServiceImplTest {

    private static final long SHIPMENT_ID = 1L;

    private static final Long PERSON_ID = 1L;

    private static final long ORG_ID = 1L;

    private static final String USERNAME = "SYSADMIN";

    private static final Long COUNT_OF_ACTIVE_ALERTS = 10L;

    @Mock
    private LtlShipmentDao shipmentDao;
    @Mock
    private ShipmentAlertDao shipmentAlertDao;

    @InjectMocks
    private ShipmentTrackingBoardServiceImpl service;

    @Test
    public void shouldUndeliveredAllShipments() {
        List<ShipmentTrackingBoardListItemBO> expected = Collections.unmodifiableList(new ArrayList<ShipmentTrackingBoardListItemBO>());
        Mockito.when(shipmentDao.findUndeliveredShipments()).thenReturn(expected);

        List<ShipmentTrackingBoardListItemBO> result = service.getUndeliveredShipments();

        Assert.assertSame(expected, result);
        Mockito.verify(shipmentDao).findUndeliveredShipments();
    }

    @Test
    public void shouldGetBookedShipments() {

        List<ShipmentTrackingBoardBookedListItemBO> expected = Collections.unmodifiableList(new ArrayList<ShipmentTrackingBoardBookedListItemBO>());
        Mockito.when(shipmentDao.findBookedShipments(PERSON_ID)).thenReturn(expected);

        List<ShipmentTrackingBoardBookedListItemBO> result = service.getBookedShipments(PERSON_ID);

        Assert.assertSame(expected, result);
        Mockito.verify(shipmentDao).findBookedShipments(PERSON_ID);
    }

    @Test
    public void shouldGetAlertShipments() {
        SecurityTestUtils.login(USERNAME, PERSON_ID, ORG_ID);

        ShipmentAlertsStatus shipmentAlertsStatus1 = ShipmentAlertsStatus.ACTIVE;
        ShipmentAlertsStatus shipmentAlertsStatus2 = ShipmentAlertsStatus.ACKNOWLEDGED;

        ArrayList<ShipmentTrackingBoardAlertListItemBO> list = new ArrayList<ShipmentTrackingBoardAlertListItemBO>();
        list.add(new ShipmentTrackingBoardAlertListItemBO());
        Mockito.when(shipmentAlertDao.getAlertsForUser(PERSON_ID, shipmentAlertsStatus1, shipmentAlertsStatus2))
                .thenReturn(Collections.unmodifiableList(list));

        List<ShipmentTrackingBoardAlertListItemBO> alertShipments = service.getAlertShipments();
        Mockito.verify(shipmentAlertDao).getAlertsForUser(PERSON_ID, shipmentAlertsStatus1, shipmentAlertsStatus2);
        Assert.assertEquals(list.size(), alertShipments.size());
    }

    @Test
    public void shouldAcknowledgeAlerts() {
        SecurityTestUtils.login(USERNAME, PERSON_ID, ORG_ID);
        service.acknowledgeAlerts(SHIPMENT_ID);
        Mockito.verify(shipmentAlertDao).acknowledgeAlerts(SHIPMENT_ID, PERSON_ID);
    }

    @Test
    public void shouldCountOfActiveAlerts() {
        SecurityTestUtils.login(USERNAME, PERSON_ID, ORG_ID);
        Mockito.when(shipmentAlertDao.getAlertsCount(PERSON_ID, ShipmentAlertsStatus.ACTIVE)).thenReturn(
                COUNT_OF_ACTIVE_ALERTS);

        Long count = service.countOfActiveAlerts();
        Mockito.verify(shipmentAlertDao).getAlertsCount(PERSON_ID, ShipmentAlertsStatus.ACTIVE);
        Assert.assertEquals(COUNT_OF_ACTIVE_ALERTS, count);
    }
}
