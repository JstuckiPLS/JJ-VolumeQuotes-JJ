package com.pls.shipment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.bo.LoadAuditBO;
import com.pls.shipment.domain.bo.LoadTrackingBO;
import com.pls.shipment.domain.bo.ShipmentEventBO;
import com.pls.shipment.service.ShipmentEventService;

/**
 * Implementation of {@link com.pls.shipment.service.ShipmentEventService}.
 *
 * @author Gleb Zgonikov
 */
@Service
@Transactional
public class ShipmentEventServiceImpl implements ShipmentEventService {

    @Autowired
    private ShipmentEventDao eventDao;

    @Autowired
    private LoadTrackingDao trackingDao;

    @Override
    public List<ShipmentEventBO> findShipmentEvents(Long shipmentId) {
        return eventDao.findShipmentEvents(shipmentId);
    }

    @Override
    public List<LoadTrackingBO> findShipmentTracking(Long shipmentId) {
        return trackingDao.findShipmentTracking(shipmentId);
    }

    @Override
    public List<LoadAuditBO> findShipmentAudit(Long shipmentId) {
        return trackingDao.findShipmentAudit(shipmentId);
    }

    @Override
    public void save(LoadEventEntity entity) {
        eventDao.saveOrUpdate(entity);
    }
}
