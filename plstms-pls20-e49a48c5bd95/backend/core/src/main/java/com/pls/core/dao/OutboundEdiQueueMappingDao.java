package com.pls.core.dao;

import com.pls.core.domain.OutboundEdiQueueMapEntity;

/**
 * This Dao is created to retrieve data from outbound queue mapping database.
 * 
 * @author Yasaman Honarvar
 *
 */
public interface OutboundEdiQueueMappingDao extends AbstractDao<OutboundEdiQueueMapEntity, Long> {

    /**
     * Retrieves a queue mapping using orgId.
     * 
     * @param orgId
     *            used for finding the related mapping for the organization.
     * @return list of QueueCapablityEntity.
     */
    OutboundEdiQueueMapEntity getQueueMappingsById(Long orgId);

    /**
     * Retrieves a queue mapping using scac.
     * 
     * @param scac
     *            used for finding the related mapping for the carrier.
     * @return list of QueueCapablityEntity.
     */
    OutboundEdiQueueMapEntity getQueueMappingsByScac(String scac);
}
