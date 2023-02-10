package com.pls.shipment.service;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.BaseServiceITClass;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.exception.EntityNotFoundException;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LoadNotificationsEntity;
import com.pls.shipment.domain.LtlLoadAccessorialEntity;

/**
 * Integration test for Shipment Service.
 *
 * @author Stas Norochevskiy
 *
 */
public class ShipmentServiceImplTestIT extends BaseServiceITClass {
    @Autowired
    private ShipmentService shipmentService;

    @Before
    public void setUp() throws Exception {
        SecurityTestUtils.login("test", 2L, 1L);
    }

    @Test
    public void testCopyShipment() {
        LoadEntity origShipment = shipmentService.findById(1L);
        LoadEntity copyOfShipment = shipmentService.getCopyOfShipment(1L);

        // Assert LoadEntity properties
        Set<LtlLoadAccessorialEntity> originShipmentAccessorials = origShipment.getLtlAccessorials();
        Set<LtlLoadAccessorialEntity> copyShipmentAccessorials = copyOfShipment.getLtlAccessorials();

        Assert.assertEquals(originShipmentAccessorials.size(), copyShipmentAccessorials.size());
        for (LtlLoadAccessorialEntity accessorial : originShipmentAccessorials) {
            Assert.assertTrue(copyShipmentAccessorials.contains(accessorial));
        }

        // Assert LoadEntity notifications
        Set<LoadNotificationsEntity> originShipmentNotification = origShipment.getLoadNotifications();
        Set<LoadNotificationsEntity> copyShipmentNotification = copyOfShipment.getLoadNotifications();

        Assert.assertEquals(originShipmentNotification, copyShipmentNotification);
        for (LoadNotificationsEntity loadNotificationsEntity : originShipmentNotification) {
            Assert.assertTrue(copyShipmentNotification.contains(loadNotificationsEntity));
        }

        //Assert LoadEntity properties
        Assert.assertNull(copyOfShipment.getId());
        Assert.assertNull(copyOfShipment.getCarrier());
        Assert.assertEquals(origShipment.getOrganization().getId(), copyOfShipment.getOrganization().getId());
        Assert.assertEquals(origShipment.getRoute(), copyOfShipment.getRoute());
        Assert.assertNull(copyOfShipment.getActiveCostDetails());
        Assert.assertNull(copyOfShipment.getCostDetails());
        Assert.assertNull(copyOfShipment.getMileage());
        Assert.assertNull(copyOfShipment.getNumbers().getBolNumber());
        Assert.assertNull(copyOfShipment.getNumbers().getProNumber());
        Assert.assertNull(copyOfShipment.getNumbers().getPoNumber());
        Assert.assertNull(copyOfShipment.getNumbers().getPuNumber());
        Assert.assertNull(copyOfShipment.getNumbers().getRefNumber());
        Assert.assertEquals(ShipmentStatus.OPEN, copyOfShipment.getStatus());
        Assert.assertEquals(origShipment.getCommodity(), copyOfShipment.getCommodity());
        Assert.assertEquals(origShipment.getContainer(), copyOfShipment.getContainer());
        Assert.assertEquals(origShipment.getPieces(), copyOfShipment.getPieces());
        Assert.assertEquals(origShipment.getWeight(), copyOfShipment.getWeight());

        //Assert load details
        Assert.assertEquals(origShipment.getLoadDetails().size(), copyOfShipment.getLoadDetails().size());
    }

    @Test
    public void shouldReadShipmentWithDepricatedSavedQuote() {
        Long shipmentId = 2L;
        Long quoteId = 10L;

        getSession().createQuery("update SavedQuoteEntity set status='I',modification.createdDate=:newCreatedDate where id=:quoteId")
                .setParameter("quoteId", quoteId).setParameter("newCreatedDate", DateUtils.addDays(new Date(), -100)).executeUpdate();
        getSession().createQuery("update LoadEntity set savedQuoteId=:quoteId where id=:shipmentId").setParameter("quoteId", quoteId)
                .setParameter("shipmentId", shipmentId).executeUpdate();

        LoadEntity shipment = shipmentService.findById(shipmentId);

        Assert.assertNotNull(shipment);
        Assert.assertNotNull(shipment.getSavedQuote());
        Assert.assertEquals(quoteId, shipment.getSavedQuote().getId());
    }

    @Test
    public void shouldCloseLoad() throws EntityNotFoundException {
        Long shipmentId = 1L;

        LoadEntity shipment = shipmentService.findById(shipmentId);
        Assert.assertNotNull(shipment);
        Assert.assertNotNull(shipment.getStatus());
        Assert.assertEquals(shipment.getStatus(), ShipmentStatus.DELIVERED);

        shipmentService.closeLoad(shipmentId);
        getSession().refresh(shipment);

        Assert.assertNotNull(shipment);
        Assert.assertNotNull(shipment.getStatus());
        Assert.assertEquals(shipment.getStatus(), ShipmentStatus.CANCELLED);
    }
}