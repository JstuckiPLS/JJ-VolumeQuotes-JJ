package com.pls.invoice.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.invoice.dao.LoadBillingHistoryDao;
import com.pls.invoice.domain.LoadBillingHistoryEntity;

/**
 * {@link LoadBillingHistoryDao} implementation.
 *
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class LoadBillingHistoryDaoImpl extends AbstractDaoImpl<LoadBillingHistoryEntity, Long> implements LoadBillingHistoryDao {
}
