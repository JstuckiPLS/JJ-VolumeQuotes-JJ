package com.pls.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.user.dao.GroupDao;
import com.pls.user.domain.GroupEntity;
import com.pls.user.service.GroupService;
import com.pls.user.shared.UserCapabilitiesResultVO;

/**
 * Service that handle business logic and transactions for Groups.
 *
 * @author Pavani Challa
 */

@Service
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupDao dao;

    @Override
    public GroupEntity getGroupById(Long groupId) {
        return dao.findGroup(groupId);
    }

    @Override
    public List<GroupEntity> getAllGroups(Long personId) {
        return dao.findAllGroups(personId);
    }

    @Override
    public void deleteGroup(Long groupId) {
        dao.delete(groupId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void unassignGroup(Long groupId, List<Long> users) {
        dao.unassignGroup(groupId, users);
    }

    @Override
    public List<UserCapabilitiesResultVO> getUsersWithGroup(Long groupId, Long organizationId) {
        return dao.findUsersWithGroup(groupId, organizationId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveGroup(GroupEntity group) {
        dao.saveOrUpdate(group);
    }

    @Override
    public Boolean isNameUnique(String name, Long excludeGroup) {
        return dao.isNameUnique(name, excludeGroup);
    }
}
