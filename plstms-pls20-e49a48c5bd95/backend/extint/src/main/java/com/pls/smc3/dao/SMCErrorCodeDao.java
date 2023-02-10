package com.pls.smc3.dao;

import com.pls.core.dao.AbstractDao;
import com.pls.smc3.domain.SMCErrorCodeEntity;

/**
 * SMC Error Code DAO.
 * 
 * @author PAVANI CHALLA
 * 
 */
public interface SMCErrorCodeDao extends AbstractDao<SMCErrorCodeEntity, Long> {

    /**
     * Method to fetch the Error Code object based on error code.
     * 
     * @param errorCode
     *            {@link String}.
     * @return {@link SMCErrorCodeEntity}
     */
    SMCErrorCodeEntity getErrorCode(String errorCode);

}
