package com.pls.shipment.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.rating.AccessorialTypeDao;
import com.pls.core.domain.AccessorialTypeEntity;
import com.pls.shipment.service.AccessorialTypeService;

/**
 * {@link com.pls.shipment.service.AccessorialTypeService} implementation.
 *
 * @author Mikhail Boldinov, 16/05/13
 */
@Service
@Transactional
public class AccessorialTypeServiceImpl implements AccessorialTypeService {

    @Autowired
    private AccessorialTypeDao accessorialTypeDao;

    @Override
    public List<AccessorialTypeEntity> getAvailableAccessorialTypes() {
        return accessorialTypeDao.getAllApplicableAccessorialTypes();
    }

    @Override
    public void refreshAccessorials(Collection<AccessorialTypeEntity> accessorials) {
        accessorialTypeDao.refreshAccessorials(accessorials);
    }
}
