package com.pls.core.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.OutboundEdiQueueMapEntity;
import com.pls.core.service.integration.OutboundEdiQueueMappingService;

/**
 * This utility class is a singleton class used to get list of queue mappings for customers and carriers.
 * 
 * @author Yasaman Honarvar
 *
 */
@Component
@Scope("singleton")
@Transactional
public class OutboundEdiQueueMappingUtils {

    @Autowired
    private OutboundEdiQueueMappingService mappingService;

    /**
     * Returns the queue mapping entity while given the related organization id.
     * 
     * @param orgId
     *            the ID of the organization we are sending EDI to.
     * @return OutboundEdiQueueMappingEntity of the related organization.
     */
    public OutboundEdiQueueMapEntity getOutboundEdiQueueMapEntityById(
            Long orgId) {
        return mappingService.getQueueMappingsById(orgId);
    }

    /**
     * This function checks to see whether the mapping list contains an organization with given Scac or not.
     * 
     * @param scac
     *            of the carrier we are sending the EDI to.
     * @return boolean true/false
     */
    public boolean isQueueEnabled(String scac) {
        return mappingService.isQueueEnabled(scac);
    }

    /**
     * Returns the queue mapping entity for the related carrier when given the
     * carriers Scac.
     * 
     * @param scac
     *            of the carrier we are sending the EDI to.
     * @return OutboundEdiQueueMappingEntity of the related organization.
     */
    public OutboundEdiQueueMapEntity getOutboundEdiQueueMapEntityByScac(String scac) {
        return mappingService.getQueueMappingsByScac(scac);
    }
}
