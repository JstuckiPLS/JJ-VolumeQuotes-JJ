package com.pls.ltlrating.service;

import java.util.List;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity;
import com.pls.ltlrating.domain.bo.GuaranteedPriceListItemVO;

/**
 * Service that handle business logic and for Ltl Guaranteed Price.
 *
 * @author Artem Arapov
 *
 */
public interface LtlGuaranteedPriceService {

    /**
     * Save and update {@link LtlGuaranteedPriceEntity}.
     *
     * @param entity
     *            Not <code>null</code> instance of {@link LtlGuaranteedPriceEntity} which should be saved.
     * @return persisted object of {@link LtlGuaranteedPriceEntity} class.
     * @throws ValidationException
     *             validation exceptions
     */
    LtlGuaranteedPriceEntity saveGuaranteedPrice(LtlGuaranteedPriceEntity entity) throws ValidationException;

    /**
     * Get {@link LtlGuaranteedPriceEntity} object by pricing detail id.
     *
     * @param id
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlGuaranteedPriceEntity} if it was found, otherwise return
     *         <code>null</code>
     */
    LtlGuaranteedPriceEntity getGuaranteedPriceById(Long id);

    /**
     * Get list of {@link LtlGuaranteedPriceEntity} by specified profile id.
     *
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link LtlGuaranteedPriceEntity} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<LtlGuaranteedPriceEntity> getAllGuaranteedByProfileDetailId(Long profileDetailId);

    /**
     * Get list of {@link GuaranteedPriceListItemVO} with active status and effective date >= current date.
     *
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link GuaranteedPriceListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<GuaranteedPriceListItemVO> getActiveGuaranteedByProfileDetailId(Long profileDetailId);

    /**
     * Get list of {@link GuaranteedPriceListItemVO} with inactive status.
     *
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link GuaranteedPriceListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<GuaranteedPriceListItemVO> getInactiveGuaranteedByProfileDetailId(Long profileDetailId);

    /**
     * Get list of {@link GuaranteedPriceListItemVO} with expired effective dates.
     *
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link GuaranteedPriceListItemVO} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<GuaranteedPriceListItemVO> getExpiredGuaranteedByProfileDetailId(Long profileDetailId);

    /**
     * To archive multiple guaranteed price. Return list of active or expired guaranteed price based on the
     * boolean flag "isActiveList". If flag is yes, the guaranteed price are picked from "Active" grid and so
     * need to return updated "Active" list using method
     * "getActiveGuaranteedByProfileDetailId(Long profileDetailId);" else return updated "Expired" list using
     * method "getExpiredGuaranteedByProfileDetailId(Long profileDetailId);" method. In UI, "Edit" and
     * "Archive" fields should be disabled when user selects "Archived" tab.
     *
     * @param guaranteedIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlGuaranteedPriceEntity} which should be saved.
     * @param profileDetailId
     *            Not <code>null</code> positive value of {@link Long}.
     * @param isActiveList
     *            Not <code>null</code> instance of {@link Boolean}.
     * @return {@link List} of {@link GuaranteedPriceListItemVO}
     */
    List<GuaranteedPriceListItemVO> inactivateGuaranteedPricings(List<Long> guaranteedIds, Long profileDetailId,
            boolean isActiveList);

    /**
     * To reactivate multiple guaranteed price. Return updated list of inactive using method
     * {@link LtlGuaranteedPriceService#getInactiveGuaranteedByProfileDetailId(Long)}.
     *
     * @param guaranteedIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlGuaranteedPriceEntity} which should be saved.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link GuaranteedPriceListItemVO}
     */
    List<GuaranteedPriceListItemVO> reactivateGuaranteedPricings(List<Long> guaranteedIds, Long profileDetailId);

    /**
     * Makes specified records Expired.
     *
     * @param guaranteedIds
     *            Not <code>null</code> {@link List} of {@link Long} with id's of
     *            {@link LtlGuaranteedPriceEntity} which should be expired.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return {@link List} of {@link GuaranteedPriceListItemVO}
     */
    List<GuaranteedPriceListItemVO> expireGuaranteedPricings(List<Long> guaranteedIds, Long profileDetailId);

    /**
     * Copying list of {@link LtlGuaranteedPriceEntity} from profile specified by copyFromProfileId argument to
     * profile specified by copyToProfileId.
     *
     * @param copyFromProfileId
     *            Not <code>null</code> value of {@link Long}
     * @param copyToProfileId
     *            Not <code>null</code> value of {@link Long}
     * @param shouldCopyToCSP
     *            Should copying to child CSP profiles
     */
    void copyFrom(Long copyFromProfileId, Long copyToProfileId, boolean shouldCopyToCSP);

}
