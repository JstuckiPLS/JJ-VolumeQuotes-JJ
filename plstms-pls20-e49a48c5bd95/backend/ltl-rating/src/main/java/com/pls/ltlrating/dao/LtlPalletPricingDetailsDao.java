package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;

/**
 * Data Access Object for {@link LtlPalletPricingDetailsEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPalletPricingDetailsDao extends AbstractDao<LtlPalletPricingDetailsEntity, Long> {

    /**
     * Find {@link LtlPalletPricingDetailsEntity} by specified <code>detailId</code> and <code>status</code>.
     *
     * @param detailId
     *            Not <code>null</code> instance of {@link Long}.
     * @param status
     *            Value of {@link Status}.
     * @return List of {@link LtlPalletPricingDetailsEntity}.
     */
    List<LtlPalletPricingDetailsEntity> findByStatusAndDetailId(Long detailId, Status status);

    /**
     * Find {@link LtlPalletPricingDetailsEntity} with active status and effective dates by specified
     * <code>detailId</code>.
     *
     * @param detailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return List of {@link LtlPalletPricingDetailsEntity}.
     */
    List<LtlPalletPricingDetailsEntity> findActiveAndEffective(Long detailId);

    /**
     * Update status of {@link LtlPalletPricingDetailsEntity} by specified <code>priceDetailId</code> and
     * <code>status</code>.
     *
     * @param priceDetailId
     *            Primary key of entity which should be updated. Not <code>null</code> instance of
     *            {@link Long}.
     * @param status
     *            Value of {@link Status}.
     * @param modifiedBy
     *            <code>personId</code> which should be given from session context. Not <code>null</code>
     *            instance of {@link Long}.
     */
    void updateStatus(Long priceDetailId, Status status, Long modifiedBy);

    /**
     * Inactivate all existed entities by specified <code>detailId</code>.
     *
     * @param detailId
     *            Not <code>null</code> instance of {@link Long}.
     * @param modifiedBy
     *            <code>personId</code> which should be given from session context. Not <code>null</code>
     *            instance of {@link Long}.
     */
    void inactivateByDetailId(Long detailId, Long modifiedBy);

    /**
     * Clone entities from one Profile Detail to another.
     *
     * @param copyFromDetailId
     *            Profile Detail from which clone operation should be performed. Not <code>null</code>
     *            instance of {@link Long}.
     * @param copyToDetailId
     *            Profile Detail from which clone operation should be performed. Not <code>null</code>
     *            instance of {@link Long}.
     */
    void clone(Long copyFromDetailId, Long copyToDetailId);

    /**
     * Find all {@link LtlPalletPricingDetailsEntity} which pricingType is 'BLANKET_CSP' and was copied from
     * specified <tt>copiedFrom</tt> value.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlPalletPricingDetailsEntity}.
     */
    List<LtlPalletPricingDetailsEntity> findAllCspChildsCopyedFrom(Long copiedFrom);

    /**
     * Updates status in child Customer Specific Price entities.
     *
     * @param copiedFromId
     *              Id of profile detail from which child CSPs were copied. (It's a link to parent entity).
     *              Not <code>null</code> value of {@link Long}.
     * @param status
     *              Value of {@link Status} enumeration.
     *              Not <code>null</code>.
     * @param modifiedBy
     *              Not <code>null</code> value of {@link Long}.
     */
    void updateStatusInCSPByCopiedFrom(Long copiedFromId, Status status, Long modifiedBy);

    /**
     * Check if all the zones are available to copy the pallet details.
     *
     * @param copyFromProfileId
     *            Not <code>null</code> instance of {@link Long}.
     * @param copyToProfileId
     *            Not <code>null</code> instance of {@link Long}.
     * @return true if any zones are missing else false
     */
    boolean areZonesMissing(Long copyFromProfileId, Long copyToProfileId);
}
