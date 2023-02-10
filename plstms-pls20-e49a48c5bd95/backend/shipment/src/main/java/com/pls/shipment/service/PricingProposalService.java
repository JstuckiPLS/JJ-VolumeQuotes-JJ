package com.pls.shipment.service;

import java.util.List;

import com.pls.shipment.domain.LoadEntity;
import com.pls.shipment.domain.LtlPricingProposalsEntity;

/**
 * Service for creating / updating {@link LtlPricingProposalsEntity}.
 * 
 * @author Hima Bindu Challa
 */
public interface PricingProposalService {

    /**
     * Method saves all the pricing proposals generated during the getQuotes, after user has booked a load.
     * 
     * @param proposalEntities
     *            - list of pricing proposals generated during the getQuotes
     */
    void savePricingProposals(List<LtlPricingProposalsEntity> proposalEntities);

    /**
     * Method creates and saves all the pricing proposal's materials selected during the getQuotes, after user
     * has booked a load.
     * 
     * @param load
     *            load with materials selected during the getQuotes
     */
    void createPricingProposalMaterials(LoadEntity load);

    /**
     * Updates the pricing proposals matching the quote id with the corresponding load id.
     * @param loadId - load id.
     * @param quoteId - saved quote id.
     */
    void updateLoadId(Long loadId, Long quoteId);
}
