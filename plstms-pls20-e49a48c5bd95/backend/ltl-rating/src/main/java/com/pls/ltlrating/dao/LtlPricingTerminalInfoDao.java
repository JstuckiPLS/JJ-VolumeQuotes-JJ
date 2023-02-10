/**
 *
 */
package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity;

/**
 * Data Access Object to access {@link LtlPricingTerminalInfoEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPricingTerminalInfoDao extends AbstractDao<LtlPricingTerminalInfoEntity, Long> {

    /**
     * Get Active {@link LtlPricingTerminalInfoEntity} by profile detail Id.
     *
     * @param profileDetailId
     *            Not <code>null</code> {@link Long}.
     * @return {@link LtlPricingTerminalInfoEntity}
     */
    LtlPricingTerminalInfoEntity findActiveByProfileDetailId(Long profileDetailId);

    /**
     * Get List of {@link LtlPricingTerminalInfoEntity} which related to specified profile detail id.
     *
     * @param profileDetailId
     *            Profile Detail Id.
     * @return List of {@link LtlPricingTerminalInfoEntity}
     */
    List<LtlPricingTerminalInfoEntity> findByProfileDetailId(Long profileDetailId);

    /**
     * Find all {@link LtlPricingTerminalInfoEntity} which pricingType is 'BLANKET_CSP' and was copied from
     * specified <tt>copiedFrom</tt> value.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlPricingTerminalInfoEntity}.
     */
    List<LtlPricingTerminalInfoEntity> findAllCspChildsCopyedFrom(Long copiedFrom);

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
