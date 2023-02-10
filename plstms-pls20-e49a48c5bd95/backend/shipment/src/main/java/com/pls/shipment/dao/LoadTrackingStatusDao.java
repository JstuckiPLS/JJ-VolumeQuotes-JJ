package com.pls.shipment.dao;

import java.util.List;
import java.util.Map;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LoadTrackingStatusEntity;
import com.pls.shipment.domain.LoadTrackingStatusEntityId;

/**
 * DAO for {@link LoadTrackingStatusEntity}.
 *
 * @author Mikhail Boldinov, 26/05/14
 */
public interface LoadTrackingStatusDao extends AbstractDao<LoadTrackingStatusEntity, LoadTrackingStatusEntityId> {

    /**
     * Get list of Tracking Status for some EDI.
     * 
     * @param source
     *            EDI number
     * @return list entity
     */
    List<LoadTrackingStatusEntity> getAll(Long source);

    /**
     * find Tracking Status for some EDI.
     * 
     * @param source
     *            EDI number
     * @param code
     *            EDI code
     * @return entity
     */
    LoadTrackingStatusEntity find(String code, Long source);

    /**
     * Get map of Tracking Status for some EDI.
     * 
     * @param source
     *            EDI number
     * 
     * @return Map
     */
    Map<String, String> getMap(Long source);
}
