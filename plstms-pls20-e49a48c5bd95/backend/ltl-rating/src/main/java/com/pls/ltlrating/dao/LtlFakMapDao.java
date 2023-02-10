package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.domain.LtlFakMapEntity;

/**
 * Data Access Object for {@link LtlFakMapEntity}.
 *
 * @author Hima Bindu Challa
 *
 */
public interface LtlFakMapDao extends AbstractDao<LtlFakMapEntity, Long> {

    /**
     * Get a list of {@link LtlFakMapEntity} by specified pricing detail id.
     *
     * @param pricingDetailId
     *            Not <code>null</code> value of {@link Long}
     * @return {@link List} of {@link LtlFakMapEntity}.
     */
    List<LtlFakMapEntity> findByPricingDetailId(Long pricingDetailId);
}
