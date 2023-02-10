package com.pls.extint.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.extint.domain.ApiExceptionEntity;
import com.pls.extint.shared.ApiCriteriaCO;

/**
 * Logs the mismatches for the load fields in PLSPRO database and the value received from Tracking API.
 * 
 * @author Pavani Challa
 * 
 */
public interface ApiExceptionsDao extends AbstractDao<ApiExceptionEntity, Long> {

    /**
     * Find all the API_EXCEPTIONS records that match the criteria.
     * 
     * @param criteriaCO
     *            Criteria object.
     * @return the list of API_EXCEPTIONS matching the criteria
     */
    List<ApiExceptionEntity> findByCriteria(ApiCriteriaCO criteriaCO);
}
