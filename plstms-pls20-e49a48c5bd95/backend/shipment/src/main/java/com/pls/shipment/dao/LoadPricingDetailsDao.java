package com.pls.shipment.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LoadPricingDetailsEntity;

/**
 * DAO for {@link LoadPricingDetailsEntity}.
 * 
 * @author Ashwini Neelgund
 */
public interface LoadPricingDetailsDao extends AbstractDao<LoadPricingDetailsEntity, Long> {

    /**
     * delete existing {@link LoadPricingDetailsEntity}.
     *
     * @param loadPricDetail
     *            load pricing detail
     */
    void delete(LoadPricingDetailsEntity loadPricDetail);

    /**
     * get set of shipment pricing details {@link LoadPricingDetailsEntity}.
     *
     * @param shipmentId
     *            id of shipment
     * @return load pricing details
     */
    LoadPricingDetailsEntity getShipmentPricingDetails(Long shipmentId);

}
