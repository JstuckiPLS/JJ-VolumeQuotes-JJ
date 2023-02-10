package com.pls.smc3.dao.impl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.smc3.dao.SMCErrorCodeDao;
import com.pls.smc3.domain.SMCErrorCodeEntity;

/**
 * Implementation class of {@link SMCErrorCodeDao}.
 * 
 * @author PAVANI CHALLA
 * 
 */
@Transactional
@Repository
public class SMCErrorCodeDaoImpl extends AbstractDaoImpl<SMCErrorCodeEntity, Long> implements SMCErrorCodeDao {

    @Override
    public SMCErrorCodeEntity getErrorCode(String errorCode) {

        String hqlQuery = "SELECT errorCodeEntity FROM SMCErrorCodeEntity errorCodeEntity WHERE errorCodeEntity.code = :errorCode";

        Query query = getCurrentSession().createQuery(hqlQuery);
        query.setParameter("errorCode", errorCode);

        return (SMCErrorCodeEntity) query.uniqueResult();
    }
}
