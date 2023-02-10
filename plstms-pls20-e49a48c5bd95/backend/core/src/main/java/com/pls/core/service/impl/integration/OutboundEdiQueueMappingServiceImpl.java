package com.pls.core.service.impl.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.OutboundEdiQueueMappingDao;
import com.pls.core.domain.OutboundEdiQueueMapEntity;
import com.pls.core.service.integration.OutboundEdiQueueMappingService;

/**
 * This service calls the DAO to receive outbound queue mappings.
 * 
 * @author Yasaman Honarvar
 */
@Service
@Transactional
public class OutboundEdiQueueMappingServiceImpl implements OutboundEdiQueueMappingService {

    @Autowired
    private OutboundEdiQueueMappingDao mappingDao;

    @Override
    public OutboundEdiQueueMapEntity getQueueMappingsById(Long orgId) {
        return mappingDao.getQueueMappingsById(orgId);
    }

    @Override
    public OutboundEdiQueueMapEntity getQueueMappingsByScac(String scac) {
        return mappingDao.getQueueMappingsByScac(scac);
    }

    @Override
    public boolean isQueueEnabled(String scac) {
        return mappingDao.getQueueMappingsByScac(scac) != null;
    }
}
