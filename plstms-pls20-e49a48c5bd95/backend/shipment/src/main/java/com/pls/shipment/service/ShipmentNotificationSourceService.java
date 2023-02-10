package com.pls.shipment.service;

import java.util.List;

import com.pls.shipment.domain.bo.ShipmentNotificationSourceItemBo;

/**
 * Service provides sources for shipment notification.
 * 
 * @author Alexander Kirichenko
 */
public interface ShipmentNotificationSourceService {

    /**
     * Search for all shipment notification sources.
     * 
     * @param customerId
     *            ID of actual organization
     * @param personId
     *            user id
     * @return list of {@link ShipmentNotificationSourceItemBo}
     */
    List<ShipmentNotificationSourceItemBo> getShipmentNotificationSourceItems(Long customerId, Long personId);
}
