package com.pls.core.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OrgServiceDao;
import com.pls.core.domain.organization.OrgServiceEntity;

/**
 * DAO implementation class of {@link OrgServiceEntity}.
 * 
 * @author Pavani Challa
 * 
 */
@Transactional
@Repository
public class OrgServiceDaoImpl extends AbstractDaoImpl<OrgServiceEntity, Long> implements OrgServiceDao {

    @Override
    public OrgServiceEntity getServicesByOrgId(Long orgId) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("orgId", orgId));

        return (OrgServiceEntity) criteria.uniqueResult();
    }


    @Override
    public Boolean isApiAvailable(Long orgId, String category) {
        StringBuilder sql = new StringBuilder("select distinct org_id from org_services where org_id = :orgId and ");
        sql.append(category).append(" = 'API'");

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        query.setParameter("orgId", orgId);

        return query.list().size() != 0;
    }

    @Override
    public OrgServiceEntity saveOrUpdate(OrgServiceEntity entity) {
        OrgServiceEntity updatedEntity = super.saveOrUpdate(entity);
        getCurrentSession().flush();
        getCurrentSession().refresh(updatedEntity);
        return updatedEntity;
    }
}
