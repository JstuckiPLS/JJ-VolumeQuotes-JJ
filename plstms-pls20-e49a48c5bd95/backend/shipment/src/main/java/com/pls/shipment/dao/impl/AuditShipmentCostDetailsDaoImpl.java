package com.pls.shipment.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.AuditShipmentCostDetailsDao;
import com.pls.shipment.domain.AuditShipmentCostDetailsEntity;

/**
 * Implementation of {@link AuditShipmentCostDetailsDao}.
 *
 * @author Brichak Aleksandr
 */
@Repository
@Transactional
public class AuditShipmentCostDetailsDaoImpl extends AbstractDaoImpl<AuditShipmentCostDetailsEntity, Long>
        implements AuditShipmentCostDetailsDao {

    @Override
    public AuditShipmentCostDetailsEntity findAuditShipmentCostDetailsByLoadId(Long loadId) {
        Query query = getCurrentSession().getNamedQuery(AuditShipmentCostDetailsEntity.Q_BY_LOAD_ID);
        query.setParameter("id", loadId);
        return (AuditShipmentCostDetailsEntity) query.uniqueResult();
    }
}
