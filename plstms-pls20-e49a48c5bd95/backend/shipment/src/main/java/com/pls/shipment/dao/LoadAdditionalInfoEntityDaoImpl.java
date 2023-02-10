package com.pls.shipment.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.domain.LoadAdditionalInfoEntity;

/**
 * Implementation for DAO LoadAdditionalInfoEntity.
 * 
 * @author Sergii Belodon
 */
@Repository
@Transactional
public class LoadAdditionalInfoEntityDaoImpl extends AbstractDaoImpl<LoadAdditionalInfoEntity, Long> implements LoadAdditionalInfoEntityDao {

    @Override
    public LoadAdditionalInfoEntity getAdditionalInfoByLoadId(Long loadId) {
        Query query = getCurrentSession().getNamedQuery(LoadAdditionalInfoEntity.Q_GET_ADDITIONAL_INFO_BY_LOAD_ID);
        query.setParameter("loadId", loadId);
        return (LoadAdditionalInfoEntity) query.uniqueResult();
    }

}
