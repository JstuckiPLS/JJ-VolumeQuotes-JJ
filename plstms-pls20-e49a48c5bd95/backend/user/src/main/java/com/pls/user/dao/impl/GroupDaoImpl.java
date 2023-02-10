package com.pls.user.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.shared.Status;
import com.pls.user.dao.CapabilityDao;
import com.pls.user.dao.GroupDao;
import com.pls.user.domain.GroupCapabilitiesEntity;
import com.pls.user.domain.GroupEntity;
import com.pls.user.domain.UserGroupEntity;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * Implementation of {@link GroupDao}.
 * 
 * @author Pavani Challa
 */
@Transactional
@Repository
public class GroupDaoImpl extends AbstractDaoImpl<GroupEntity, Long> implements GroupDao {

    @Autowired
    private CapabilityDao capabilityDao;

    private static final String PARAM_GROUP_ID = "groupId";

    @SuppressWarnings("unchecked")
    @Override
    public List<GroupEntity> findAllGroups(Long personId) {
        Query query = getCurrentSession().getNamedQuery(GroupEntity.Q_GET_ALL_USER_GROUP_ENTITIES);
        query.setParameter("personId", personId);
        return query.list();
    }

    @Override
    public GroupEntity findGroup(Long groupId) {
        GroupEntity group = find(groupId);
        if (group != null) {
            group.setGrpCapabilities(new ArrayList<GroupCapabilitiesEntity>());
            group.setCapabilities(capabilityDao.findCapabilitiesForGrp(groupId));
        }
        return group;
    }

    @Override
    public void delete(Long groupId) {
        Query query = getCurrentSession().getNamedQuery(UserGroupEntity.Q_INACTIVATE_ALL_USERS_STATEMENT);
        query.setParameter(PARAM_GROUP_ID, groupId);
        Long personId = SecurityUtils.getCurrentPersonId();
        query.setParameter("modifiedBy", personId);
        query.executeUpdate();

        query = getCurrentSession().getNamedQuery(GroupEntity.Q_UPDATE_STATUS_STATEMENT);
        query.setParameter("status", Status.INACTIVE.getCode());
        query.setParameter("modifiedBy", personId);
        query.setParameter(PARAM_GROUP_ID, groupId);
        query.executeUpdate();

        getCurrentSession().flush();
    }

    @Override
    public void unassignGroup(Long groupId, List<Long> users) {
        Query query = getCurrentSession().getNamedQuery(UserGroupEntity.Q_UPDATE_STATUS_STATEMENT);
        query.setParameter("status", Status.INACTIVE);
        query.setParameter("modifiedBy", SecurityUtils.getCurrentPersonId());
        query.setParameter(PARAM_GROUP_ID, groupId);

        for (Long personId : users) {
            query.setParameter("personId", personId);
            query.executeUpdate();
        }

        getCurrentSession().flush();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserCapabilitiesResultVO> findUsersWithGroup(Long groupId, Long organizationId) {
        return getCurrentSession().getNamedQuery(GroupEntity.Q_GET_USERS_WITH_GROUP)
                .setParameter(PARAM_GROUP_ID, groupId).setParameter("orgId", organizationId, LongType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(UserCapabilitiesResultVO.class)).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public GroupEntity saveOrUpdate(GroupEntity entity) {
        Boolean newGroup = (entity.getId() == null);

        // If this is an existing group, delete the capabilities first.
        if (!newGroup) {
            Query query = getCurrentSession().getNamedQuery(GroupCapabilitiesEntity.Q_GET_GROUP_CAPABILITIES);
            query.setParameter(PARAM_GROUP_ID, entity.getId());
            List<GroupCapabilitiesEntity> allCapabilities = query.list();
            Map<Long, GroupCapabilitiesEntity> existingMap = new HashMap<Long, GroupCapabilitiesEntity>();
            for (GroupCapabilitiesEntity existing : allCapabilities) {
                existing.setStatus(Status.INACTIVE);
                existingMap.put(existing.getCapabilityId(), existing);
            }
            for (GroupCapabilitiesEntity uiCapability : entity.getGrpCapabilities()) {
                GroupCapabilitiesEntity existing = existingMap.get(uiCapability.getCapabilityId());
                if (existing != null) {
                    existing.setStatus(Status.ACTIVE);
                } else {
                    allCapabilities.add(uiCapability);
                }
            }
            entity.setGrpCapabilities(allCapabilities);
        }

        GroupEntity group = super.saveOrUpdate(entity);

        if (newGroup) {
            UserGroupEntity userGroup = new UserGroupEntity();
            userGroup.setGroupId(group.getId());
            userGroup.setPersonId(SecurityUtils.getCurrentPersonId());
            getCurrentSession().saveOrUpdate(userGroup);
        }

        getCurrentSession().flush();
        return group;
    }

    @Override
    public Boolean isNameUnique(String name, Long excludeGroup) {
        StringBuilder sql = new StringBuilder(106);
        sql.append("SELECT groups.name from groups where lower(groups.name) like lower(:name)");
        if (excludeGroup != null && excludeGroup > 0) {
            sql.append(" and groups.group_id != :groupId");
        }

        Query query = getCurrentSession().createSQLQuery(sql.toString()).setParameter("name", name.trim().toLowerCase());
        if (excludeGroup != null && excludeGroup > 0) {
            query.setParameter("groupId", excludeGroup);
        }
        return query.list().size() == 0;
    }

}
