package com.pls.ltlrating.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.ltlrating.domain.LtlCustHidePricDetailsEntity;

/**
 * When a carrier profile rate breakdown information and also the rate information need to be hidden from Quoting
 * for specific customer, the information is saved in LtlCustHidePricDetailsEntity through this Dao class.
 *
 * @author Hima Bindu Challa
 */
public interface LtlCustHidePricDetailsDao extends AbstractDao<LtlCustHidePricDetailsEntity, Long> {

}
