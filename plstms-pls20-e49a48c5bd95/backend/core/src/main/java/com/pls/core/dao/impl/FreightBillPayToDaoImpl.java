package com.pls.core.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.FreightBillPayToDao;
import com.pls.core.domain.FreightBillPayToEntity;

/**
 * {@link FreightBillPayToDao} implementation.
 * 
 * @author Aleksandr Leshchenko
 */
@Repository
@Transactional
public class FreightBillPayToDaoImpl extends AbstractDaoImpl<FreightBillPayToEntity, Long> implements FreightBillPayToDao {

    public static final Long DEFAULT_FREIGHT_BILL_PAY_TO_ID = -1L;

    @Override
    public FreightBillPayToEntity getDefaultFreightBillPayTo() {
        return find(DEFAULT_FREIGHT_BILL_PAY_TO_ID);
    }
}
