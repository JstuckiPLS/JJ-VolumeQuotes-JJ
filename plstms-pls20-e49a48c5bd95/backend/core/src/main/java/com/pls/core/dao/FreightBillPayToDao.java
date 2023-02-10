package com.pls.core.dao;

import com.pls.core.domain.FreightBillPayToEntity;

/**
 * Data Access Object for {@link FreightBillPayToEntity} data.
 * 
 * @author Aleksandr Leshchenko
 */
public interface FreightBillPayToDao extends AbstractDao<FreightBillPayToEntity, Long> {
    /**
     * Get instance of default {@link FreightBillPayToEntity}.
     * 
     * @return {@link FreightBillPayToEntity}
     */
    FreightBillPayToEntity getDefaultFreightBillPayTo();
}
