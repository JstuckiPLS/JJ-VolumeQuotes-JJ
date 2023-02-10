package com.pls.core.service;

import java.util.List;

import com.pls.core.domain.bo.CarrierInfoBO;
import com.pls.core.domain.bo.SimpleCarrierInfoBO;
import com.pls.core.domain.organization.CarrierEntity;

/**
 * Business service that handle business logic for carrier.
 * 
 * @author Alexander Nalapko
 */
public interface CarrierService {

    /**
     * Find carrier information by name.
     * 
     * @param name Name pattern.
     * @param limit Max number of result entities.
     * @param offset Number of the first entity.
     * @return Not <code>null</code> {@link List}.
     */
    List<CarrierInfoBO> findCarrierByName(String name, int limit, int offset);

    /**
     * Find carrier by SCAC.
     * 
     * @param scac
     *            scac.
     * @return carrier.
     */
    CarrierEntity findByScac(String scac);

    /**
     * Find carrier by mc number.
     * 
     * @param mcNumber
     *            MC number.
     * @return carrier.
     */
    CarrierEntity findByMcNumber(String mcNumber);

    /**
     * Find carrier by SCAC and MC, if present. MC is optional, it is not considered when empty.
     * 
     * @param scac
     *            scac.
     * @param mcNumber
     *            MC number, optional.
     * @return carrier.
     * 
     */
    CarrierEntity findByScacAndMC(String scac, String mcNumber);

    /**
     * Fetches list of {@link SimpleCarrierInfoBO} for specified search criteria.
     * 
     * @param carrier
     *              carrier search criteria
     * @param scacCode
     *              scac search criteria
     * @param status
     *              status search criteria
     * 
     * @return list of carrier info matching search criteria.
     */
    List<SimpleCarrierInfoBO> getCarrierInfos(String carrier, String scacCode, String status);

    /**
     * Find Carrier by id.
     *
     * @param id the entity id to search.
     * @return carrier.
     */
    CarrierEntity findCarrierById(Long id);

    /**
     *  Retrieves default non-registered carrier.
     * 
     * @return - carrier
     */
    CarrierInfoBO getDefaultCarrier();

    /**
     * Save corresponded entity.
     * 
     * @param entity - Not <code>null</code> instance of {@link CarrierEntity}.
     */
    void save(CarrierEntity entity);
}
