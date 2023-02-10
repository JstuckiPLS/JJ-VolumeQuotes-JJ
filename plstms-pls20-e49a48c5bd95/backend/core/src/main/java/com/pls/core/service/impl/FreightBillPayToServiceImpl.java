package com.pls.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pls.core.dao.FreightBillPayToDao;
import com.pls.core.domain.FreightBillPayToEntity;
import com.pls.core.service.FreightBillPayToService;

/**
 * Implementation of {@link FreightBillPayToService}.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
public class FreightBillPayToServiceImpl implements FreightBillPayToService {
    @Autowired
    private FreightBillPayToDao dao;

    @Override
    public FreightBillPayToEntity getDefaultFreightBillPayTo() {
        return dao.getDefaultFreightBillPayTo();
    }

    @Override
    public FreightBillPayToEntity getFreightBillPayTo(Long id) {
        return dao.find(id);
    }
}
