package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.organization.OrganizationNotificationsEntity;

public interface OrganizationNotificationsDao extends AbstractDao<OrganizationNotificationsEntity, Long> {

    List<OrganizationNotificationsEntity> findByOrgnId(Long id);
}
