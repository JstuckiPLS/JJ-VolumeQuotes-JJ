package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.core.shared.Status;
import com.pls.ltlrating.domain.LtlBlockLaneEntity;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;
import com.pls.ltlrating.domain.bo.BlanketCarrListItemVO;
import com.pls.ltlrating.domain.bo.BlockLaneListItemVO;

/**
 * Data Access Object for {@link LtlBlockLaneEntity}.
 *
 * @author Ashwini Neelgund
 *
 */
public interface LtlBlockLaneDao extends AbstractDao<LtlBlockLaneEntity, Long> {

    /**
     * To get All active and effective {@link BlockLaneListItemVO} for the given profile Id.
     *
     * @param profileId
     *            - Not <code>null</code> {@link Long}
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<BlockLaneListItemVO> findActiveAndEffectiveByProfileId(Long profileId);

    /**
     * To get All {@link BlockLaneListItemVO} irrespective expiration date for the given status and profile
     * Id. Will return all active profiles or all inactive profiles.
     *
     * @param profileId
     *            - Not <code>null</code> {@link Long}
     * @param status
     *            - Status of the {@link LtlGuaranteedPriceEntity} - Active/Inactive
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<BlockLaneListItemVO> findByStatusAndProfileId(String status, Long profileId);

    /**
     * To get All active and expired {@link BlockLaneListItemVO} for the given profile Id.
     *
     * @param profileId
     *            - Not <code>null</code> {@link Long}
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<BlockLaneListItemVO> findExpiredByProfileId(Long profileId);

    /**
     * To get list of applicable blanket carrier profiles {@link BlanketCarrListItemVO} for a customer.
     *
     * @param profileId
     *            - Not <code>null</code> {@link Long}
     * @return {@link List} of {@link BlanketCarrListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<BlanketCarrListItemVO> getUnblockedBlanketCarrListForCust(Long profileId);

    /**
     * Makes specified records Expired.
     *
     * @param blockedLaneIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of {@link LtlBlockLaneEntity}
     *            which should be expired.
     * @param personId
     *            The user who modified or inactivated/reactivated the blocked lanes.
     */
    void expireBlockedLanes(List<Long> blockedLaneIds, Long personId);

    /**
     * Update a status of {@link LtlBlockLaneEntity} by given list of ids.
     *
     * @param blockedLaneIds
     *            - Not <code>null</code> and not empty {@link List} of {@link Long}.
     * @param status
     *            - {@link Status}
     * @param personId
     *            - The user who modified or inactivated/reactivated the blocked lanes.
     */
    void updateStatusOfBlockedLanes(List<Long> blockedLaneIds, Status status, Long personId);

    /**
     * Get {@link BlockLaneListItemVO} object by primary key id.
     *
     * @param id
     *            id - Not <code>null</code> positive value of {@link Long}.
     * @return instance of {@link BlockLaneListItemVO} if it was found, otherwise return <code>null</code>
     */
    BlockLaneListItemVO findById(Long id);

}
