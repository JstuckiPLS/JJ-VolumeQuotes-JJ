package com.pls.invoice.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.invoice.dao.BillingStatusReasonDao;
import com.pls.invoice.domain.BillingStatusReasonEntity;

/**
 * Implements {@link BillingStatusReasonDao}.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class BillingStatusReasonDaoImpl extends AbstractDaoImpl<BillingStatusReasonEntity, String> implements BillingStatusReasonDao {

    @Override
    public String getDescriptionByCode(String reasonCode) {
        return (String) getCurrentSession().getNamedQuery(BillingStatusReasonEntity.Q_BY_CODE)
                .setString("code", reasonCode)
                .setCacheable(true)
                .uniqueResult();
    }

}
