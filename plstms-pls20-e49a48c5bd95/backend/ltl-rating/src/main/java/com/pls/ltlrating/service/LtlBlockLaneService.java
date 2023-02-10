package com.pls.ltlrating.service;

import java.util.List;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlBlockLaneEntity;
import com.pls.ltlrating.domain.bo.BlanketCarrListItemVO;
import com.pls.ltlrating.domain.bo.BlockLaneListItemVO;

/**
 * Service to handle business logic for blocking lanes of specific/all blanket carriers for particular
 * customer.
 *
 * @author Ashwini Neelgund
 *
 */
public interface LtlBlockLaneService {

    /**
     * Get list of {@link BlockLaneListItemVO} with active status and effective date >= current date.
     *
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<BlockLaneListItemVO> getActiveBlockedLanesByProfileId(Long profileId);

    /**
     * Get list of {@link BlockLaneListItemVO} with inactive status.
     *
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<BlockLaneListItemVO> getExpiredBlockLaneByProfileId(Long profileId);

    /**
     * Get list of {@link BlockLaneListItemVO} with expired effective dates.
     *
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<BlockLaneListItemVO> getInactiveBlockLaneByProfileId(Long profileId);

    /**
     * Get list of {@link BlockLaneListItemVO} with expired effective dates.
     *
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<BlanketCarrListItemVO> getApplicableBlanketCarrListForCust(Long profileId);

    /**
     * Save or update {@link LtlBlockLaneEntity}.
     *
     * @param entity
     *            Not <code>null</code> instance of {@link LtlBlockLaneEntity} which should be saved.
     * @return persisted object of {@link LtlBlockLaneEntity} class.
     * @throws ValidationException
     *             validation exceptions
     */
    LtlBlockLaneEntity saveBlockedLane(LtlBlockLaneEntity entity) throws ValidationException;

    /**
     * Get {@link LtlBlockLaneEntity} object by primary key id.
     *
     * @param id
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link BlockLaneListItemVO} if it was found, otherwise return <code>null</code>
     */
    BlockLaneListItemVO getBlockedLaneById(Long id);

    /**
     * Makes specified records Expired.
     *
     * @param blockedLaneIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of {@link LtlBlockLaneEntity}
     *            which should be expired.
     * @param profileId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO}
     */
    List<BlockLaneListItemVO> expireBlockedLanes(List<Long> blockedLaneIds, Long profileId);

    /**
     * To archive multiple blocked lanes. Return list of active or expired blocked lanes based on the boolean
     * flag "isActiveList". If flag is yes, the blocked lanes are picked from "Active" grid and so need to
     * return updated "Active" list else return updated "Expired" list.
     *
     * @param blockedLaneIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of {@link LtlBlockLaneEntity}
     *            which should be saved.
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @param isActive
     *            Not <code>null</code> instance of {@link Boolean}.
     * @return {@link List} of {@link BlockLaneListItemVO}
     */
    List<BlockLaneListItemVO> inactivateBlockedLanes(List<Long> blockedLaneIds, Long profileId, boolean isActive);

    /**
     * To reactivate multiple blocked lanes. Return updated list of inactive blocked lanes.
     *
     * @param blockedLaneIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of {@link LtlBlockLaneEntity}
     *            which should be saved.
     * @param profileId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link BlockLaneListItemVO}
     */
    List<BlockLaneListItemVO> reactivateBlockedLanes(List<Long> blockedLaneIds, Long profileId);

}
