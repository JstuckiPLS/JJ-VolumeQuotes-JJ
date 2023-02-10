package com.pls.shipment.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.CarrierInvoiceAddressDetailsDao;
import com.pls.shipment.domain.CarrierInvoiceAddressDetailsEntity;

/**
 * Implementation for {@link CarrierInvoiceAddressDetailsDao}.
 *
 * @author Alexander Kirichenko
 */
@Repository
@Transactional
public class CarrierInvoiceAddressDetailsDaoImpl extends AbstractDaoImpl<CarrierInvoiceAddressDetailsEntity, Long>
        implements CarrierInvoiceAddressDetailsDao {
}
