package com.pls.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OrganizationNotificationsDao;
import com.pls.core.domain.organization.OrganizationNotificationsEntity;

@Repository
@Transactional
public class OrganizationNotificationsDaoImpl extends AbstractDaoImpl<OrganizationNotificationsEntity, Long>
        implements OrganizationNotificationsDao {

    @Override
    public List<OrganizationNotificationsEntity> findByOrgnId(Long orgId) {
        return (List<OrganizationNotificationsEntity>) getCurrentSession()
                .getNamedQuery("com.pls.core.domain.organization.OrganizationNotificationsEntity.Q_GET_BY_ORG_ID")
                .setLong("orgId", orgId).list();
    }
}
