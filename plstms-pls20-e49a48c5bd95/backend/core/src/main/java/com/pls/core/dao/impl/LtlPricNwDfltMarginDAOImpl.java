package com.pls.core.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.LtlPricNwDfltMarginDAO;
import com.pls.core.domain.organization.LtlPricNwDfltMarginEntity;

/**
 * DAO Implementation class for {@link LtlPricNwDfltMarginEntity}.
 * 
 * @author Ashwini Neelgund
 */
@Repository
@Transactional
public class LtlPricNwDfltMarginDAOImpl extends AbstractDaoImpl<LtlPricNwDfltMarginEntity, Long> implements LtlPricNwDfltMarginDAO {
    private static final long LTL_NETWORK_ID = 7L;

    @Override
    public LtlPricNwDfltMarginEntity getDefaultLTLMargin() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("networkId", LTL_NETWORK_ID);
        return findUniqueObjectByNamedQuery(LtlPricNwDfltMarginEntity.Q_BY_NETWORK_ID, paramMap);
    }
}
