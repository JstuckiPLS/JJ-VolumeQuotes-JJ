package com.pls.shipment.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.dao.impl.AbstractDaoImpl;
import com.pls.shipment.dao.CarrierInvoiceDetailsReasonsDao;
import com.pls.shipment.domain.CarrierInvoiceDetailReasonsEntity;

/**
 * Implementation of {@link CarrierInvoiceDetailsReasonsDao}.
 *
 * @author Alexander Nalapko
 */
@Repository
@Transactional
public class CarrierInvoiceDetailsReasonsDaoImpl extends AbstractDaoImpl<CarrierInvoiceDetailReasonsEntity, Long>
        implements CarrierInvoiceDetailsReasonsDao {

}
