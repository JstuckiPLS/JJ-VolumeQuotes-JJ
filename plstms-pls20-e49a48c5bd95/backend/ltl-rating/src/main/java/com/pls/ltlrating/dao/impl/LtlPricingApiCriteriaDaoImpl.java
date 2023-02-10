package com.pls.ltlrating.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.LtlPricingApiCriteriaDao;
import com.pls.ltlrating.domain.LtlPricingApiCriteriaEntity;

/**
 * Implementation of {@link LtlPricingApiCriteriaDao} for persisting Ltl pricing api criteria.
 *
 * @author Pavani Challa
 *
 */
@Transactional
@Repository
public class LtlPricingApiCriteriaDaoImpl extends AbstractDaoImpl<LtlPricingApiCriteriaEntity, Long> implements LtlPricingApiCriteriaDao {

}
