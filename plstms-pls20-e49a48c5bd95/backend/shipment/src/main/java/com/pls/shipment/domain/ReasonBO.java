package com.pls.shipment.domain;

import java.util.List;

import com.pls.core.domain.enums.CarrierInvoiceReasons;

/**
 * BO for Vendor Bill Reason.
 * 
 * @author Alexander Nalapko
 *
 */
public class ReasonBO {
    private CarrierInvoiceReasons reason;
    private String loadId;
    private String note;
    private List<Long> vendorBills;

    public List<Long> getVendorBills() {
        return vendorBills;
    }

    public void setVendorBills(List<Long> vendorBills) {
        this.vendorBills = vendorBills;
    }

    public CarrierInvoiceReasons getReason() {
        return reason;
    }

    public void setReason(CarrierInvoiceReasons reason) {
        this.reason = reason;
    }

    public String getLoadId() {
        return loadId;
    }

    public void setLoadId(String loadId) {
        this.loadId = loadId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
