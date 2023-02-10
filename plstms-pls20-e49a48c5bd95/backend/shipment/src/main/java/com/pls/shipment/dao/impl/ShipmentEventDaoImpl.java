package com.pls.shipment.dao.impl;

import java.util.List;

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.ShipmentEventDao;
import com.pls.shipment.domain.LoadEventEntity;
import com.pls.shipment.domain.bo.ShipmentEventBO;

/**
 * {@link com.pls.shipment.dao.ShipmentEventDao} implementation.
 * 
 * @author Gleb Zgonikov
 */
@Repository
@Transactional
public class ShipmentEventDaoImpl extends AbstractDaoImpl<LoadEventEntity, Long> implements ShipmentEventDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentEventBO> findShipmentEvents(Long shipmentId) {
        return getCurrentSession().getNamedQuery(LoadEventEntity.Q_BY_LOAD_ID).setLong("loadId", shipmentId)
                .setResultTransformer(new AliasToBeanResultTransformer(ShipmentEventBO.class)).list();
    }

}
