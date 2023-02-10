package com.pls.shipment.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.LtlPricProcMaterialsDao;
import com.pls.shipment.domain.LtlPricPropMaterialsEntity;

/**
 * Dao Implementation class for LtlPricPropMaterialsEntity {@link com.pls.shipment.dao.LtlPricProcMaterialsDao}.
 *
 * @author Ashwini Neelgund
 */
@Repository
@Transactional
public class LtlPricProcMaterialsDaoImpl extends AbstractDaoImpl<LtlPricPropMaterialsEntity, Long> implements LtlPricProcMaterialsDao {

    @Override
    public void inactivatePricingProposalMaterials(Long loadId) {
        getCurrentSession().getNamedQuery(LtlPricPropMaterialsEntity.U_INACTIVATE_FOR_LOAD).setLong("loadId", loadId).executeUpdate();
    }
}
