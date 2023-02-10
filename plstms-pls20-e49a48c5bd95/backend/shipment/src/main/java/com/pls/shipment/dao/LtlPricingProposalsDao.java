package com.pls.shipment.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LtlPricingProposalsEntity;

/**
 * Data Access Object for {@link LtlPricingProposalsEntity} data.
 *
 * @author Ashwini Neelgund
 */
public interface LtlPricingProposalsDao extends AbstractDao<LtlPricingProposalsEntity, Long> {

    /**
     * Update {@link LtlPricingProposalsEntity#getStatus()} to Inactive by Load ID.
     *
     * @param loadId
     *            ID of Load
     */
    void inactivatePricingProposals(Long loadId);

    /**
     * Creates new pricing proposals with specified load id by copying them from existing proposals with specified quote id.
     * 
     * @param loadId
     *            - load id.
     * @param quoteId
     *            - saved quote id.
     */
    void createForLoadByQuoteId(Long loadId, Long quoteId);
}
