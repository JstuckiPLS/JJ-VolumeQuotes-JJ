package com.pls.core.dao.impl;

import com.pls.core.dao.CustomerNoteDao;
import com.pls.core.domain.organization.CustomerNoteEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link CustomerNoteDao} implementation.
 *
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class CustomerNoteDaoImpl extends AbstractDaoImpl<CustomerNoteEntity, Integer> implements CustomerNoteDao {
    @Override
    public CustomerNoteEntity update(CustomerNoteEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(CustomerNoteEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Integer id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CustomerNoteEntity> getCustomerNotesByCustomerId(Long customerId) {
        Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put("customerId", customerId);
        return findByNamedQuery(CustomerNoteEntity.Q_BY_CUSTOMER_ID, parameters);
    }
}
