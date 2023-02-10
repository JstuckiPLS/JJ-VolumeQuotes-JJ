package com.pls.extint.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.extint.dao.ApiExceptionsDao;
import com.pls.extint.domain.ApiExceptionEntity;
import com.pls.extint.shared.ApiCriteriaCO;

/**
 * Implementation class of {@link ApiExceptionsDao}.
 * 
 * @author Pavani Challa
 * 
 */
@Transactional
@Repository
public class ApiExceptionsDaoImpl extends AbstractDaoImpl<ApiExceptionEntity, Long> implements ApiExceptionsDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<ApiExceptionEntity> findByCriteria(ApiCriteriaCO criteriaCO) {
        Criteria criteria = getCurrentSession().createCriteria(ApiExceptionEntity.class);
        if (criteriaCO.getFromDate() != null && criteriaCO.getToDate() != null) {
            criteria.add(Restrictions.between("modification.createdDate", criteriaCO.getFromDate(), criteriaCO.getToDate()));
        }
        if (criteriaCO.getCarrierReferenceNumber() != null) {
            criteria.add(Restrictions.eq("carrierReferenceNumber", criteriaCO.getCarrierReferenceNumber()));
        }
        if (criteriaCO.getLoadId() != null) {
            criteria.add(Restrictions.eq("loadId", criteriaCO.getLoadId()));
        }
        return (List<ApiExceptionEntity>) criteria.list();
    }
}
