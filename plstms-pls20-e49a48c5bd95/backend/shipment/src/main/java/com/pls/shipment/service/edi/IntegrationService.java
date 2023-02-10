package com.pls.shipment.service.edi;

import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.exception.ApplicationException;

/**
 * This interface is created to provide for the service needed for integration process.
 * @author Yasaman Honarvar
 *
 * @param <T>
 *      instance holding the information for integration with PLSPRO.
 */
public interface IntegrationService<T extends IntegrationMessageBO> {

    String TIME_ZONE = "ET";

    Long LTL_NETWORK = 7L;
    
    Long LTL_LIFECYCLE = 44L;

    Long EDI_214 = 214L;

    Long EDI_990 = 990L;

    Long EDI_997 = 997L;

    Long EDI_204 = 204L;

    /**
     * Processes the received EDI files.
     * @param obj is the EDI message received by our system and needs to be handled.
     * @throws ApplicationException might be thrown.
     */
    void processMessage(T obj) throws ApplicationException;
}
