package com.pls.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.IntegrationAuditDao;
import com.pls.core.dao.OrganizationDao;
import com.pls.core.domain.AuditDetailEntity;
import com.pls.core.domain.AuditEntity;
import com.pls.core.domain.organization.OrganizationEntity;
import com.pls.core.domain.sterling.EDIMessageType;
import com.pls.core.integration.AuditBO;
import com.pls.core.service.IntegrationAuditService;

/**
 * Implementation of AuditService. Matches the resource request with the right DAO functionality.
 * 
 * @author Yasaman Honarvar
 *
 */
@Service
@Transactional(readOnly = true)
public class IntegrationAuditServiceImpl implements IntegrationAuditService {

    @Autowired
    private IntegrationAuditDao intDao;

    @Autowired
    private OrganizationDao orgDao;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public AuditEntity saveLog(AuditEntity audit) {
        return intDao.saveOrUpdate(audit);
    }

    @Override
    public AuditBO getLogById(Long id) {
        AuditBO bo = new AuditBO();
        AuditEntity entity = intDao.find(id);
        bo.setAuditEntity(entity);
        return bo;
    }

    @Override
    public List<AuditBO> getLogs(Date dateFrom, Date dateTo, Long customerId, Long carrierId, String bolNumber, String shipmentNumber, Long loadId,
            EDIMessageType messageType) {
        String scac = null;
        if (carrierId != null) {
            OrganizationEntity org = orgDao.find(carrierId);
            if (org != null) {
                scac = org.getScac();
            }
        }
        return intDao.getLogsByCriteria(dateFrom, dateTo, customerId, scac, bolNumber, shipmentNumber, loadId, messageType);
    }

    @Override
    public AuditDetailEntity getLogDetails(Long auditId) {
        return intDao.getLogDetailsByAuditId(auditId);

    }
}
