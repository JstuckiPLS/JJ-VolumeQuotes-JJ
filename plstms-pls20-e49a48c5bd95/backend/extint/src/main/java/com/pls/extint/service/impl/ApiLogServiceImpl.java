package com.pls.extint.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pls.extint.dao.ApiLogDao;
import com.pls.extint.domain.ApiLogEntity;
import com.pls.extint.service.ApiLogService;
import com.pls.extint.shared.ApiCriteriaCO;
import com.pls.extint.shared.ApiLogVO;

/**
 * Implementation of ApiLogService interface.
 * 
 * @author Pavani Challa
 * 
 */
@Service
@Transactional(readOnly = true)
public class ApiLogServiceImpl implements ApiLogService {

    @Autowired
    private ApiLogDao apiLogDao;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ApiLogEntity save(ApiLogEntity entity) {
        return apiLogDao.saveOrUpdate(entity);
    }

    @Override
    public ApiLogEntity find(Long id) throws Exception {
        return apiLogDao.find(id);
    }

    @Override
    public List<ApiLogVO> getAll() {
        return apiLogDao.getAllLogs();
    }

    @Override
    public List<ApiLogVO> getByCriteria(ApiCriteriaCO criteria) {
        return apiLogDao.findByCriteria(criteria);
    }

    @Override
    public String getLatestTrackingNote(Long loadId, Long apiLogId) {
        return apiLogDao.getLatestTrackingNote(loadId, apiLogId);
    }
}
