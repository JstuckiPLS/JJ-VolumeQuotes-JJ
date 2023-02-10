package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.LtlLookupValueEntity;

/**
 * Class to get all LTL Lookup values.
 * 
 * @author Hima Bindu Challa
 */
public interface LtlLookupValueDao extends AbstractDao<LtlLookupValueEntity, Long> {

    /**
     * Find lookup values by group.
     * 
     * @param lookupGroup
     *            Look up Group
     * 
     * @return list of LtlLookupValueEntities.
     */
    List<LtlLookupValueEntity> findLookupValuesbyGroup(String lookupGroup);
}