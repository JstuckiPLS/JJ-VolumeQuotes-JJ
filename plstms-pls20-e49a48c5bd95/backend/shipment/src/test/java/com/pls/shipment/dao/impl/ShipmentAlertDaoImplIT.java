package com.pls.shipment.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.shipment.dao.ShipmentAlertDao;
import com.pls.shipment.domain.ShipmentAlertEntity;
import com.pls.shipment.domain.bo.ShipmentAlertType;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardAlertListItemBO;
import com.pls.shipment.domain.enums.ShipmentAlertsStatus;

/**
 * Tests for {@link ShipmentAlertDaoImpl}.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class ShipmentAlertDaoImplIT extends AbstractDaoTest {

    private static final Long ACCOUNT_EXECUTIVE_ID = 1L;

    private static final Long SHIPMENT_ID = 2L;

    private static final long ALERT_ID = 1L;

    @Autowired
    @Qualifier("shipmentAlertDaoImpl")
    private ShipmentAlertDao dao;

    @Test
    public void shouldGetAlertsCount() {
        Long alertsCount = dao.getAlertsCount(ACCOUNT_EXECUTIVE_ID, ShipmentAlertsStatus.ACTIVE);
        assertTrue(alertsCount > 0);
    }

    @Test
    public void shouldGetAlertsCountZero() {
        Long alertsCount = dao.getAlertsCount(-1L, ShipmentAlertsStatus.ACTIVE);
        assertEquals(alertsCount, Long.valueOf(0));
    }

    @Test
    public void shouldAcknowledgeAlerts() {
        dao.acknowledgeAlerts(SHIPMENT_ID, ACCOUNT_EXECUTIVE_ID);
        flushAndClearSession();

        @SuppressWarnings("unchecked") List<ShipmentAlertEntity> actualAlerts =
                getSession().createQuery("from ShipmentAlertEntity where load.id=:id").setParameter("id", SHIPMENT_ID).list();

        for (ShipmentAlertEntity actualAlert : actualAlerts) {
            assertEquals(actualAlert.getStatus(), ShipmentAlertsStatus.ACKNOWLEDGED);
            assertEquals(actualAlert.getAcknowledgedUser().getId(), ACCOUNT_EXECUTIVE_ID);
        }
    }

    @Test
    public void shouldFindAlertsByShipment() {
        List<ShipmentAlertEntity> alertsByShipment = dao.findAlertsByShipment(Arrays.asList(SHIPMENT_ID));
        assertFalse(alertsByShipment.isEmpty());
        for (ShipmentAlertEntity shipmentAlertEntity : alertsByShipment) {
            assertEquals(SHIPMENT_ID, shipmentAlertEntity.getLoad().getId());
        }
    }

    @Test
    public void shouldFindAlertsByShipmentEmpty() {
        List<ShipmentAlertEntity> alertsByShipment = dao.findAlertsByShipment(new ArrayList<Long>());
        assertTrue(alertsByShipment.isEmpty());
    }

    @Test
    public void shouldGetAlertsForUser() {
        ShipmentAlertsStatus status = ShipmentAlertsStatus.ACTIVE;
        List<ShipmentTrackingBoardAlertListItemBO> alerts = dao.getAlertsForUser(ACCOUNT_EXECUTIVE_ID, status);
        assertFalse(alerts.isEmpty());
        for (ShipmentTrackingBoardAlertListItemBO shipmentAlertEntity : alerts) {
            assertTrue(shipmentAlertEntity.isNewAlert());
        }
    }

    @Test
    public void shouldGetAlertsForUserEmpty() {
        List<ShipmentTrackingBoardAlertListItemBO> alerts = dao.getAlertsForUser(-1L, ShipmentAlertsStatus.ACTIVE);
        assertTrue(alerts.isEmpty());
    }

    @Test
    public void shouldGetByStatus() {
        List<ShipmentAlertEntity> alerts = dao.getByStatus(ShipmentAlertsStatus.ACTIVE, ShipmentAlertsStatus.ACKNOWLEDGED);
        assertFalse(alerts.isEmpty());
        for (ShipmentAlertEntity shipmentAlertEntity : alerts) {
            assertTrue(ShipmentAlertsStatus.ACTIVE == shipmentAlertEntity.getStatus()
                    || ShipmentAlertsStatus.ACKNOWLEDGED == shipmentAlertEntity.getStatus());
        }
    }

    @Test
    public void shouldGetByStatusEmpty() {
        List<ShipmentAlertEntity> alerts = dao.getByStatus();
        assertTrue(alerts.isEmpty());
    }

    @Test
    public void shouldUpdateStatus() {
        dao.updateStatus(Arrays.asList(SHIPMENT_ID), ShipmentAlertsStatus.INACTIVE);
        flushAndClearSession();

        @SuppressWarnings("unchecked") List<ShipmentAlertEntity> actualAlerts =
                getSession().createQuery("from ShipmentAlertEntity where load.id=:id").setParameter("id", ALERT_ID).list();

        for (ShipmentAlertEntity actualAlert : actualAlerts) {
            assertEquals(actualAlert.getStatus(), ShipmentAlertsStatus.INACTIVE);
        }
    }

    @Test
    public void shouldGenerateTimeAlerts() {
        executeScript("prepareAlerts.sql");

        dao.removeOutdatedTimeAlerts();
        dao.generateNewTimeAlerts();

        List<ShipmentAlertEntity> shipmentAlerts = loadAll(ShipmentAlertEntity.class);
        assertNotNull(shipmentAlerts);
        assertEquals(4, shipmentAlerts.size());
        for (ShipmentAlertEntity alert : shipmentAlerts) {
            if (alert.getLoadId() == 302) {
                assertEquals(ShipmentAlertType.THIRTY_MIN_TO_PICKUP, alert.getType());
            } else if (alert.getLoadId() == 1) {
                assertEquals(ShipmentAlertType.PICKUP_TODAY, alert.getType());
            } else if (alert.getLoadId() == 3) {
                assertEquals(ShipmentAlertType.MISSED_PICKUP, alert.getType());
            } else if (alert.getLoadId() == 4) {
                assertEquals(ShipmentAlertType.MISSED_DELIVERY, alert.getType());
            } else {
                fail();
            }
        }
    }

    @Test
    public void shouldGetAlertsCountWithActiveOrInActiveOrganization() {
        updateOrganizationStatus(1L, true);
        long alertsCount = dao.getAlertsCount(ACCOUNT_EXECUTIVE_ID, ShipmentAlertsStatus.ACTIVE);
        Assert.assertNotNull(alertsCount);
        Assert.assertEquals(4, alertsCount);
        updateOrganizationStatus(1L, false);
        alertsCount = dao.getAlertsCount(ACCOUNT_EXECUTIVE_ID, ShipmentAlertsStatus.ACTIVE);
        Assert.assertNotNull(alertsCount);
        Assert.assertEquals(4, alertsCount);
    }
}
