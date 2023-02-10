package com.pls.ltlrating.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.ltlrating.dao.LtlCustHidePricDetailsDao;
import com.pls.ltlrating.domain.LtlCustHidePricDetailsEntity;

/**
 * When a carrier profile rate breakdown information and also the rate information need to be hidden from Quoting
 * for specific customer, the information is saved in LtlCustHidePricDetailsEntity through this Dao class.
 *
 * @author Hima Bindu Challa
 */
@Transactional
@Repository
public class LtlCustHidePricDetailsDaoImpl extends AbstractDaoImpl<LtlCustHidePricDetailsEntity, Long> implements LtlCustHidePricDetailsDao {

}
