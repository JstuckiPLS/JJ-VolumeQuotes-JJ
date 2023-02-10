package com.pls.core.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.LtlLookupValueDao;
import com.pls.core.domain.LtlLookupValueEntity;

/**
 * Class to get all LTL Lookup values.
 * 
 * @author Hima Bindu Challa
 */
@Transactional
@Repository
public class LtlLookupValueDaoImpl extends AbstractDaoImpl<LtlLookupValueEntity, Long> implements LtlLookupValueDao {

    /**
     * Find lookup values by group.
     * 
     * @param lookupGroup
     *            Look up Group
     * 
     * @return list of LtlLookupValueEntities.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<LtlLookupValueEntity> findLookupValuesbyGroup(String lookupGroup) {

        return getCurrentSession().createCriteria(LtlLookupValueEntity.class)
                .add(Restrictions.eq("ltlLookupGroup", lookupGroup)).list();
    }
}
