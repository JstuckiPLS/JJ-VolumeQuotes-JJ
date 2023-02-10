package com.pls.shipment.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.bo.ShipmentEventBO;

/**
 * DAO for {@link LoadEventEntity} entities.
 * 
 * @author Gleb Zgonikov
 */
public interface ShipmentEventDao extends AbstractDao<LoadEventEntity, Long> {
    /**
     * Get {@link LoadEventEntity} entities by shipment ID.
     * 
     * @param shipmentId Not <code>null</code> ID.
     * @return Not <code>null</code> {@link List}.
     */
    List<ShipmentEventBO> findShipmentEvents(Long shipmentId);

}
