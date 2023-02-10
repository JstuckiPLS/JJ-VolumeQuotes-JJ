package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity;

/**
 * DAO for {@link LtlPricingApplicableCustomersEntity}.
 *
 * @author Hima Bindu Challa
 */
public interface LtlPricingApplicableCustomersDao extends AbstractDao<LtlPricingApplicableCustomersEntity, Long> {

    /**
     * Gets list of {@link LtlPricingApplicableCustomersEntity} for specified profile id.
     *
     * @param profileId Not <code>null</code> ID.
     * @return list of {@link LtlPricingApplicableCustomersEntity}
     */
    List<LtlPricingApplicableCustomersEntity> getApplicableCustomersByProfileId(Long profileId);

    /**
     * Gets list of Customer Names for specified smc3 Tariff Name.
     *
     * @param tariffName
     *            smc3 tariff Name
     * @return list of Customer Names
     */
    List<String> findApplicableCustomersBySMC3TariffName(String tariffName);
}
