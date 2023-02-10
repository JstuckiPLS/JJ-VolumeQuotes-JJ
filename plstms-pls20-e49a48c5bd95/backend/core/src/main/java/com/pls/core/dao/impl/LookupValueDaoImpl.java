package com.pls.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.LookupValueDao;
import com.pls.core.domain.LookupValueEntity;
import com.pls.core.domain.enums.LookupGroup;

/**
 * {@link com.pls.core.dao.LookupValueDao} implementation.
 * 
 * @author Sergey Vovchuk
 */
@Transactional
@Repository
public class LookupValueDaoImpl extends AbstractDaoImpl<LookupValueEntity, Long> implements LookupValueDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<LookupValueEntity> findLookupValuesByGroup(List<LookupGroup> lookupGroups) {
        return getCurrentSession().getNamedQuery(LookupValueEntity.Q_FIND_FOR_LOOKUP_GROUP)
                .setParameterList("lookupGroups", lookupGroups).list();
    }

    @Override
    public List<LookupValueEntity> findLookupValuesForPayMethod() {
        return findLookupValuesByGroup(Arrays.asList(LookupGroup.BT_PAYMENT_METHOD));
    }
}