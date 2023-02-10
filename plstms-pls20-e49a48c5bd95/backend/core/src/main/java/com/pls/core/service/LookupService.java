package com.pls.core.service;

import java.util.List;

import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.LtlLookupValueEntity;

/**
 * Class to get all Lookup values - either LTL Lookup values or flatbed Lookup values.
 * 
 * @author Hima Bindu Challa
 */
public interface LookupService {

    /**
     * Get LTL lookup values by group.
     * 
     * @param lookupGroup
     *            Look up Group
     * 
     * @return list of LtlLookupValueEntities.
     */
    List<LtlLookupValueEntity> getLtlLookupValuesByGroup(String lookupGroup);

    /**
     * Find lookup values for GL number formation.
     * 
     * @return list of {@link LookupValueEntity}.
     */
    List<LookupValueEntity> getGLNumberComponents();

     /**
      * Find lookup values for payment methods.
      * 
      * @return list of {@link LookupValueEntity}.
      */
      List<LookupValueEntity> getLookupValuesForPayMethod();

    /**
     * Get GL values for freight charge.
     * 
     * @return list of {@link List<LookupValueEntity>}.
     */
    List<LookupValueEntity> getGLValuesForFreightCharge();

    /**
     * Find lookup values for Brand Industrial Services customer.
     * 
     * @return list of {@link LookupValueEntity}.
     */
    List<LookupValueEntity> getBrandNumberComponents();

    /**
     * Find lookup values for Aluma Systems customer.
     * 
     * @return list of {@link LookupValueEntity}.
     */
    List<LookupValueEntity> getAlumaNumberComponents();
}