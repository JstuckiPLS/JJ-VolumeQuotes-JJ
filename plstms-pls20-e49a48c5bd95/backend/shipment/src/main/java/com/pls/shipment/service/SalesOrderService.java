package com.pls.shipment.service;

import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.exception.ApplicationException;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Service to build {@link LoadEntity} from vendorBill Id.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
public interface SalesOrderService {

    /**
     * Create {@link LoadEntity} from Vendor Bill id and Customer id.
     * 
     * @param vendorBillId - id of {@link CarrierInvoiceDetailsEntity}
     * @param customerId - id of {@link CustomerEntity}
     * @return matching {@link LoadEntity}
     * @throws ApplicationException if creating order fails
     */
    LoadEntity createNewOrder(Long vendorBillId, Long customerId) throws ApplicationException;

    /**
     * Create {@link LoadEntity} from {@link@CarrierInvoiceDetailsEntity}.
     * 
     * @param carrierInvoiceDetails {@link@CarrierInvoiceDetailsEntity} vendor bill
     * @param customer {@link CustomerEntity}
     * @return matching {@link LoadEntity}
     * @throws ApplicationException if creating order fails
     */
    LoadEntity createNewOrder(CarrierInvoiceDetailsEntity carrierInvoiceDetails, CustomerEntity customer) throws ApplicationException;
}
