package com.pls.invoice.dao;

import com.pls.invoice.domain.LoadBillingHistoryEntity;

/**
 * DAO for load billing history.
 *
 * @author Aleksandr Leshchenko
 */
public interface LoadBillingHistoryDao {

    /**
     * Save or update enitity in DB.
     * 
     * @param entity
     *            to save
     * @return saved entity
     */
    LoadBillingHistoryEntity saveOrUpdate(LoadBillingHistoryEntity entity);
}
