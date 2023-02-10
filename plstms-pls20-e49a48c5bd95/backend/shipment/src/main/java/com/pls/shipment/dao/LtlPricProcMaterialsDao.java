package com.pls.shipment.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.shipment.domain.LtlPricPropMaterialsEntity;

/**
 * Data Access Object for {@link LtlPricPropMaterialsEntity} data.
 *
 * @author Ashwini Neelgund
 */
public interface LtlPricProcMaterialsDao extends AbstractDao<LtlPricPropMaterialsEntity, Long> {

    /**
     * Update {@link LtlPricPropMaterialsEntity#getStatus()} to Inactive by Load ID.
     *
     * @param loadId
     *            ID of Load
     */
    void inactivatePricingProposalMaterials(Long loadId);

}
