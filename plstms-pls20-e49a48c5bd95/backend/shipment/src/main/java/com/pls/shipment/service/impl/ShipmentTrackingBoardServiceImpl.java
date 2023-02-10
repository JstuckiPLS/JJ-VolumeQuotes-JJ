package com.pls.shipment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.dao.ShipmentAlertDao;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardAlertListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardBookedListItemBO;
import com.pls.shipment.domain.bo.ShipmentTrackingBoardListItemBO;
import com.pls.shipment.domain.enums.ShipmentAlertsStatus;
import com.pls.shipment.service.ShipmentTrackingBoardService;

/**
 * {@link ShipmentTrackingBoardService} implementation.
 *
 * @author Viacheslav Krot
 */
@Service
@Transactional(readOnly = true)
public class ShipmentTrackingBoardServiceImpl implements ShipmentTrackingBoardService {
    @Autowired
    private LtlShipmentDao shipmentDao;
    @Autowired
    private ShipmentAlertDao shipmentAlertDao;

    @Override
    public List<ShipmentTrackingBoardAlertListItemBO> getAlertShipments() {
        return shipmentAlertDao.getAlertsForUser(SecurityUtils.getCurrentPersonId(), ShipmentAlertsStatus.ACTIVE, ShipmentAlertsStatus.ACKNOWLEDGED);
    }

    @Override
    public void acknowledgeAlerts(long shipmentId) {
        shipmentAlertDao.acknowledgeAlerts(shipmentId, SecurityUtils.getCurrentPersonId());
    }

    @Override
    public Long countOfActiveAlerts() {
        return shipmentAlertDao.getAlertsCount(SecurityUtils.getCurrentPersonId(), ShipmentAlertsStatus.ACTIVE);
    }

    @Override
    public List<ShipmentTrackingBoardBookedListItemBO> getBookedShipments(Long personId) {
        return shipmentDao.findBookedShipments(personId);
    }

    @Override
    public List<ShipmentTrackingBoardListItemBO> getUndeliveredShipments() {
        return shipmentDao.findUndeliveredShipments();
    }

    @Override
    public List<ShipmentTrackingBoardListItemBO> getOpenShipments(Long personId, RegularSearchQueryBO search) {
        return shipmentDao.findOpenShipments(personId, search);
    }

    @Override
    public List<ShipmentListItemBO> getUnbilledShipments(Long personId) {
        return shipmentDao.findUnbilledShipments(personId);
    }

    @Override
    public List<ShipmentListItemBO> getAllShipments(RegularSearchQueryBO search, Long userId) {
        return shipmentDao.findAllShipments(search, userId);
    }

    @Override
    public List<ShipmentListItemBO> getHoldShipments() {
        return shipmentDao.findHoldShipments();
    }
}
