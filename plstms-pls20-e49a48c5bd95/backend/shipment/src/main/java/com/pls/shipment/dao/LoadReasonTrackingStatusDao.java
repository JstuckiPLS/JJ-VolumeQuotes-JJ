package com.pls.shipment.dao;

import java.util.List;
import java.util.Map;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LoadReasonTrackingStatusEntity;

/**
 * DAO for {@link LoadReasonTrackingStatusEntity}.
 *
 * @author Alexander Nalapko
 */
public interface LoadReasonTrackingStatusDao extends AbstractDao<LoadReasonTrackingStatusEntity, String> {
    /**
     * Get list of Reason Tracking Status for some EDI.
     * 
     * @param source
     *            EDI number
     * @return list entity
     */
    List<LoadReasonTrackingStatusEntity> getAll(Long source);

    /**
     * find Reason Tracking Status for some EDI.
     * 
     * @param source
     *            EDI number
     * @param id
     *            EDI code
     * @return entity
     */
    LoadReasonTrackingStatusEntity find(String id, Long source);

    /**
     * Get map of Reason Tracking Status for some EDI.
     * 
     * @param source
     *            EDI number
     * @return Map
     */
    Map<String, String> getMap(Long source);
}
