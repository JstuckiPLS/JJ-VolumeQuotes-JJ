package com.pls.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.user.dao.CapabilityDao;
import com.pls.user.domain.CapabilityEntity;
import com.pls.user.service.CapabilityService;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * Service that handle business logic and transactions for Capabilities.
 *
 * @author Pavani Challa
 */

@Service
@Transactional(readOnly = true)
public class CapabilityServiceImpl implements CapabilityService {

    @Autowired
    private CapabilityDao dao;

    @Override
    public CapabilityEntity getCapabilityById(Long capabilityId) {
        return dao.find(capabilityId);
    }

    @Override
    public List<CapabilityEntity> getAllCapabilities(Long excludeGroup) {
        return dao.findAllCapabilities(excludeGroup);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unassignCapability(Long capabilityId, List<Long> users) {
        dao.unassignCapability(capabilityId, users);

    }

    @Override
    public List<UserCapabilitiesResultVO> getUsersWithCapability(Long capabilityId, Long organizationId) {
        return dao.findUsersWithCapability(capabilityId, organizationId);
    }

}
