package com.pls.core.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.SimpleOrganizationDao;
import com.pls.core.domain.organization.SimpleOrganizationEntity;

/**
 * Class to get minimal Organization table information.
 * 
 * @author Hima Bindu Challa
 *
 */
@Transactional
@Repository
public class SimpleOrganizationDaoImpl extends AbstractDaoImpl<SimpleOrganizationEntity, Long> implements SimpleOrganizationDao {
    @SuppressWarnings("unchecked")
    @Override
    public List<SimpleOrganizationEntity> getOrganizationsByNameAndType(String orgType,
            String name, Integer limit, Integer offset) {
        return getCurrentSession().createCriteria(SimpleOrganizationEntity.class)
                .add(Restrictions.eq("orgType", orgType))
                .add(Restrictions.ilike("name", name.replaceAll("\\*", "%") + "%"))
                .setFirstResult(offset).setMaxResults(limit).list();
    }
}
