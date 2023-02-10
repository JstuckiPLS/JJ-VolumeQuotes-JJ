package com.pls.shipment.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.CarrierInvoiceLineItemDao;
import com.pls.shipment.domain.CarrierInvoiceLineItemEntity;

/**
 * Implementation of {@link CarrierInvoiceLineItemDao}.
 *
 * @author Mikhail Boldinov, 07/11/13
 */
@Repository
@Transactional
public class CarrierInvoiceLineItemDaoImpl extends AbstractDaoImpl<CarrierInvoiceLineItemEntity, Long> implements CarrierInvoiceLineItemDao {
}
