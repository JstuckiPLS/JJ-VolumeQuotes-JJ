package com.pls.core.dao;

import java.util.List;

import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.enums.LookupGroup;

/**
 * Class to get all Lookup values.
 * 
 * @author Sergey Vovchuk
 */
public interface LookupValueDao extends AbstractDao<LookupValueEntity, Long> {

    /**
     * Find lookup values by lookup group.
     * 
     * @param lookupGroups
     *            {@link LookupValueEntity#getLookupGroup()}
     * 
     * @return list of {@link LookupValueEntity}.
     */
    List<LookupValueEntity> findLookupValuesByGroup(List<LookupGroup> lookupGroups);

    /**
     * Find lookup values for payment methods.
     * 
     * @return list of {@link LookupValueEntity}.
     */
    List<LookupValueEntity> findLookupValuesForPayMethod();

}