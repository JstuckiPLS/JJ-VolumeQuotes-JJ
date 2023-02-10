package com.pls.shipment.service.dictionary;

import java.util.List;

import com.pls.shipment.domain.FinancialReasonsEntity;

/**
 * Service for {@link FinancialReasonsEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public interface FinancialReasonsDictionaryService {

    /**
     * Get list of all applicable reasons for adjustments.
     * 
     * @return list of all applicable reasons for adjustments.
     */
    List<FinancialReasonsEntity> getFinancialReasonsForAdjustments();
}
