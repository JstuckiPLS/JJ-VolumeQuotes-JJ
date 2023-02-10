package com.pls.extint.service;

import java.util.List;

import com.pls.extint.domain.ApiLogEntity;
import com.pls.extint.shared.ApiCriteriaCO;
import com.pls.extint.shared.ApiLogVO;

/**
 * Service class for accessing Api Log records.
 * 
 * @author Pavani Challa
 * 
 */
public interface ApiLogService {

    /**
     * Saves the API_LOG record to the database. As the API_LOG record will be created and updated in the same transaction of the calling service,
     * this update to API_LOG record is performed in a new transaction so that users are able to search for the log for the requests that are still
     * being processed.
     * 
     * @param entity
     *            API_LOG record to be saved.
     * @return the saved entity.
     */
    ApiLogEntity save(ApiLogEntity entity);

    /**
     * Find API_LOG record by id. This returns all the data along with the request, response and error blob data.
     * 
     * @param id
     *            the entity id to search.
     * @return the found result or null.
     * @throws Exception
     *             if the record is not found or if any error occurs while extract data.
     */
    ApiLogEntity find(Long id) throws Exception;

    /**
     * Get all the API_LOG records.
     * 
     * @return all the records.
     */
    List<ApiLogVO> getAll();

    /**
     * Get the API_LOG records that match the criteria passed.
     * 
     * @param criteria
     *            criteria to search the API_LOG table for.
     * @return the matched API_LOG records.
     */
    List<ApiLogVO> getByCriteria(ApiCriteriaCO criteria);

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
