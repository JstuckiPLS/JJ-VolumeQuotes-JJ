package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.domain.LtlCarrierLiabilitiesEntity;

/**
 * Data Access Object for {@link LtlCarrierLiabilitiesEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface LtlCarrierLiabilitiesDao extends AbstractDao<LtlCarrierLiabilitiesEntity, Long> {

    /**
     * Get a list of {@link LtlCarrierLiabilitiesEntity} by specified profile id.
     *
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     */
    List<LtlCarrierLiabilitiesEntity> findCarrierLiabilitiesByProfileId(Long profileId);

    /**
     * Save a list of {@link LtlCarrierLiabilitiesEntity} and return list of saved entities.
     *
     * @param list
     *            Not <code>null</code> {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     * @return Not <code>null</code> {@link List} of {@link LtlCarrierLiabilitiesEntity}.
     *
     */
    List<LtlCarrierLiabilitiesEntity> saveCarrierLiabilitiesList(List<LtlCarrierLiabilitiesEntity> list);

    /**
     * Delete all liabilities for a profile.
     *
     * @param profileId
     *            Profile for which liabilities are to be deleted
     */
    void deleteLiabilities(Long profileId);
}
