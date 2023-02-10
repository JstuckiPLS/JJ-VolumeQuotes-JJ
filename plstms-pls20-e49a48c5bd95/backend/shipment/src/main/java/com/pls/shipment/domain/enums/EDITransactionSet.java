package com.pls.shipment.domain.enums;

import com.pls.core.dao.AbstractDao;
import com.pls.core.domain.Identifiable;
import com.pls.shipment.dao.CarrierInvoiceDetailsDao;
import com.pls.shipment.dao.LoadTrackingDao;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.service.edi.handler.EDI204ParseResultHandler;
import com.pls.shipment.service.edi.handler.EDI210ParseResultHandler;
import com.pls.shipment.service.edi.handler.EDI214ParseResultHandler;
import com.pls.shipment.service.edi.handler.EDI990ParseResultHandler;
import com.pls.shipment.service.edi.handler.EDI997ParseResultHandler;
import com.pls.shipment.service.edi.handler.EDIParseResultHandler;

/**
 * Transaction set ids enumeration.
 *
 * @author Mikhail Boldinov, 03/09/13
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public enum EDITransactionSet {
    _204("204", LtlShipmentDao.class, EDI204ParseResultHandler.class),
    _210("210", CarrierInvoiceDetailsDao.class, EDI210ParseResultHandler.class),
    _214("214", LoadTrackingDao.class, EDI214ParseResultHandler.class),
    _990("990", LoadTrackingDao.class, EDI990ParseResultHandler.class),
    _997("997", LoadTrackingDao.class, EDI997ParseResultHandler.class);

    private String id;

    private Class<AbstractDao<Identifiable<Long>, Long>> daoClass;

    private Class<EDIParseResultHandler<Identifiable<Long>>> handlerClass;

    /**
     * Constructor.
     *
     * @param id
     *            ID
     * @param daoClass
     *            DAO class
     * @param handlerClass
     *            handler class
     */
    EDITransactionSet(String id, Class daoClass, Class handlerClass) {
        this.id = id;
        this.daoClass = daoClass;
        this.handlerClass = handlerClass;
    }

    public String getId() {
        return id;
    }

    public Class<AbstractDao<Identifiable<Long>, Long>> getDaoClass() {
        return daoClass;
    }

    public Class<EDIParseResultHandler<Identifiable<Long>>> getHandlerClass() {
        return handlerClass;
    }

    /**
     * Get {@link EDITransactionSet} enum instance by id.
     *
     * @param id transaction set id
     * @return instance of this enum
     */
    public static EDITransactionSet getById(String id) {
        for (EDITransactionSet transactionSet : EDITransactionSet.values()) {
            if (transactionSet.id.equals(id)) {
                return transactionSet;
            }
        }
        throw new IllegalArgumentException("Can not get EDITransactionSet by id: " + id);
    }
}
