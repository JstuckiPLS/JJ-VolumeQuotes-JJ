package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;
import com.pls.ltlrating.domain.bo.GuaranteedPriceListItemVO;

/**
 * Data Access Object for {@link LtlGuaranteedPriceEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface LtlGuaranteedPriceDao extends AbstractDao<LtlGuaranteedPriceEntity, Long> {

    /**
     * Get list of {@link LtlGuaranteedPriceEntity} by specified profileDetailId.
     *
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link LtlGuaranteedPriceEntity} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<LtlGuaranteedPriceEntity> findByProfileDetailId(Long profileDetailId);

    /**
     * To get All active and effective {@link LtlGuaranteedPriceEntity} for the given profile Id.
     *
     * @param profileDetailId
     *            - Not <code>null</code> {@link Long}
     * @return {@link List} of {@link LtlGuaranteedPriceEntity} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<LtlGuaranteedPriceEntity> findActiveAndEffectiveEntitiesForProfile(Long profileDetailId);

    /**
     * To get All {@link GuaranteedPriceListItemVO} irrespective expiration date for the given status and
     * profile Id. Will return all active profiles or all inactive profiles.
     *
     * @param profileDetailId
     *            - Not <code>null</code> {@link Long}
     * @param status
     *            - Status of the {@link LtlGuaranteedPriceEntity} - Active/Inactive
     * @return {@link List} of {@link GuaranteedPriceListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<GuaranteedPriceListItemVO> findByStatusAndProfileDetailId(Status status, Long profileDetailId);

    /**
     * To get All active and effective {@link GuaranteedPriceListItemVO} for the given profile Id.
     *
     * @param profileDetailId
     *            - Not <code>null</code> {@link Long}
     * @return {@link List} of {@link GuaranteedPriceListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<GuaranteedPriceListItemVO> findActiveAndEffectiveByProfileDetailId(Long profileDetailId);

    /**
     * To get All active and expired {@link GuaranteedPriceListItemVO} for the given profile Id.
     *
     * @param profileDetailId
     *            - Not <code>null</code> {@link Long}
     * @return {@link List} of {@link GuaranteedPriceListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<GuaranteedPriceListItemVO> findExpiredByProfileDetailId(Long profileDetailId);

    /**
     * Update a status of {@link LtlGuaranteedPriceEntity} by given list of ids.
     *
     * @param guaranteedIds
     *            - Not <code>null</code> and not empty {@link List} of {@link Long}.
     * @param status
     *            - {@link Status}
     * @param modifiedBy
     *            - The user who modified or inactivated/reactivated the accessorial
     */
    void updateStatusOfGuaranteedPriceList(List<Long> guaranteedIds, Status status, Long modifiedBy);

    /**
     * Inactivate all active and effective {@link LtlGuaranteedPriceEntity} entities by specified profileId.
     *
     * @param profileId
     *            Not <code>null</code> value of {@link Long}.
     * @param modifiedBy
     *            Not <code>null</code> value of {@link Long}.
     */
    void updateStatusToInactiveByProfileId(Long profileId, Long modifiedBy);

    /**
     * Makes specified records Expired.
     *
     * @param ids
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlGuaranteedPriceEntity} which should be expired.
     * @param modifiedBy
     *            The user who modified or inactivated/reactivated the price detail.
     */
    void expireByListOfIds(List<Long> ids, Long modifiedBy);

    /**
     * Find all {@link LtlGuaranteedPriceEntity} which pricingType is 'BLANKET_CSP' and was copied from
     * specified <tt>copiedFrom</tt> value.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlGuaranteedPriceEntity}.
     */
    List<LtlGuaranteedPriceEntity> findAllCspChildsCopyedFrom(Long copiedFrom);

    /**
     * Makes all child Customer Specific Prices expire.
     *
     * @param copiedFromIds
     *              Ids of profile detail from which child CSPs were copied. (It's a link to parent entity).
     *              Not <code>null</code> value of {@link Long}.
     * @param modifiedBy
     *              Not <code>null</code> value of {@link Long}.
     */
    void expirateCSPByCopiedFrom(List<Long> copiedFromIds, Long modifiedBy);

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
}
