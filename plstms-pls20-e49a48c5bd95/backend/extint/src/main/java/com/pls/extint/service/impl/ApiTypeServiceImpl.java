package com.pls.extint.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.extint.dao.ApiTypeDao;
import com.pls.extint.domain.ApiTypeEntity;
import com.pls.extint.service.ApiTypeService;

/**
 * Implementation class of {@link ApiTypeService}.
 * 
 * @author Pavani Challa
 * 
 */
@Service
@Transactional
public class ApiTypeServiceImpl implements ApiTypeService {

    @Autowired
    private ApiTypeDao dao;

    @Override
    public List<ApiTypeEntity> getByCategory(Long carrierOrgId, Long shipperOrgId, String category, String apiOrgType) {
        return dao.findByCategory(carrierOrgId, shipperOrgId, category, apiOrgType);
    }

    @Override
    public ApiTypeEntity save(ApiTypeEntity entityDetail) {
        return dao.saveOrUpdate(entityDetail);
    }

}
