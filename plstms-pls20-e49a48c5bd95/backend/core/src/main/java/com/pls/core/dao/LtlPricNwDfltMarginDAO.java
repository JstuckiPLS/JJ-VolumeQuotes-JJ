package com.pls.core.dao;

import com.pls.core.domain.organization.LtlPricNwDfltMarginEntity;

/**
 * DAO for {@link LtlPricNwDfltMarginEntity}.
 * 
 * @author Ashwini Neelgund
 */
public interface LtlPricNwDfltMarginDAO extends AbstractDao<LtlPricNwDfltMarginEntity, Long> {

    /**
     * Get pricing default margin data for LTL network.
     * 
     * @return pricing default margin data for LTL network.
     */
    LtlPricNwDfltMarginEntity getDefaultLTLMargin();

}
