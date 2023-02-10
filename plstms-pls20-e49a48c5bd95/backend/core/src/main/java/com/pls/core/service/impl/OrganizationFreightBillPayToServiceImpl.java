package com.pls.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.dao.OrganizationFreightBillPayToDao;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.domain.OrganizationFreightBillPayToEntity;
import com.pls.core.service.OrganizationFreightBillPayToService;

/**
 * Implementation of {@link OrganizationFreightBillPayToService}.
 * 
 * @author Artem Arapov
 *
 */

@Service
public class OrganizationFreightBillPayToServiceImpl implements OrganizationFreightBillPayToService {

    @Autowired
    private OrganizationFreightBillPayToDao dao;

    @Override
    public FreightBillPayToEntity getActiveFreightBillPayToByOrgId(Long orgId) {
        return dao.getActiveFreightBillPayToByOrgId(orgId);
    }

    @Override
    public void save(OrganizationFreightBillPayToEntity entity) {
        dao.inactivateExistingByOrgId(entity.getOrgId());
        dao.saveOrUpdate(entity);
    }

    @Override
    public OrganizationFreightBillPayToEntity getById(Long id) {
        return dao.find(id);
    }

}
