package com.pls.shipment.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.FinancialReasonsDao;
import com.pls.shipment.domain.FinancialReasonsEntity;

/**
 * {@link FinancialReasonsDao} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class FinancialReasonsDaoImpl extends AbstractDaoImpl<FinancialReasonsEntity, Long> implements FinancialReasonsDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<FinancialReasonsEntity> getFinancialReasonsForAdjustments() {
        return getCurrentSession().getNamedQuery(FinancialReasonsEntity.Q_LOAD_FOR_ADJUSTMENTS).setCacheable(true).list();
    }
}
