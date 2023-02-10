package com.pls.location.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.domain.bo.CustomerLocationListItemBO;
import com.pls.core.domain.bo.ShipmentLocationBO;
import com.pls.core.domain.organization.OrganizationLocationEntity;
import com.pls.location.dao.OrganizationLocationDao;

/**
 * Dao implementation for {@link OrganizationLocationDao}.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class OrganizationLocationDaoImpl extends AbstractDaoImpl<OrganizationLocationEntity, Long> implements OrganizationLocationDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerLocationListItemBO> getCustomerLocations(Long organizationId) {
        Query query = getCurrentSession().getNamedQuery(OrganizationLocationEntity.Q_GET_FOR_CUSTOMERS);

        query.setParameter("organizationId", organizationId);
        return query.setResultTransformer(new AliasToBeanResultTransformer(CustomerLocationListItemBO.class)).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentLocationBO> getShipmentLocations(Long customerId, Long personId) {
        Query query = getCurrentSession().getNamedQuery(OrganizationLocationEntity.Q_GET_FOR_SHIPMENT);
        query.setParameter("organizationId", customerId);
        query.setParameter("personId", personId);
        return query.setResultTransformer(new AliasToBeanResultTransformer(ShipmentLocationBO.class)).list();
    }

    @Override
    public Boolean isAEExistsForLocationsExcludeSpecified(Long personId, Long orgId, Long locationId) {
        Query query = getCurrentSession().getNamedQuery(OrganizationLocationEntity.Q_GET_LOCATIONS_FOR_AE);
        query.setParameter("personId", personId);
        query.setParameter("organizationId", orgId);
        query.setParameter("locationId", locationId);

        return (Long) query.uniqueResult() != 0;
    }
}
