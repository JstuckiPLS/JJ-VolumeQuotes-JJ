package com.pls.core.service.address.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.BillToDao;
import com.pls.core.domain.bo.KeyValueBO;
import com.pls.core.domain.organization.BillToDefaultValuesEntity;
import com.pls.core.domain.organization.BillToEntity;
import com.pls.core.service.address.BillToService;

/**
 * Implementation of {@link com.pls.core.service.address.BillToService} service.
 * 
 * @author Andrey Kachur
 */
@Transactional
@Service
public class BillToServiceImpl implements BillToService {

    @Autowired
    private BillToDao billToDao;

    @Override
    public BillToEntity getBillTo(Long id) {
        return billToDao.find(id);
    }

    @Override
    public boolean validateDuplicateName(String name, Long orgId) {
        return billToDao.validateDuplicateName(name, orgId);
    }

    @Override
    public List<KeyValueBO> getIdAndNameByOrgId(Long orgId) {
        return billToDao.getIdAndNameByOrgId(orgId);
    }

    @Override
    public List<KeyValueBO> getBillToEmails(List<Long> billToIds) {
        return billToDao.getBillToEmails(billToIds);
    }

    @Override
    public Collection<BillToEntity> getBillToAddresses(Long customerId) {
        return billToDao.getBillToAddresses(customerId);
    }

    @Override
    public BillToDefaultValuesEntity getDefaultEntityById(Long defaultEntityId) {
        return billToDao.getDefaultEntityById(defaultEntityId);
    }
}
