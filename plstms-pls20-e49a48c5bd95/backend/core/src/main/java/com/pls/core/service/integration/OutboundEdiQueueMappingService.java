package com.pls.core.service.integration;

import com.pls.core.domain.OutboundEdiQueueMapEntity;

/**
 * This class provides us the service to retrieve the list of outbound queue mappings for carrier and customer.
 * 
 * @author Yasaman Honarvar
 */
public interface OutboundEdiQueueMappingService {
    /**
     * This function calls the Dao to get the mapping with given orgId.
     * 
     * @param orgId
     *            used for searching the organization
     * 
     * @return Queue mapping.
     */
    OutboundEdiQueueMapEntity getQueueMappingsById(Long orgId);

    /**
     * This function calls the Dao to get the mapping with given scac.
     * 
     * @param scac
     *            used for searching the carrier
     * 
     * @return Queue mapping.
     */
    OutboundEdiQueueMapEntity getQueueMappingsByScac(String scac);

    /**
     * This function calls the Dao to see if a carrier is Queue enabled.
     * 
     * @param scac
     *            used for searching the carrier
     * 
     * @return Queue mapping.
     */
    boolean isQueueEnabled(String scac);
}
