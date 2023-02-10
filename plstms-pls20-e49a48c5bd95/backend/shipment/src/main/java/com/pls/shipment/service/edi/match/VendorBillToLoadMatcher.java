package com.pls.shipment.service.edi.match;

import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

/**
 * Functionality to find single load that Vendor Bill can be matched to.
 * 
 * @author Aleksandr Leshchenko
 */
public interface VendorBillToLoadMatcher {

    /**
     * Find a load that matches specified vendor bill.
     * 
     * @param vendorBill
     *            vendor bill.
     * @return {@link LoadEntity} or <code>null</code>.
     */
    LoadEntity findMatchingLoad(CarrierInvoiceDetailsEntity vendorBill);

}
