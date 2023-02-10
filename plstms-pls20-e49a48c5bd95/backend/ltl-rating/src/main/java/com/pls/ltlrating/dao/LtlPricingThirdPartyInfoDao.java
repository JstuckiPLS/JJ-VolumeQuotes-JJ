package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity;

/**
 * Data Access Object to manipulate {@link LtlPricingThirdPartyInfoEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPricingThirdPartyInfoDao extends AbstractDao<LtlPricingThirdPartyInfoEntity, Long> {

    /**
     * Get Active {@link LtlPricingThirdPartyInfoEntity} by profileId.
     *
     * @param profileDetailId
     *            Not <code>null</code> {@link Long}.
     * @return {@link LtlPricingThirdPartyInfoEntity}
     */
    LtlPricingThirdPartyInfoEntity findActiveByProfileDetailId(Long profileDetailId);

    /**
     * Get List of {@link LtlPricingThirdPartyInfoEntity} which related to specified profile detail id.
     *
     * @param profileDetailId
     *            Profile Detail Id.
     * @return List of {@link LtlPricingThirdPartyInfoEntity}
     */
    List<LtlPricingThirdPartyInfoEntity> findByProfileDetailId(Long profileDetailId);

    /**
     * Get {@link LtlPricingThirdPartyInfoEntity} by specified profile id.
     *
     * @param profileId -
     *          Not <code>null</code> {@link Long}
     * @return {@link LtlPricingThirdPartyInfoEntity}
     */
    LtlPricingThirdPartyInfoEntity findByProfileId(Long profileId);

    /**
     * Find all {@link LtlPricingThirdPartyInfoEntity} which pricingType is 'BLANKET_CSP' and was copied from
     * specified <tt>copiedFrom</tt> value.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlPricingThirdPartyInfoEntity}.
     */
    List<LtlPricingThirdPartyInfoEntity> findAllCspChildsCopyedFrom(Long copiedFrom);

    /**
     * Update status of entity by specified Id.
     *
     * @param id
     *          Not <code>null</code>. Id of entity which should be updated.
     * @param status
     *          Not <code>null</code>. Value of {@link Status} enumeration.
     * @param modifiedBy
     *          Not <code>null</code>. Id of person who performs this operation.
     */
    void updateStatus(Long id, Status status, Long modifiedBy);

    /**
     * Updates status in child Customer Specific Price entities.
     *
     * @param profileDetailId
     *            Id of profile detail which child CSPs should be inactivated.
     * @param modifiedBy
     *            Not <code>null</code> value of {@link Long}.
     */
    void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy);
}
