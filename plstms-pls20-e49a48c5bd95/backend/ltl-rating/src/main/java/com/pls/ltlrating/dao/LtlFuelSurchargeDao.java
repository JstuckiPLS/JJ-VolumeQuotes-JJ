package com.pls.ltlrating.dao;

import java.math.BigDecimal;
import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;

/**
 *
 * DAO for {@link LtlFuelSurchargeEntity}.
 *
 * @author Stas Norochevskiy
 *
 */
public interface LtlFuelSurchargeDao extends AbstractDao<LtlFuelSurchargeEntity, Long> {

    /**
     * Retrieve Active Fuel Surcharge and also to export fuel surcharge.
     *
     * @param profileDetailId profile detail
     * @return fuel surcharge for give profile datails
     */
    List<LtlFuelSurchargeEntity> getActiveFuelSurchargeByProfileDetailId(Long profileDetailId);

    /**
     * query LTL_FUEL_SURCHARGE table using this FUEL_CHARGE value and get the SURCHARGE like “SELECT
     * SURCHARGE FROM LTL_FUEL_SURCHARGE WHERE MIN_RATE <= <<FUEL_CHARGE>> AND MAX_RATE >= <<FUEL_CHARGE>>”.
     *
     * @param charge
     *            - Not <code>null</code> instance of {@link BigDecimal}
     * @return proper surcharge value
     */
    BigDecimal getFuelSurchargeByFuelCharge(BigDecimal charge);

    /**
     * Inactivate all active and effective {@link LtlFuelSurchargeEntity} entities by specified profileId.
     *
     * @param profileId
     *            profile to inactive fuels
     * @param modifiedBy
     *            user who made inactivateion
     */
    void updateStatusToInactiveByProfileId(Long profileId, Long modifiedBy);

    /**
     * Update status by specified list of ids.
     *
     * @param ids
     *          List of ids of {@link LtlFuelSurchargeEntity} which status should be updated.
     *          Not <code>null</code>.
     * @param status
     *          New status value. {@link Status}.
     *          Not <code>null</code>.
     * @param modifiedBy
     *          Person Id value.
     *          Not <code>null</code>.
     */
    void updateStatusByListOfIds(List<Long> ids, Status status, Long modifiedBy);

    /**
     * Find all {@link LtlFuelSurchargeEntity} which pricingType is 'BLANKET_CSP' and was copied from
     * specified <tt>copiedFrom</tt> value.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlFuelSurchargeEntity}.
     */
    List<LtlFuelSurchargeEntity> findAllCspChildsCopyedFrom(Long copiedFrom);

    /**
     * Updates status in child Customer Specific Price entities.
     *
     * @param copiedFromIds
     *              Ids of profile detail from which child CSPs were copied. (It's a link to parent entity).
     *              Not <code>null</code> value of {@link Long}.
     * @param status
     *              Value of {@link Status} enumeration.
     *              Not <code>null</code>.
     * @param modifiedBy
     *              Not <code>null</code> value of {@link Long}.
     */
    void updateStatusInCSPByCopiedFrom(List<Long> copiedFromIds, Status status, Long modifiedBy);

    /**
     * Updates status in child Customer Specific Price entities.
     *
     * @param profileDetailId
     *            Id of profile detail which child CSPs should be inactivated.
     * @param modifiedBy
     *            Not <code>null</code> value of {@link Long}.
     */
    void inactivateCSPByProfileDetailId(Long profileDetailId, Long modifiedBy);

    /**
     * Retrieve All entities for a specific profile.
     *
     * @param profileDetailId ID of profile details
     * @return list of {@link LtlFuelSurchargeEntity} object with specified profile details ID
     */
    List<LtlFuelSurchargeEntity> getAllByProfileDetailId(Long profileDetailId);
}
