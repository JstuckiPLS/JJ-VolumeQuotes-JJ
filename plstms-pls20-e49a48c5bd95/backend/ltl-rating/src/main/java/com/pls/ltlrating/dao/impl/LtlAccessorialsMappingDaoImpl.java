package com.pls.ltlrating.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.LtlAccessorialsMappingDao;
import com.pls.ltlrating.domain.LtlAccessorialsMappingEntity;

/**
 * Implementation of {@link LtlAccessorialsMappingDao}.
 *
 * @author Dmitriy Davydenko.
 */
@Transactional
@Repository
public class LtlAccessorialsMappingDaoImpl extends AbstractDaoImpl<LtlAccessorialsMappingEntity, Long> implements LtlAccessorialsMappingDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlAccessorialsMappingEntity> getAccessorialsMapping(Long carrierId) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsMappingEntity.GET_ACCESSORIALS_MAPPING);
        query.setParameter("carrierId", carrierId);

        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LtlAccessorialsMappingEntity> getAccessorialsMappingBySCAC(String scac) {
        Query query = getCurrentSession().getNamedQuery(LtlAccessorialsMappingEntity.GET_ACCESSORIALS_MAPPING_BY_SCAC);
        query.setParameter("scac", scac);
        return query.list();
    }
}
