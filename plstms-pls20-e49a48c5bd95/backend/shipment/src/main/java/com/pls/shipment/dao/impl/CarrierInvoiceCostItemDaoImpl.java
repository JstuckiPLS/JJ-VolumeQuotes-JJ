package com.pls.shipment.dao.impl;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.CarrierInvoiceCostItemDao;
import com.pls.shipment.domain.CarrierInvoiceCostItemEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link CarrierInvoiceCostItemDao}.
 *
 * @author Sergey Kirichenko
 */
@Repository
@Transactional
public class CarrierInvoiceCostItemDaoImpl extends AbstractDaoImpl<CarrierInvoiceCostItemEntity, Long> implements CarrierInvoiceCostItemDao {
}
