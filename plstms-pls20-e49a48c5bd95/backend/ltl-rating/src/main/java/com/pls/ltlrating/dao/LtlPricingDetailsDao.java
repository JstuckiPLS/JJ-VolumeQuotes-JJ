package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlPricingDetailsEntity;
import com.pls.ltlrating.domain.bo.PricingDetailListItemVO;

/**
 * Data Access Object for {@link LtlPricingDetailsEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPricingDetailsDao extends AbstractDao<LtlPricingDetailsEntity, Long> {

    /**
     * Get all inactive(archived) pricing details.
     *
     * @param profileDetailId
     *            - Not <code>null</code> {@link Long} LTL Profile Detail Id (Buy/Sell/None) to which the
     *            Pricing Detail should be retrieved.
     * @return List of {@link PricingDetailListItemVO} for selected profile.
     */
    List<PricingDetailListItemVO> findArchivedPrices(Long profileDetailId);

    /**
     * To get All active and effective {@link LtlPricingDetailsEntity} for the given profile Id.
     *
     * @param profileDetailId
     *            - Not <code>null</code> {@link Long} LTL Profile Detail Id (Buy/Sell/None) to which the
     *            Pricing Detail should be retrieved.
     * @return List of {@link LtlPricingDetailsEntity} for selected profile.
     */
    List<LtlPricingDetailsEntity> findActiveAndEffectiveForProfile(Long profileDetailId);

    /**
     * To get All active and effective {@link LtlPricingDetailsEntity} for the given profile Id.
     *
     * @param profileDetailId
     *            - Not <code>null</code> {@link Long} LTL Profile Detail Id (Buy/Sell/None) to which the
     *            Pricing Detail should be retrieved.
     * @return List of {@link PricingDetailListItemVO} for selected profile.
     */
    List<PricingDetailListItemVO> findActiveAndEffectiveByProfileDetailId(Long profileDetailId);

    /**
     * To get All active and expired {@link LtlPricingDetailsEntity} for the given profile Id.
     *
     * @param profileDetailId
     *            - Not <code>null</code> {@link Long} LTL Profile Detail Id (Buy/Sell/None) to which the
     *            Pricing Detail should be retrieved.
     * @return List of {@link PricingDetailListItemVO} for selected profile.
     */
    List<PricingDetailListItemVO> findExpiredByProfileDetailId(Long profileDetailId);

    /**
     * This method is for updating status - mainly while inactivating or reactivating the
     * {@link LtlPricingDetailsEntity}.
     *
     * @param priceDetailIds
     *            -  {@link List} of {@link Long} with id's of
     *            {@link LtlPricingDetailsEntity} which should be updated.
     * @param status
     *            - status of {@link LtlPricingDetailsEntity}. Active/Inactive.
     * @param modifiedBy
     *            - The user who modified or inactivated/reactivated the accessorial
     */
    void updateStatus(List<Long> priceDetailIds, Status status, Long modifiedBy);

    /**
     * Inactivate all active and effective {@link LtlPricingDetailsEntity} entities by specified profileId.
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
     * @param priceDetailIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlPricingDetailsEntity} which should be expired.
     * @param modifiedBy
     *            The user who modified or inactivated/reactivated the price detail.
     */
    void updateStatusToExpired(List<Long> priceDetailIds, Long modifiedBy);

    /**
     * Find all {@link LtlPricingDetailsEntity} which were copied from specified.
     *
     * @param copiedFrom
     *            Not <code>null</code> value of {@link Long}.
     * @return List of {@link LtlPricingDetailsEntity}.
     */
    List<LtlPricingDetailsEntity> findAllByCopiedFrom(Long copiedFrom);

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
}
