package com.pls.extint.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.extint.domain.ApiLogEntity;
import com.pls.extint.shared.ApiCriteriaCO;
import com.pls.extint.shared.ApiLogVO;

/**
 * Dao operations for accessing the API_LOG table.
 * 
 * @author Pavani Challa
 * 
 */
public interface ApiLogDao extends AbstractDao<ApiLogEntity, Long> {

    /**
     * Find all the API_LOG records that match the criteria.
     * 
     * @param criteriaCO
     *            criteria object.
     * @return the matching API_LOG records
     */
    List<ApiLogVO> findByCriteria(ApiCriteriaCO criteriaCO);

    /**
     * Get all the API_LOG records.
     * 
     * @return all the records.
     */
    List<ApiLogVO> getAllLogs();

    /**
     * Get the latest note returned by tracking api for the load.
     * 
     * @param loadId
     *            load for which recent status has to be returned.
     * @param apiLogId
     *            to ensure that the current log is ignored
     * @return the recent status returned by tracking api
     */
    String getLatestTrackingNote(Long loadId, Long apiLogId);
}
