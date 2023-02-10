package com.pls.shipment.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.enums.LoadAction;
import com.pls.core.domain.enums.PointType;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.domain.user.UserEntity;
import com.pls.core.shared.Status;
import com.pls.shipment.dao.ShipmentAlertDao;
import com.pls.shipment.domain.CostDetailItemEntity;
import com.pls.shipment.domain.LoadCostDetailsEntity;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.ShipmentAlertEntity;
import com.pls.shipment.domain.bo.ShipmentAlertType;
import com.pls.shipment.domain.enums.ShipmentAlertsStatus;
import com.pls.shipment.service.ShipmentService;

/**
 * Tests for {@link ShipmentAlertServiceImpl}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentAlertServiceImplTest {
    @InjectMocks
    private ShipmentAlertServiceImpl sut;

    @Mock
    private ShipmentAlertDao shipmentAlertDao;

    @Test
    public void shouldDeactivateAlerts() {
        LoadEntity shipment = new LoadEntity();
        shipment.setId(1L);

        sut.deactivateAlerts(shipment);

        Mockito.verify(shipmentAlertDao).removeAlerts(Matchers.eq(shipment.getId()));
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void shouldRemoveAlertsForCancelledShipment() {
        LoadEntity shipment = new LoadEntity();
        shipment.setStatus(ShipmentStatus.CANCELLED);
        shipment.setId(1L);

        sut.processShipmentAlerts(shipment);

        Mockito.verify(shipmentAlertDao).removeAlerts(Matchers.eq(shipment.getId()));
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void shouldNotGenerateAnyAlerts() {
        LoadEntity shipment = new LoadEntity();
        shipment.setId(1L);
        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        shipment.setCostDetails(activeCostDetails);
        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();
        CostDetailItemEntity costDetailItem = new CostDetailItemEntity();
        costDetailItem.setAccessorialType("not a guaranteed type");
        costDetailItems.add(costDetailItem);
        costDetails.setCostDetailItems(costDetailItems);
        activeCostDetails.add(costDetails);

        sut.processShipmentAlerts(shipment);

        verifyDeletedAlerts(shipment, 1);
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void shouldGenerateGuaranteedAlert() {
        LoadEntity shipment = new LoadEntity();
        shipment.setId(1L);
        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        shipment.setCostDetails(activeCostDetails);
        CustomerEntity organization = new CustomerEntity();
        organization.setId(2L);
        shipment.setOrganization(organization);
        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        costDetails.setGuaranteedBy(1200L);
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();
        CostDetailItemEntity costDetailItem = new CostDetailItemEntity();
        costDetailItem.setAccessorialType(ShipmentService.GUARANTEED_SERVICE_REF_TYPE);
        costDetailItems.add(costDetailItem);
        costDetails.setCostDetailItems(costDetailItems);
        activeCostDetails.add(costDetails);

        sut.processShipmentAlerts(shipment);

        Mockito.verify(shipmentAlertDao).getShipmentAlert(shipment.getId(), ShipmentAlertType.GUARANTEED_SERVICE);
        verifyDeletedAlerts(shipment, 1, ShipmentAlertType.GUARANTEED_SERVICE);
        ArgumentCaptor<ShipmentAlertEntity> captor = ArgumentCaptor.forClass(ShipmentAlertEntity.class);
        Mockito.verify(shipmentAlertDao).saveOrUpdate(captor.capture());
        ShipmentAlertEntity alert = captor.getValue();
        Assert.assertEquals(ShipmentAlertType.GUARANTEED_SERVICE, alert.getType());
        Assert.assertEquals(shipment.getId(), alert.getLoadId());
        Assert.assertEquals(organization.getId(), alert.getCustomerId());
        Assert.assertEquals(ShipmentAlertsStatus.ACTIVE, alert.getStatus());
        Assert.assertNull(alert.getAcknowledgedUser());
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void shouldGenerateMissedPickupDateAlert() {
        LoadEntity shipment = new LoadEntity();
        shipment.setId(1L);
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        Date date = new Date();
        origin.setEarlyScheduledArrival(date);
        origin.setScheduledArrival(DateUtils.addMinutes(date, 31));
        shipment.addLoadDetails(origin);
        shipment.setStatus(ShipmentStatus.DELIVERED);

        CustomerEntity organization = new CustomerEntity();
        organization.setId(2L);
        shipment.setOrganization(organization);
        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        shipment.setCostDetails(activeCostDetails);
        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();
        CostDetailItemEntity costDetailItem = new CostDetailItemEntity();
        costDetailItem.setAccessorialType(Math.random() + "");
        costDetailItems.add(costDetailItem);
        costDetails.setCostDetailItems(costDetailItems);
        activeCostDetails.add(costDetails);

        sut.processShipmentAlerts(shipment);

        Mockito.verify(shipmentAlertDao).getShipmentAlert(shipment.getId(), ShipmentAlertType.DELIVERY_DATE_WO_PICKUP_DATE);
        verifyDeletedAlerts(shipment, 1, ShipmentAlertType.DELIVERY_DATE_WO_PICKUP_DATE);
        ArgumentCaptor<ShipmentAlertEntity> captor = ArgumentCaptor.forClass(ShipmentAlertEntity.class);
        Mockito.verify(shipmentAlertDao).saveOrUpdate(captor.capture());
        ShipmentAlertEntity alert = captor.getValue();
        Assert.assertEquals(ShipmentAlertType.DELIVERY_DATE_WO_PICKUP_DATE, alert.getType());
        Assert.assertEquals(shipment.getId(), alert.getLoadId());
        Assert.assertEquals(organization.getId(), alert.getCustomerId());
        Assert.assertEquals(ShipmentAlertsStatus.ACTIVE, alert.getStatus());
        Assert.assertNull(alert.getAcknowledgedUser());
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void shouldGenerate30MinutesAlert() {
        LoadEntity shipment = new LoadEntity();
        shipment.setId(1L);
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        Date date = new Date();
        origin.setEarlyScheduledArrival(date);
        origin.setScheduledArrival(DateUtils.addMinutes(date, 1));
        shipment.addLoadDetails(origin);
        shipment.setStatus(ShipmentStatus.BOOKED);

        CustomerEntity organization = new CustomerEntity();
        organization.setId(2L);
        shipment.setOrganization(organization);
        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        shipment.setCostDetails(activeCostDetails);
        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();
        costDetails.setCostDetailItems(costDetailItems);
        activeCostDetails.add(costDetails);

        sut.processShipmentAlerts(shipment);

        Mockito.verify(shipmentAlertDao).getShipmentAlert(shipment.getId(), ShipmentAlertType.THIRTY_MIN_TO_PICKUP);
        verifyDeletedAlerts(shipment, 1, ShipmentAlertType.THIRTY_MIN_TO_PICKUP);
        ArgumentCaptor<ShipmentAlertEntity> captor = ArgumentCaptor.forClass(ShipmentAlertEntity.class);
        Mockito.verify(shipmentAlertDao).saveOrUpdate(captor.capture());
        ShipmentAlertEntity alert = captor.getValue();
        Assert.assertEquals(ShipmentAlertType.THIRTY_MIN_TO_PICKUP, alert.getType());
        Assert.assertEquals(shipment.getId(), alert.getLoadId());
        Assert.assertEquals(organization.getId(), alert.getCustomerId());
        Assert.assertEquals(ShipmentAlertsStatus.ACTIVE, alert.getStatus());
        Assert.assertNull(alert.getAcknowledgedUser());
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void shouldGeneratePickupTodayAlert() {
        LoadEntity shipment = new LoadEntity();
        shipment.setId(1L);
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        Date date = new Date();
        origin.setEarlyScheduledArrival(date);
        origin.setScheduledArrival(DateUtils.addMinutes(date, 31));
        shipment.addLoadDetails(origin);
        shipment.setStatus(ShipmentStatus.BOOKED);

        CustomerEntity organization = new CustomerEntity();
        organization.setId(2L);
        shipment.setOrganization(organization);
        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        shipment.setCostDetails(activeCostDetails);
        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();
        costDetails.setCostDetailItems(costDetailItems);
        activeCostDetails.add(costDetails);

        sut.processShipmentAlerts(shipment);

        Mockito.verify(shipmentAlertDao).getShipmentAlert(shipment.getId(), ShipmentAlertType.PICKUP_TODAY);
        verifyDeletedAlerts(shipment, 1, ShipmentAlertType.PICKUP_TODAY);
        ArgumentCaptor<ShipmentAlertEntity> captor = ArgumentCaptor.forClass(ShipmentAlertEntity.class);
        Mockito.verify(shipmentAlertDao).saveOrUpdate(captor.capture());
        ShipmentAlertEntity alert = captor.getValue();
        Assert.assertEquals(ShipmentAlertType.PICKUP_TODAY, alert.getType());
        Assert.assertEquals(shipment.getId(), alert.getLoadId());
        Assert.assertEquals(organization.getId(), alert.getCustomerId());
        Assert.assertEquals(ShipmentAlertsStatus.ACTIVE, alert.getStatus());
        Assert.assertNull(alert.getAcknowledgedUser());
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void shouldGenerateMissedPickupAlert() {
        LoadEntity shipment = new LoadEntity();
        shipment.setId(1L);
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        Date date = new Date();
        origin.setEarlyScheduledArrival(DateUtils.addMinutes(date, -2));
        origin.setScheduledArrival(DateUtils.addMinutes(date, -1));
        shipment.addLoadDetails(origin);
        shipment.setStatus(ShipmentStatus.DISPATCHED);

        CustomerEntity organization = new CustomerEntity();
        organization.setId(2L);
        shipment.setOrganization(organization);
        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        shipment.setCostDetails(activeCostDetails);
        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();
        costDetails.setCostDetailItems(costDetailItems);
        activeCostDetails.add(costDetails);

        sut.processShipmentAlerts(shipment);

        Mockito.verify(shipmentAlertDao).getShipmentAlert(shipment.getId(), ShipmentAlertType.MISSED_PICKUP);
        verifyDeletedAlerts(shipment, 1, ShipmentAlertType.MISSED_PICKUP);
        ArgumentCaptor<ShipmentAlertEntity> captor = ArgumentCaptor.forClass(ShipmentAlertEntity.class);
        Mockito.verify(shipmentAlertDao).saveOrUpdate(captor.capture());
        ShipmentAlertEntity alert = captor.getValue();
        Assert.assertEquals(ShipmentAlertType.MISSED_PICKUP, alert.getType());
        Assert.assertEquals(shipment.getId(), alert.getLoadId());
        Assert.assertEquals(organization.getId(), alert.getCustomerId());
        Assert.assertEquals(ShipmentAlertsStatus.ACTIVE, alert.getStatus());
        Assert.assertNull(alert.getAcknowledgedUser());
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void shouldGenerateMissedDeliveryAlert() {
        LoadEntity shipment = new LoadEntity();
        shipment.setId(1L);
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        Date date = new Date();
        origin.setEarlyScheduledArrival(DateUtils.addMinutes(date, -2));
        origin.setScheduledArrival(DateUtils.addMinutes(date, -1));
        origin.setDeparture(date);
        shipment.addLoadDetails(origin);
        CustomerEntity organization = new CustomerEntity();
        organization.setId(2L);
        shipment.setOrganization(organization);
        LoadDetailsEntity destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        destination.setEarlyScheduledArrival(DateUtils.addMinutes(date, -2));
        destination.setScheduledArrival(DateUtils.addMinutes(date, -1));
        shipment.addLoadDetails(destination);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);

        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        shipment.setCostDetails(activeCostDetails);
        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();
        costDetails.setCostDetailItems(costDetailItems);
        activeCostDetails.add(costDetails);

        sut.processShipmentAlerts(shipment);

        Mockito.verify(shipmentAlertDao).getShipmentAlert(shipment.getId(), ShipmentAlertType.MISSED_DELIVERY);
        verifyDeletedAlerts(shipment, 1, ShipmentAlertType.MISSED_DELIVERY);
        ArgumentCaptor<ShipmentAlertEntity> captor = ArgumentCaptor.forClass(ShipmentAlertEntity.class);
        Mockito.verify(shipmentAlertDao).saveOrUpdate(captor.capture());
        ShipmentAlertEntity alert = captor.getValue();
        Assert.assertEquals(ShipmentAlertType.MISSED_DELIVERY, alert.getType());
        Assert.assertEquals(shipment.getId(), alert.getLoadId());
        Assert.assertEquals(organization.getId(), alert.getCustomerId());
        Assert.assertEquals(ShipmentAlertsStatus.ACTIVE, alert.getStatus());
        Assert.assertNull(alert.getAcknowledgedUser());
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void shouldUpdateMissedDeliveryAlert() {
        LoadEntity shipment = new LoadEntity();
        shipment.setId(1L);
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        Date date = new Date();
        origin.setEarlyScheduledArrival(DateUtils.addMinutes(date, -2));
        origin.setScheduledArrival(DateUtils.addMinutes(date, -1));
        origin.setDeparture(date);
        shipment.addLoadDetails(origin);
        CustomerEntity organization = new CustomerEntity();
        organization.setId(2L);
        shipment.setOrganization(organization);
        LoadDetailsEntity destination = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        destination.setEarlyScheduledArrival(DateUtils.addMinutes(date, -2));
        destination.setScheduledArrival(DateUtils.addMinutes(date, -1));
        shipment.addLoadDetails(destination);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);

        Set<LoadCostDetailsEntity> activeCostDetails = new HashSet<LoadCostDetailsEntity>();
        shipment.setCostDetails(activeCostDetails);
        LoadCostDetailsEntity costDetails = new LoadCostDetailsEntity();
        costDetails.setStatus(Status.ACTIVE);
        Set<CostDetailItemEntity> costDetailItems = new HashSet<CostDetailItemEntity>();
        costDetails.setCostDetailItems(costDetailItems);
        activeCostDetails.add(costDetails);

        ShipmentAlertEntity shipmentAlert = new ShipmentAlertEntity();
        shipmentAlert.setStatus(ShipmentAlertsStatus.ACKNOWLEDGED);
        shipmentAlert.setAcknowledgedUser(new UserEntity());
        Mockito.when(shipmentAlertDao.getShipmentAlert(shipment.getId(), ShipmentAlertType.MISSED_DELIVERY)).thenReturn(shipmentAlert);

        sut.processShipmentAlerts(shipment);

        Mockito.verify(shipmentAlertDao).getShipmentAlert(shipment.getId(), ShipmentAlertType.MISSED_DELIVERY);
        verifyDeletedAlerts(shipment, 1, ShipmentAlertType.MISSED_DELIVERY);
        ArgumentCaptor<ShipmentAlertEntity> captor = ArgumentCaptor.forClass(ShipmentAlertEntity.class);
        Mockito.verify(shipmentAlertDao).update(captor.capture());
        ShipmentAlertEntity alert = captor.getValue();
        Assert.assertEquals(ShipmentAlertsStatus.ACTIVE, alert.getStatus());
        Assert.assertNull(alert.getAcknowledgedUser());
        Mockito.verifyNoMoreInteractions(shipmentAlertDao);
    }

    @Test
    public void testAlerts30MinToPickup() throws ParseException {
        LoadEntity load = createLoad();
        load.setStatus(ShipmentStatus.OPEN);
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        Date date = DateUtils.addMinutes(new Date(), 29);
        origin.setEarlyScheduledArrival(date);
        origin.setScheduledArrival(date);
        origin.setDeparture(null);
        load.addLoadDetails(origin);

        assertTrue(sut.is30MinLeftToPickup(load));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testAlertsPickupToday() throws ParseException {
        // skip this test if less than 32 min to end of this day left
        Date now = new Date();
        if (DateUtils.addMinutes(now, 32).getDay() != now.getDay()) {
            return;
        }

        LoadEntity load = createLoad();
        load.setStatus(ShipmentStatus.OPEN);
        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        Date date = DateUtils.addMinutes(new Date(), 31);
        origin.setEarlyScheduledArrival(date);
        origin.setDeparture(null);
        origin.setScheduledArrival(date);
        load.addLoadDetails(origin);

        assertTrue(sut.isPickupToday(load));

        // test the same but with different status
        load.setStatus(ShipmentStatus.BOOKED);
        load.addLoadDetails(origin);

        assertTrue(sut.isPickupToday(load));

        load.setStatus(ShipmentStatus.DISPATCHED);
        load.addLoadDetails(origin);

        assertTrue(sut.isPickupToday(load));

        load.setStatus(ShipmentStatus.IN_TRANSIT);
        LoadDetailsEntity destination = load.getDestination();
        destination.setDeparture(DateUtils.addDays(new Date(), -1));
        load.addLoadDetails(origin);

        assertFalse(sut.isPickupToday(load));
    }

    private LoadEntity createLoad() throws ParseException {
        LoadEntity load = new LoadEntity();
        LoadCostDetailsEntity est = new LoadCostDetailsEntity();
        est.setStatus(Status.INACTIVE);
        est.setTotalCost(BigDecimal.valueOf(100));
        est.setTotalRevenue(BigDecimal.valueOf(120));

        // this should not be used. because older record is present
        LoadCostDetailsEntity est2 = new LoadCostDetailsEntity();
        est2.setStatus(Status.INACTIVE);
        est2.setTotalCost(BigDecimal.ZERO);
        est2.setTotalRevenue(BigDecimal.ZERO);

        LoadCostDetailsEntity act = new LoadCostDetailsEntity();
        act.setStatus(Status.ACTIVE);
        act.setTotalCost(BigDecimal.valueOf(100));
        act.setTotalRevenue(BigDecimal.valueOf(120));

        load.setCostDetails(new HashSet<LoadCostDetailsEntity>());
        load.getCostDetails().add(act);
        load.getCostDetails().add(est);

        LoadDetailsEntity origin = new LoadDetailsEntity(LoadAction.PICKUP, PointType.ORIGIN);
        origin.setEarlyScheduledArrival(new Date());
        origin.setDeparture(DateUtils.truncate(new Date(), Calendar.DATE));
        origin.setArrivalWindowEnd(new Date());
        load.addLoadDetails(origin);

        LoadDetailsEntity dest = new LoadDetailsEntity(LoadAction.DELIVERY, PointType.DESTINATION);
        dest.setDeparture(DateUtils.addDays(new Date(), 1));
        load.addLoadDetails(dest);
        return load;
    }

    private void verifyDeletedAlerts(LoadEntity shipment, int count, ShipmentAlertType... notRemovedAlerts) {
        List<ShipmentAlertType> removedAlerts = new ArrayList<ShipmentAlertType>(Arrays.asList(ShipmentAlertType.values()));
        for (ShipmentAlertType alert : notRemovedAlerts) {
            removedAlerts.remove(alert);
        }

        Mockito.verify(shipmentAlertDao, Mockito.times(count)).removeAlerts(shipment.getId(),
                removedAlerts.toArray(new ShipmentAlertType[removedAlerts.size()]));
    }
}
