package com.pls.core.service;

import com.pls.core.domain.FreightBillPayToEntity;

/**
 * Service for {@link FreightBillPayToEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public interface FreightBillPayToService {
    /**
     * Get instance of default {@link FreightBillPayToEntity}.
     * 
     * @return {@link FreightBillPayToEntity}
     */
    FreightBillPayToEntity getDefaultFreightBillPayTo();

    /**
     * Get instance of {@link FreightBillPayToEntity}.
     * 
     * @param id Not <code>null</code> unique identifier.
     * @return {@link FreightBillPayToEntity}
     */
    FreightBillPayToEntity getFreightBillPayTo(Long id);
}
