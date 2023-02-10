package com.pls.shipment.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.CarrierInvoiceDetailsReasonLinksDao;
import com.pls.shipment.domain.CarrierInvoiceDetailReasonLinksEntity;

/**
 * Implementation of {@link CarrierInvoiceDetailsReasonLinksDao}.
 *
 * @author Alexander Nalapko
 */
@Repository
@Transactional
public class CarrierInvoiceDetailsReasonLinksDaoImpl extends
        AbstractDaoImpl<CarrierInvoiceDetailReasonLinksEntity, Long> implements CarrierInvoiceDetailsReasonLinksDao {

}
