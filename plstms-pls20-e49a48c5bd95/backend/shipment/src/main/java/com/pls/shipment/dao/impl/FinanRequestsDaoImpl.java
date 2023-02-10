package com.pls.shipment.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.FinanRequestsDao;
import com.pls.shipment.domain.FinanRequestsEntity;

/**
 * {@link FinanRequestsDao} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class FinanRequestsDaoImpl extends AbstractDaoImpl<FinanRequestsEntity, Long> implements FinanRequestsDao {

}
