package com.pls.core.dao;

import com.pls.core.domain.organization.CustomerNoteEntity;

import java.util.List;

/**
 * DAO for customer notes.
 * 
 * @author Aleksandr Leshchenko
 * 
 */
public interface CustomerNoteDao extends AbstractDao<CustomerNoteEntity, Integer> {
    /**
     * Customer notes won't be updated.
     * UnsupportedOperationException will be thrown in case of update attempt.
     * 
     * @param entity
     *            to be updated
     * @return nothing
     */
    @Override
    CustomerNoteEntity update(CustomerNoteEntity entity);

    /**
     * Customer notes won't be removed.
     * UnsupportedOperationException will be thrown in case of remove attempt.
     *
     * @param entity
     *            to be removed
     */
    void remove(CustomerNoteEntity entity);

    /**
     * Customer notes won't be removed.
     * UnsupportedOperationException will be thrown in case of remove attempt.
     *
     * @param id
     *            of entity to be removed
     */
    void remove(Integer id);

    /**
     * Retrieve customer notes for specified customerId.
     * 
     * @param customerId
     *            id of customer for whom notes need to be retrieved
     * @return list of customer notes
     */
    List<CustomerNoteEntity> getCustomerNotesByCustomerId(Long customerId);
}
