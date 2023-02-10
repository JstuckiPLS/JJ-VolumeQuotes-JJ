package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.bo.CarrierInfoBO;
import com.pls.core.domain.bo.SimpleCarrierInfoBO;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.organization.CarrierEntity;
import com.pls.core.domain.organization.CustomerEntity;

/**
 * DAO for carriers.
 *
 * @author Alexander Nalapko
 */
public interface CarrierDao  extends AbstractDao<CarrierEntity, Long> {
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
     * Find the first available carrier.
     * 
     * @return Normally not <code>null</code> {@link CarrierEntity}.
     */
    CarrierEntity find();

    /**
     * Find carrier by SCAC. Parameters are case insensitive.
     * 
     * @param scac
     *            scac, case insensitive.
     * @return carrier.
     */
    CarrierEntity findByScac(String scac);
    
    /**
     * Find carrier by SCAC. Parameters are case insensitive.
     * This method searches also in carriers actual scac codes (the one used for external communication)
     * 
     * @param scac
     *            scac, case insensitive.
     * @return carrier.
     */
    CarrierEntity findByScacIncludingActual(String scac);
    
    /**
     * Find carrier by SCAC and currency. Parameters are case insensitive.
     * This method searches for carrier with real or internal scac, matching the provided currency
     * 
     * @param scac
     *            scac, case insensitive.
     * @param currency
     *            currency to search for
     * @return carrier.
     */
    CarrierEntity findByScacAndCurrency(String scac, Currency currency);

    /**
     * Find carrier by mc number. Parameters are case insensitive.
     * 
     * @param mcNumber
     *            MC number, case insensitive.
     * @return carrier.
     */
    CarrierEntity findByMcNumber(String mcNumber);

    /**
     * Find carrier by SCAC and MC, if present. MC is optional, it is not considered when empty. Parameters
     * are case insensitive.
     * 
     * @param scac
     *            scac, case insensitive.
     * @param mcNumber
     *            MC number, optional, case insensitive.
     * @return carrier.
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
     * @return list of carrier info matching search criteria.
     */
    List<SimpleCarrierInfoBO> getCarrierInfos(String carrier, String scacCode, String status);
    
    /**
     *  Retrieves default non registered carrier.
     * 
     * @return actual carrier
     */
    CarrierInfoBO getDefaultCarrier();

    /**
     * Checks if Customer is in Carrier list of Customers for which EDI210 should be rejected.
     * 
     * @param scac - {@link CarrierEntity#getScac()}. Not <code>null</code>.
     * @param ediAccount - {@link CustomerEntity#getEdiAccount()}. Not <code>null</code>.
     * @return <code>True</code> in case when Customer is in list and EDI210 should be rejected, otherwise returns <code>False</code>.
     */
    Boolean rejectEdiForCustomer(String scac, String ediAccount);

	CarrierEntity findById(Long id);
}
