package com.pls.core.service.impl.rating;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.rating.AccessorialTypeDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.core.service.rating.RatingAccessorialTypeService;
import com.pls.core.shared.Status;

/**
 * Implementation of {@link AccessorialTypeService}.
 * 
 * @author Artem Arapov
 * 
 */
@Service
@Transactional
public class RatingAccessorialTypeServiceImpl implements RatingAccessorialTypeService {

    @Autowired
    private AccessorialTypeDao dao;

    @Override
    public List<AccessorialTypeEntity> getAllAccessorialType() {
        return dao.getAll();
    }

    @Override
    public List<AccessorialTypeEntity> getAllAccessorialTypeByStatus(Status status) {
        return dao.findAccessorialTypesByStatus(status);
    }

    @Override
    public AccessorialTypeEntity getByCode(String accessorialCode) {
        return dao.find(accessorialCode);
    }

    @Override
    public void saveAccessorialType(AccessorialTypeEntity entity) {
        dao.saveOrUpdate(entity);
    }

    @Override
    public void updateStatus(List<String> accessorialCodes, Status status) {
        dao.updateStatus(accessorialCodes, status, SecurityUtils.getCurrentPersonId());
    }

    @Override
    public boolean checkAccessorialCodeExists(String code) {
        return dao.checkAccessorialCodeExists(code);
    }

    @Override
    public List<AccessorialTypeEntity> listAccessorialTypesByGroup(String group) {
        return dao.listPickupAccessorialTypes(group);
    }

    @Override
    public boolean isAccessorialTypeUnique(String code) {
        return dao.isAccessorialTypeUnique(code);
    }
}
