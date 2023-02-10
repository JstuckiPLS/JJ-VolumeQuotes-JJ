package com.pls.invoice.dao;

import java.util.List;

import com.pls.invoice.domain.FinanIntResponsesEntity;

/**
 * DAO for {@link FinanIntResponsesEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public interface FinanIntResponsesDao {

    /**
     * Insert loads details into {@link FinanIntResponsesEntity} for specified request ID.
     * 
     * @param requestId
     *            request ID.
     * @param loadsIds
     *            list of invoiced loads IDs.
     * @param userId
     *            user responsible for invoicing
     */
    void insertLoads(Long requestId, List<Long> loadsIds, Long userId);

    /**
     * Insert adjustments details into {@link FinanIntResponsesEntity} for specified request ID.
     * 
     * @param requestId
     *            request ID.
     * @param adjustmentsIds
     *            list of invoiced adjustmetns IDs.
     * @param userId
     *            user responsible for invoicing
     */
    void insertAdjustments(Long requestId, List<Long> adjustmentsIds, Long userId);
}
