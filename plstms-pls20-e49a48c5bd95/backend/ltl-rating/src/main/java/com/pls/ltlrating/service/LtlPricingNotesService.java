package com.pls.ltlrating.service;

import java.util.List;

import com.pls.core.service.validation.ValidationException;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;

/**
 * Service that handle business logic and for Ltl Pricing Notes.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPricingNotesService {

    /**
     * Save and update {@link LtlPricingNotesEntity}.
     *
     * @param entity
     *            Not <code>null</code> instance of {@link LtlPricingNotesEntity} which should be saved.
     * @throws ValidationException
     *             validation exceptions
     */
    void saveNotes(LtlPricingNotesEntity entity) throws ValidationException;

    /**
     * Get {@link LtlPricingNotesEntity} object by pricing detail id.
     *
     * @param id
     *            Not <code>null</code> positive {@link Long}.
     * @return instance of {@link LtlPricingNotesEntity} if it was found, otherwise return <code>null</code>
     */
    LtlPricingNotesEntity getNotesById(Long id);

    /**
     * Get list of {@link LtlPricingNotesEntity} by specified profile id.
     *
     * @param profileId
     *            Not <code>null</code> positive value of {@link Long}.
     * @return {@link List} of {@link LtlPricingNotesEntity} if it was found, otherwise return empty
     *         {@link List}.
     */
    List<LtlPricingNotesEntity> getNotesByProfileId(Long profileId);
}
