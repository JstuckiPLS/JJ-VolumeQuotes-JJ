package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.domain.LtlPricingNotesEntity;

/**
 * Data Access Object for {@link LtlPricingNotesEntity}.
 *
 * @author Artem Arapov
 *
 */
public interface LtlPricingNotesDao extends AbstractDao<LtlPricingNotesEntity, Long> {

    /**
     * Get a list of {@link LtlPricingNotesEntity} by specified profile id.
     *
     * @param profileId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link LtlPricingNotesEntity}.
     */
    List<LtlPricingNotesEntity> findByProfileId(Long profileId);
}
