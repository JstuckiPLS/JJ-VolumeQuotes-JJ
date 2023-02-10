package com.pls.ltlrating.service;

import java.util.List;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.bo.ProhibitedNLiabilitiesVO;

/**
 * Service that handle business logic and for Ltl Carrier Liabiilities.
 *
 * @author Artem Arapov
 *
 */
public interface LtlCarrierLiabilitiesService {

    /**
     * Save a list of {@link LtlCarrierLiabilitiesEntity} and return list of saved entities.
     *
     * @param list
     *            Not <code>null</code> {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     * @param profileId
     *            Profile for which the liabilities belong to
     * @return Not <code>null</code> {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     */
    List<LtlCarrierLiabilitiesEntity> saveCarrierLiabilities(List<LtlCarrierLiabilitiesEntity> list, Long profileId);

    /**
     * Get {@link LtlCarrierLiabilitiesEntity} by specified primary key <code>id</code> value.
     *
     * @param id
     *            Not <code>null</code> value of {@link Long}
     * @return appropriate {@link LtlCarrierLiabilitiesEntity} if it was found, otherwise return null.
     */
    LtlCarrierLiabilitiesEntity getCarrierLiabilitiesById(Long id);

    /**
     * Get a list of {@link LtlCarrierLiabilitiesEntity} by specified profile id.
     *
     * @param profileId
     *            Not <code>null</code> value of {@link Long}.
     * @return {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     */
    List<LtlCarrierLiabilitiesEntity> getCarrierLiabilitiesByProfileId(Long profileId);

    /**
     * Save prohibited commodities and liabilities for a profile and return list of saved liabilities. This method is required as they both has to be
     * saved in the same transaction.
     *
     * @param vo
     *            VO with prohibited commodities and liabilities to save.
     * @return Not <code>null</code> {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     * @throws ValidationException
     *             validation exceptions
     */
    List<LtlCarrierLiabilitiesEntity> saveProhibitedNLiabilities(ProhibitedNLiabilitiesVO vo) throws ValidationException;

    /**
     * Clone the Liabilities from "copyFromProfileDetailId" to "copyToProfileDetailId" and save the same.
     *
     * @param copyFromProfileDetailId
     *            - The profile detail id from which the Liabilities should be copied
     * @param copyToProfileDetailId
     *            - The profile detail id to which the Liabilities should be copied
     * @return {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     * @throws ValidationException
     *             validation exceptions
     */
    List<LtlCarrierLiabilitiesEntity> cloneLiabilities(Long copyFromProfileDetailId, Long copyToProfileDetailId) throws ValidationException;

    /**
     * Clone the Prohibited Commodities from "copyFromProfileDetailId" to "copyToProfileDetailId" and save the same.
     *
     * @param copyFromProfileDetailId
     *            - The profile detail id from which the Prohibited Commodities should be copied
     * @param copyToProfileDetailId
     *            - The profile detail id to which the Prohibited Commodities should be copied
     * @return updated {@link LtlPricingProfileEntity}.
     * @throws ValidationException
     *             validation exceptions
     */
    LtlPricingProfileEntity cloneProhibitedCommodities(Long copyFromProfileDetailId, Long copyToProfileDetailId) throws ValidationException;
}
