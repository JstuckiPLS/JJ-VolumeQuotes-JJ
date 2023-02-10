package com.pls.core.dao.impl.rating;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.dao.rating.AccessorialTypeDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.domain.enums.ApplicableToUnit;
import com.pls.core.domain.enums.LtlAccessorialGroup;
import com.pls.core.shared.Status;

/**
 * {@link AccessorialTypeDao} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class AccessorialTypeDaoImpl extends AbstractDaoImpl<AccessorialTypeEntity, String> implements AccessorialTypeDao {
    @SuppressWarnings("unchecked")
    @Override
    public List<AccessorialTypeEntity> findAccessorialTypesByStatus(Status status) {
        Query query = getCurrentSession().getNamedQuery(AccessorialTypeEntity.Q_ALL_ACCESSORIALS_BY_STATUS);
        query.setParameter("status", status);
        return query.list();
    }

    @Override
    public void updateStatus(List<String> accessorialCodes, Status status, Long modifiedBy) {
        Query query = getCurrentSession().getNamedQuery(AccessorialTypeEntity.Q_UPDATE_ACCESSORIALS_STATUS);
        query.setParameterList("ids", accessorialCodes);
        query.setParameter("status", status);
        query.setParameter("modifiedBy", modifiedBy);
        query.executeUpdate();
    }

    @Override
    public boolean checkAccessorialCodeExists(String code) {
        Criteria criteria = getCurrentSession().createCriteria(AccessorialTypeEntity.class);
        criteria.add(Restrictions.eq("id", code).ignoreCase());
        criteria.setProjection(Projections.rowCount());
        Long count = (Long) criteria.uniqueResult();

        return count != 0L;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AccessorialTypeEntity> listPickupAccessorialTypes(String group) {
        return getCurrentSession().getNamedQuery(AccessorialTypeEntity.QUERY_ACCESSORIALS_BY_GROUP)
                .setParameter("group", LtlAccessorialGroup.valueOf(group))
                .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AccessorialTypeEntity> getAllApplicableAccessorialTypes() {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("status", Status.ACTIVE));
        criteria.add(Restrictions.or(Restrictions.eq("applicableTo", ApplicableToUnit.ALL), Restrictions.eq("applicableTo", ApplicableToUnit.LTL)));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AccessorialTypeEntity> getPickupAndDeliveryAccessorials(Set<String> accessorialTypes) {
        return getCurrentSession().getNamedQuery(AccessorialTypeEntity.Q_PICKUP_AND_DELIVERY_ACCESSORIALS)
                .setParameterList("accessorialTypes", accessorialTypes).list();
    }

    @Override
    public void refreshAccessorials(Collection<AccessorialTypeEntity> accessorials) {
        for (AccessorialTypeEntity accessorial : accessorials) {
            getCurrentSession().refresh(accessorial);
        }
    }

    @Override
    public boolean isAccessorialTypeUnique(String code) {
        int count = getCount(code, AccessorialTypeEntity.QUERY_SAVED_QUOTES_FOR_CODE_USAGE);

        if (count != 0) {
            return false;
        }

        count = getCount(code, AccessorialTypeEntity.QUERY_LOADS_FOR_CODE_USAGE);

        return count == 0 ? true : false;
    }

    private int getCount(String accessorialTypeCode, String countQuery) {
        return ((Number) getCurrentSession().getNamedQuery(countQuery)
                .setParameter("accessorialType", accessorialTypeCode)
                .uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AccessorialTypeEntity> getLtlAccessorialTypes() {
       Criteria criteria = getCriteria();
       criteria.add(Restrictions.eq("status", Status.ACTIVE));
       criteria.add(Restrictions.eq("applicableTo", ApplicableToUnit.LTL));
       criteria.add(Restrictions.isNotNull("accessorialGroup"));
       criteria.addOrder(Order.asc("id"));
       return criteria.list();
    }
}
