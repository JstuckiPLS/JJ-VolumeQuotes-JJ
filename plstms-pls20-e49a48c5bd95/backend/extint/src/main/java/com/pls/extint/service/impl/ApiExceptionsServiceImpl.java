package com.pls.extint.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.extint.dao.ApiExceptionsDao;
import com.pls.extint.domain.ApiExceptionEntity;
import com.pls.extint.service.ApiExceptionsService;
import com.pls.extint.shared.ApiCriteriaCO;

/**
 * Implementation of ApiExceptionsService interface.
 * 
 * @author Pavani Challa
 * 
 */
@Service
@Transactional(readOnly = true)
public class ApiExceptionsServiceImpl implements ApiExceptionsService {

    @Autowired
    private ApiExceptionsDao apiExceptionDao;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public ApiExceptionEntity save(ApiExceptionEntity entity) {
        return apiExceptionDao.saveOrUpdate(entity);
    }

    @Override
    public List<ApiExceptionEntity> getAll() {
        return apiExceptionDao.getAll();
    }

    @Override
    public List<ApiExceptionEntity> getByCriteria(ApiCriteriaCO criteria) {
        return apiExceptionDao.findByCriteria(criteria);
    }

    @Override
    public ApiExceptionEntity find(Long id) {
        return apiExceptionDao.find(id);
    }

}
