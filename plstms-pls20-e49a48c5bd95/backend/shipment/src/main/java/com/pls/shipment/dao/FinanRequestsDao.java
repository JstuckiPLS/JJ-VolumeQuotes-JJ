package com.pls.shipment.dao;

import com.pls.shipment.domain.FinanRequestsEntity;

/**
 * DAO for {@link FinanRequestsEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public interface FinanRequestsDao {
    /**
     * Save new entity or update existing entity.
     * 
     * @param entity
     *            Not <code>null</code> entity.
     * @return updated entity
     */
    FinanRequestsEntity saveOrUpdate(FinanRequestsEntity entity);
}
