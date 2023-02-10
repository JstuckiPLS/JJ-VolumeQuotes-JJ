package com.pls.user.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.user.dao.CapabilityDao;
import com.pls.user.domain.CapabilityEntity;
import com.pls.user.domain.UserCapabilityEntity;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * Implementation of {@link CapabilityDao}.
 * 
 * @author Pavani Challa
 */
@Transactional
@Repository
public class CapabilityDaoImpl extends AbstractDaoImpl<CapabilityEntity, Long> implements CapabilityDao {

    private static final String ASSIGNED_TO_CURRENT_USER = "capability_id in (select ucx.capability_id from "
            + "user_capabilities_xref ucx where ucx.status = 'A' and ucx.person_id = ? union all select "
            + "gc.capability_id from group_capabilities gc join groups on gc.group_id = groups.group_id and "
            + "groups.status = 'A' join user_groups ug on ug.group_id = groups.group_id and ug.status = 'A'"
            + "where ug.person_id = ? and gc.status='A')";

    private static final String EXCLUDE_GROUP_RESTRICTION = "capability_id not in (select gc.capability_id  "
            + "from  group_capabilities gc JOIN groups ge on gc.group_id = ge.group_id and gc.status = 'A'  "
            + "and ge.status = 'A' and ge.group_id = ?)";

    private static final String IN_GROUP_RESTRICTION = "capability_id in (select gc.capability_id  from  "
            + "group_capabilities gc JOIN groups ge on gc.group_id = ge.group_id and gc.status = 'A'  "
            + "and ge.status = 'A' and ge.group_id = ?)";

    @Override
    @SuppressWarnings("unchecked")
    public List<CapabilityEntity> findAllCapabilities(Long excludeGroup) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("sys20", "Y"));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        Long personId = SecurityUtils.getCurrentPersonId();
        criteria.add(Restrictions.sqlRestriction(ASSIGNED_TO_CURRENT_USER,
                new Long[] {personId, personId},
                new LongType[] { StandardBasicTypes.LONG, StandardBasicTypes.LONG }));

        if (excludeGroup != null) {
            criteria.add(Restrictions.sqlRestriction(EXCLUDE_GROUP_RESTRICTION, excludeGroup,
                        StandardBasicTypes.LONG));
        }

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CapabilityEntity> findCapabilitiesForGrp(Long groupId) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("sys20", "Y"));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.add(Restrictions.sqlRestriction(IN_GROUP_RESTRICTION, groupId, StandardBasicTypes.LONG));

        return criteria.list();
    }

    @Override
    public void unassignCapability(Long capabilityId, List<Long> users) {
        Query query = getCurrentSession().getNamedQuery(UserCapabilityEntity.Q_UPDATE_STATUS_STATEMENT);
        query.setParameter("status", Status.INACTIVE);
        query.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId());
        query.setParameter("capabilityId", capabilityId);

        for (Long personId : users) {
            query.setParameter("personId", personId);
            query.executeUpdate();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserCapabilitiesResultVO> findUsersWithCapability(Long capabilityId, Long organizationId) {
        return getCurrentSession().getNamedQuery(CapabilityEntity.Q_GET_USERS_WITH_CAPABILITY)
                .setParameter("capabilityId", capabilityId)
                .setParameter("orgId", organizationId, LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(UserCapabilitiesResultVO.class)).list();
    }
}
