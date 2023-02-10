package com.pls.core.dao;

import java.math.BigDecimal;

import com.pls.core.domain.organization.OrganizationPricingEntity;

/**
 * DAO for {@link OrganizationPricingEntity}.
 * 
 * @author Hima Bindu Challa
 * 
 */

public interface OrganizationPricingDao extends AbstractDao<OrganizationPricingEntity, Long> {

    /**
     * Get active customer pricing information for the given customer.
     * @param orgId - Customer org Id.
     * @return the active customer pricing information.
     */
    OrganizationPricingEntity getActivePricing(Long orgId);

    /**
     * Get {@link OrganizationPricingEntity#getMinAcceptMargin()} for the given customer.
     * 
     * @param orgId
     *            - Customer org Id.
     * @return the active {@link OrganizationPricingEntity#getMinAcceptMargin()}.
     */
    BigDecimal getMinAcceptMargin(Long orgId);

}
