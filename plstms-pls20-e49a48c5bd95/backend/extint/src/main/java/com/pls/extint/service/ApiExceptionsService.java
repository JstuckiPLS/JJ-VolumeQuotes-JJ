package com.pls.extint.service;

import java.util.List;

import com.pls.extint.domain.ApiExceptionEntity;
import com.pls.extint.shared.ApiCriteriaCO;

/**
 * Service for accessing the API_EXCEPTIONS data.
 * 
 * @author Pavani Challa
 * 
 */
public interface ApiExceptionsService {

    /**
     * Saves the API_EXCEPTIONS record to the database.
     * 
     * @param entity
     *            API_EXCEPTIONS record to be saved.
     * @return the saved entity.
     */
    ApiExceptionEntity save(ApiExceptionEntity entity);

    /**
     * Get all the API_EXCEPTIONS records.
     * 
     * @return all the records.
     */
    List<ApiExceptionEntity> getAll();

    /**
     * Get the API_LOG records matching the criteria.
     * 
     * @param criteria
     *            criteria to search the API_EXCEPTIONS table for.
     * @return the matched API_EXCEPTIONS records.
     */
    List<ApiExceptionEntity> getByCriteria(ApiCriteriaCO criteria);

    /**
     * Find API_EXCEPTIONS record by id.
     * 
     * @param id
     *            the entity id to search.
     * @return the found result or null.
     */
    ApiExceptionEntity find(Long id);
}
