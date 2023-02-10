package com.pls.ltlrating.dao;

import java.util.List;

import com.pls.core.dao.DictionaryDao;
import com.pls.ltlrating.domain.LtlPricingTypesEntity;

/**
 * DAO for {@link LtlPricingTypesEntity}.
 *
 * @author Hima Bindu Challa
 */
public interface LtlPricingTypesDao extends DictionaryDao<LtlPricingTypesEntity> {

    /**
     * To get All Ltl Pricing Types by group. The group can be "CARRIER" or "CUSTOMER".
     * "Benchmark" pricing type is specific to customer and it should not show for carrier rating profile.
     *
     * @param group
     *            - CARRIER or CUSTOMER
     * @return List<LtlPricingTypesEntity> - List of all LtlPricingTypesEntity for selected group
     */
    List<LtlPricingTypesEntity> findAllByGroup(String group);

    /**
     * Get Ltl Pricing Types by name.
     *
     * @param name
     *            Pricing Types name
     * @return <LtlPricingTypesEntity>
     */
    LtlPricingTypesEntity findByName(String name);
}
