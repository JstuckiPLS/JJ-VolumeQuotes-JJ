package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.domain.LtlPricingBlockedCustomersEntity;

/**
 * DAO for {@link LtlPricingBlockedCustomersEntity}.
 *
 * @author Mikhail Boldinov, 22/02/13
 */
public interface LtlPricingBlockedCustomersDao extends AbstractDao<LtlPricingBlockedCustomersEntity, Long> {

    /**
     * Gets list of {@link LtlPricingBlockedCustomersEntity} for specified profile id.
     *
     * @param profileId Not <code>null</code> ID.
     * @return list of {@link LtlPricingBlockedCustomersEntity}
     */
    List<LtlPricingBlockedCustomersEntity> getExplicitlyBlockedCustomersByProfileId(Long profileId);
}
