package com.pls.shipment.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.LoadPricingDetailsDao;
import com.pls.shipment.domain.LoadPricingDetailsEntity;

/**
 * {@link LoadPricingDetailsEntity} implementation.
 * 
 * @author Ashwini Neelgund
 * 
 */
@Repository
@Transactional
public class LoadPricingDetailsDaoImpl extends AbstractDaoImpl<LoadPricingDetailsEntity, Long>
        implements LoadPricingDetailsDao {

    @Override
    public void delete(LoadPricingDetailsEntity loadPricDetail) {
        getCurrentSession().delete("LoadPricingDetailsEntity", loadPricDetail);
    }

    @Override
    public LoadPricingDetailsEntity getShipmentPricingDetails(Long shipmentId) {
        return (LoadPricingDetailsEntity) getCurrentSession()
                .getNamedQuery(LoadPricingDetailsEntity.Q_LOAD_PRICING_DETAILS).setParameter("loadId", shipmentId)
                .uniqueResult();
    }

}
