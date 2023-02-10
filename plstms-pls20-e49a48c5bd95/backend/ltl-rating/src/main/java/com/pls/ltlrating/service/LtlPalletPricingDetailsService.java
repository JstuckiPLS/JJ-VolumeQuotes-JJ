package com.pls.ltlrating.service;

import java.util.List;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlPalletPricingDetailsEntity;

/**
 * Service that handle business logic and for Ltl Pallet Pricing Details.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPalletPricingDetailsService {

    /**
     * Save list of {@link LtlPalletPricingDetailsEntity}.
     *
     * @param list
     *            Not <code>null</code> implementation of {@link List} which should be saved.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     * @throws ValidationException
     *             validation exceptions
     */
    void saveList(List<LtlPalletPricingDetailsEntity> list, Long profileDetailId) throws ValidationException;

    /**
     * Makes entity active.
     *
     * @param id
     *            Not <code>null</code> instance of {@link Long}. Primary key of object which should become active.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     */
    void activate(Long id, Long profileDetailId);

    /**
     * Makes entity inactive.
     *
     * @param id
     *            Not <code>null</code> instance of {@link Long}. Primary key of object which should become inactive.
     * @param profileDetailId
     *            Not <code>null</code> instance of {@link Long}.
     */
    void inactivate(Long id, Long profileDetailId);

    /**
     * Performs copying entities from one Profile Detail to another.
     *
     * @param copyFromProfileId
     *            Not <code>null</code> instance of {@link Long}.
     * @param copyToProfileId
     *            Not <code>null</code> instance of {@link Long}.
     */
    void copyFrom(Long copyFromProfileId, Long copyToProfileId);

    /**
     * Find entities with active status and effective dates by specified <code>detailId</code>.
     *
     * @param detailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return List of {@link LtlPalletPricingDetailsEntity}
     */
    List<LtlPalletPricingDetailsEntity> findActiveAndEffective(Long detailId);

    /**
     * Find entities with inactive status by specified <code>detailId</code>.
     *
     * @param detailId
     *            Not <code>null</code> instance of {@link Long}.
     * @return List of {@link LtlPalletPricingDetailsEntity}.
     */
    List<LtlPalletPricingDetailsEntity> findInactive(Long detailId);

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
