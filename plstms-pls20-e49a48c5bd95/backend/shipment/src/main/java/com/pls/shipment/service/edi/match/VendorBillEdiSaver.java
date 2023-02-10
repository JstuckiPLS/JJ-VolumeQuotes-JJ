package com.pls.shipment.service.edi.match;

import com.pls.core.exception.ApplicationException;
import com.pls.shipment.domain.CarrierInvoiceDetailsEntity;
import com.pls.shipment.domain.LoadEntity;

import java.util.Map;

/**
 * Functionality to match load with vendor bill that came from EDI and save it.
 * 
 * @author Aleksandr Leshchenko
 */
public interface VendorBillEdiSaver {

    /**
     * Find Load that EDI Vendor Bill can be matched to. Prepare Vendor Bill for saving, save it and update
     * load with matched vendor bill if any. Create order automatically if needed.
     * 
     * @param vendorBill
     *            Vendor Bill
     * @param carrierRefTypeMap
     *            mapping for carrier's and corresponding PLS' cost types.
     * @return {@link LoadEntity} that was matched to EDI or <code>null</code>.
     * @throws ApplicationException
     *             if total charges of Vendor Bill not equal to sum of cost items or if sales order was not
     *             created because of some error.
     */
    LoadEntity saveEdiVendorBill(CarrierInvoiceDetailsEntity vendorBill, Map<String, String> carrierRefTypeMap) throws ApplicationException;

    /**
     * Carrier should be rejected if customer add him to Reject List.
     * 
     * @param ediAccount
     *            edi number
     * @param scac
     *            {@link Carrier} scac code
     * @return true if rejected
     */
    boolean isReject(String ediAccount, String scac);

    /**
     * Save Vendor Bill with Inactive status.
     * 
     * @param vendorBill
     *            Vendor Bill
     */
    void saveArchiveEdiVendorBill(CarrierInvoiceDetailsEntity vendorBill);

}
