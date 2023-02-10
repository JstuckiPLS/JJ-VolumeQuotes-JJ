package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.DictionaryDaoImpl;
import com.pls.ltlrating.dao.LtlPricingTypesDao;
import com.pls.ltlrating.domain.LtlPricingTypesEntity;

/**
 * {@link LtlPricingTypesDao} implementation.
 *
 * @author Hima Bindu Challa
 */
@Transactional
@Repository
public class LtlPricingTypesDaoImpl extends DictionaryDaoImpl<LtlPricingTypesEntity> implements LtlPricingTypesDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<LtlPricingTypesEntity> findAllByGroup(String group) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingTypesEntity.GET_BY_GROUP);
        query.setParameter("groupType", group);
        return query.list();
    }

    @Override
    public LtlPricingTypesEntity findByName(String name) {
        Query query = getCurrentSession().getNamedQuery(LtlPricingTypesEntity.GET_BY_TYPE);
        query.setParameter("ltlPricingType", name);
        return (LtlPricingTypesEntity) query.uniqueResult();
    }
}
