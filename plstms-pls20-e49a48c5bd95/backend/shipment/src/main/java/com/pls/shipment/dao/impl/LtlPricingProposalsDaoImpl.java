package com.pls.shipment.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.LtlPricingProposalsDao;
import com.pls.shipment.domain.LtlPricingProposalsEntity;

/**
 * Dao Implementation class for LtlPricingProposalsEntity {@link com.pls.shipment.dao.LtlPricingProposalsDao}.
 *
 * @author Ashwini Neelgund
 */
@Repository
@Transactional
public class LtlPricingProposalsDaoImpl extends AbstractDaoImpl<LtlPricingProposalsEntity, Long> implements LtlPricingProposalsDao {

    @Override
    public void inactivatePricingProposals(Long loadId) {
        getCurrentSession().getNamedQuery(LtlPricingProposalsEntity.U_INACTIVATE_FOR_LOAD).setLong("loadId", loadId).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void createForLoadByQuoteId(Long loadId, Long quoteId) {
        List<LtlPricingProposalsEntity> pricingProposals = getCurrentSession().getNamedQuery(LtlPricingProposalsEntity.Q_GET_BY_QUOTE)
                .setLong("quoteId", quoteId).list();

        for (LtlPricingProposalsEntity proposal : pricingProposals) {
            getCurrentSession().evict(proposal);
            proposal.setId(null);
            proposal.setLoadId(loadId);
        }
        saveOrUpdateBatch(pricingProposals);
    }

}
