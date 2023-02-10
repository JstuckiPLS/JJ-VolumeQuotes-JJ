package com.pls.shipment.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

/**
 * {@link ResultTransformer} implementation to convert {@link BigDecimal} to {@link Long}.
 * 
 * @author Aleksandr Leshchenko
 */
@SuppressWarnings("serial")
class BigDecimalToLongResultTransformer implements ResultTransformer {
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (tuple != null && tuple.length == 1 && tuple[0] instanceof BigDecimal) {
            return ((BigDecimal) tuple[0]).longValue();
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List transformList(List collection) {
        return collection;
    }
}