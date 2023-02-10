package com.pls.shipment.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.FinancialAccessorialsDao;
import com.pls.shipment.domain.FinancialAccessorialsEntity;

/**
 * {@link FinancialAccessorialsDao} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class FinancialAccessorialsDaoImpl extends AbstractDaoImpl<FinancialAccessorialsEntity, Long> implements FinancialAccessorialsDao {
}
