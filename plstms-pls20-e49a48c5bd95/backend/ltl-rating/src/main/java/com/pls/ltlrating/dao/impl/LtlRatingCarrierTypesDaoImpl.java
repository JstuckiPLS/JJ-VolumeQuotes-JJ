package com.pls.ltlrating.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.DictionaryDaoImpl;
import com.pls.ltlrating.dao.LtlPricingCarrierTypesDao;
import com.pls.ltlrating.domain.LtlPricingCarrierTypesEntity;

/**
 * {@link LtlPricingCarrierTypesDao} implementation.
 *
 * @author Sergey Kirichenko
 */
@Transactional
@Repository
public class LtlRatingCarrierTypesDaoImpl extends DictionaryDaoImpl<LtlPricingCarrierTypesEntity> implements LtlPricingCarrierTypesDao {
}
