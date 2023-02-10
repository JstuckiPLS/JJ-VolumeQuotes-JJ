package com.pls.shipment.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.shipment.dao.ShipmentAlertDao;
import com.pls.shipment.domain.LoadDetailsEntity;
import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.ShipmentAlertEntity;
import com.pls.shipment.domain.bo.ShipmentAlertType;
import com.pls.shipment.domain.enums.ShipmentAlertsStatus;
import com.pls.shipment.service.ShipmentAlertService;

/**
 * {@link ShipmentAlertService} implementation.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Service
@Transactional
public class ShipmentAlertServiceImpl implements ShipmentAlertService {
    private static final Set<ShipmentStatus> PRIOR_PICKUP_STATUS = Sets.newHashSet(ShipmentStatus.OPEN,
            ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED);
    private static final Set<ShipmentStatus> ACCOMPANYING_PICKUP_STATUS = Sets.newHashSet(ShipmentStatus.DELIVERED,
            ShipmentStatus.IN_TRANSIT, ShipmentStatus.OUT_FOR_DELIVERY);
    private static final Set<ShipmentStatus> IN_TRANSIT_STATUS = Sets.newHashSet(ShipmentStatus.IN_TRANSIT,
            ShipmentStatus.OUT_FOR_DELIVERY);
    private static final int MINUS_THIRTY = -30;

    @Autowired
    private ShipmentAlertDao shipmentAlertDao;

    @Override
    public void generateMinuteBasedShipmentAlerts() {
        shipmentAlertDao.removeOutdatedTimeAlerts();
        shipmentAlertDao.generateNewTimeAlerts();
    }

    @Override
    public void processShipmentAlerts(LoadEntity shipment) {
        if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
            shipmentAlertDao.removeAlerts(shipment.getId());
            return;
        }

        List<ShipmentAlertType> typesToDeactivate = new ArrayList<ShipmentAlertType>(Arrays.asList(ShipmentAlertType.values()));

        generateTimeAlerts(shipment, typesToDeactivate);

        if (shipment.getActiveCostDetail() != null && shipment.getActiveCostDetail().getGuaranteedBy() != null) {
            createOrUpdateShipmentAlert(shipment, ShipmentAlertType.GUARANTEED_SERVICE);
            typesToDeactivate.remove(ShipmentAlertType.GUARANTEED_SERVICE);
        }
        if (isMissingPickupDate(shipment)) {
            createOrUpdateShipmentAlert(shipment, ShipmentAlertType.DELIVERY_DATE_WO_PICKUP_DATE);
            typesToDeactivate.remove(ShipmentAlertType.DELIVERY_DATE_WO_PICKUP_DATE);
        }
        deactivateAlerts(shipment, typesToDeactivate);
    }

    @Override
    public void deactivateAlerts(LoadEntity shipment) {
        shipmentAlertDao.removeAlerts(shipment.getId());
    }

    private void deactivateAlerts(LoadEntity shipment, List<ShipmentAlertType> typesToDeactivate) {
        if (!typesToDeactivate.isEmpty()) {
            shipmentAlertDao.removeAlerts(shipment.getId(),
                    typesToDeactivate.toArray(new ShipmentAlertType[typesToDeactivate.size()]));
        }
    }

    private void generateTimeAlerts(LoadEntity shipment, List<ShipmentAlertType> typesToDeactivate) {
        if (is30MinLeftToPickup(shipment)) {
            createOrUpdateShipmentAlert(shipment, ShipmentAlertType.THIRTY_MIN_TO_PICKUP);
            typesToDeactivate.remove(ShipmentAlertType.THIRTY_MIN_TO_PICKUP);
        } else if (isMissedPickup(shipment)) {
            createOrUpdateShipmentAlert(shipment, ShipmentAlertType.MISSED_PICKUP);
            typesToDeactivate.remove(ShipmentAlertType.MISSED_PICKUP);
        } else if (isPickupToday(shipment)) {
            createOrUpdateShipmentAlert(shipment, ShipmentAlertType.PICKUP_TODAY);
            typesToDeactivate.remove(ShipmentAlertType.PICKUP_TODAY);
        } else if (isMissedDelivery(shipment)) {
            createOrUpdateShipmentAlert(shipment, ShipmentAlertType.MISSED_DELIVERY);
            typesToDeactivate.remove(ShipmentAlertType.MISSED_DELIVERY);
        }
    }

    private void createOrUpdateShipmentAlert(LoadEntity shipment, ShipmentAlertType alertType) {
        if (alertType == null || shipment.getId() == null) {
            throw new IllegalArgumentException(String.format("Either shipmentId(%s) or type(%s) is null", shipment.getId(), alertType));
        }
        ShipmentAlertEntity shipmentAlert = shipmentAlertDao.getShipmentAlert(shipment.getId(), alertType);
        if (shipmentAlert == null) {
            shipmentAlert = new ShipmentAlertEntity();
            shipmentAlert.setLoadId(shipment.getId());
            shipmentAlert.setCustomerId(shipment.getOrganization().getId());
            shipmentAlert.setType(alertType);
            shipmentAlertDao.saveOrUpdate(shipmentAlert);
        } else {
            shipmentAlert.setStatus(ShipmentAlertsStatus.ACTIVE);
            shipmentAlert.setAcknowledgedUser(null);
            shipmentAlertDao.update(shipmentAlert);
        }
    }

    /**
     * Check if it's less then 30m left to pickup the load.
     *
     * @param shipment
     *            shipment to check
     * @return true if less then 30m to pickup
     */
    boolean is30MinLeftToPickup(LoadEntity shipment) {
        LoadDetailsEntity origin = shipment.getOrigin();
        if (origin == null || origin.getScheduledArrival() == null) {
            return false;
        }
        Date latestPickup = origin.getScheduledArrival();
        Date thirtyMinBeforeLatestPickup = DateUtils.addMinutes(latestPickup, MINUS_THIRTY);
        Date now = new Date();
        return PRIOR_PICKUP_STATUS.contains(shipment.getStatus())
                && now.compareTo(latestPickup) < 0
                && now.compareTo(thirtyMinBeforeLatestPickup) >= 0
                && origin.getDeparture() == null;
    }

    /**
     * Check if carrier missed pickup.
     *
     * @param shipment
     *            shipment to check
     * @return true if carrier missed pickup
     */
    private boolean isMissedPickup(LoadEntity shipment) {
        LoadDetailsEntity origin = shipment.getOrigin();
        if (origin == null || origin.getScheduledArrival() == null) {
            return false;
        }
        Date now = new Date();
        Date latestPickup = origin.getScheduledArrival();
        return PRIOR_PICKUP_STATUS.contains(shipment.getStatus())
                && latestPickup.compareTo(now) < 0
                && origin.getDeparture() == null;
    }

    /**
     * Check if load is scheduled to be picked up today and it is not missed (there is enough time to pickup
     * in time).
     *
     * @param shipment
     *            shipment to check
     * @return true if pickup today
     */
    boolean isPickupToday(LoadEntity shipment) {
        LoadDetailsEntity origin = shipment.getOrigin();
        if (origin == null || origin.getScheduledArrival() == null) {
            return false;
        }
        Date latestPickup = origin.getScheduledArrival();
        Date latestPickupStartOfTheDay = DateUtils.truncate(latestPickup, Calendar.DAY_OF_MONTH);
        Date now = new Date();
        return PRIOR_PICKUP_STATUS.contains(shipment.getStatus())
                && latestPickup.compareTo(now) > 0
                && latestPickupStartOfTheDay.compareTo(now) <= 0
                && origin.getDeparture() == null;
    }

    /**
     * Check if carrier didn't deliver load and delivery date passed.
     *
     * @param shipment
     *            shipment to check
     * @return true if carrier didn't deliver load
     */
    private boolean isMissedDelivery(LoadEntity shipment) {
        LoadDetailsEntity destination = shipment.getDestination();
        if (destination == null || destination.getScheduledArrival() == null) {
            return false;
        }
        Date now = new Date();
        Date latestDelivery = destination.getScheduledArrival();
        return IN_TRANSIT_STATUS.contains(shipment.getStatus())
                && latestDelivery.compareTo(now) <= 0
                && destination.getDeparture() == null;
    }

    /**
     * Check if load was picked up but pickup date was not specified (Can happen via EDI).
     *
     * @param shipment
     *            shipment to check.
     * @return true if we missed pickup date.
     */
    private boolean isMissingPickupDate(LoadEntity shipment) {
        LoadDetailsEntity origin = shipment.getOrigin();
        if (origin == null) {
            return false;
        }
        return ACCOMPANYING_PICKUP_STATUS.contains(shipment.getStatus()) && origin.getDeparture() == null;
    }

}


