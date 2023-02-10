package com.pls.shipment.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.FinancialReasonsEntity;

/**
 * DAO for {@link FinancialReasonsEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public interface FinancialReasonsDao extends AbstractDao<FinancialReasonsEntity, Long> {

    /**
     * Get list of all applicable reasons for adjustments.
     * 
     * @return list of all applicable reasons for adjustments.
     */
    List<FinancialReasonsEntity> getFinancialReasonsForAdjustments();
}
