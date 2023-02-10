package com.pls.core.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.NetworkDao;
import com.pls.core.domain.bo.NetworkListItemBO;
import com.pls.core.domain.bo.SimpleValue;
import com.pls.core.domain.bo.UnitAndCostCenterCodesBO;
import com.pls.core.domain.organization.NetworkEntity;

/**
 * DAO implementation for {@link NetworkEntity}..
 * 
 * @author Brichak Aleksandr
 */
@Transactional
@Repository
public class NetworkDaoImpl extends AbstractDaoImpl<NetworkEntity, Long> implements NetworkDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<NetworkListItemBO> getAllNetworks() {
        return (List<NetworkListItemBO>) getCurrentSession().getNamedQuery(NetworkEntity.Q_GET_ALL_NETWORK)
                .setResultTransformer(new AliasToBeanResultTransformer(NetworkListItemBO.class)).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SimpleValue> getActiveNetworksByUser(Long personId) {
        Query query = getCurrentSession().getNamedQuery(NetworkEntity.Q_GET_ACTIVE_NETWORKS_BY_USER);
        query.setParameter("personId", personId);
        return (List<SimpleValue>) query.list();
    }

    @Override
    public UnitAndCostCenterCodesBO getUnitAndCostCenterCodes(Long orgId) {
        Query query = getCurrentSession().getNamedQuery(NetworkEntity.Q_GET_UNIT_AND_COST_CENTER_CODES);
        query.setLong("orgId", orgId);
        return (UnitAndCostCenterCodesBO) query.setResultTransformer(
                new AliasToBeanResultTransformer(UnitAndCostCenterCodesBO.class)).uniqueResult();
    }
}
