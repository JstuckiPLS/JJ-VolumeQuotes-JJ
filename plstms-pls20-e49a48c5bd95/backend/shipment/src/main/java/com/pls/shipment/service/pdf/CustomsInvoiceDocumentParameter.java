package com.pls.shipment.service.pdf;

import com.pls.core.service.pdf.PdfDocumentParameter;
import com.pls.shipment.domain.LoadEntity;

/**
 * Implementation of {@link PdfDocumentParameter} for Consignee Invoice-Safway.
 * 
 * @author Brichak Aleksandr
 */
public class CustomsInvoiceDocumentParameter implements PdfDocumentParameter {

    private LoadEntity load;

    /**
     * Constructor.
     * 
     * @param load
     *            {@link LoadEntity}
     */
    public CustomsInvoiceDocumentParameter(LoadEntity load) {
        this.load = load;
    }

    public LoadEntity getLoad() {
        return load;
    }

    public void setLoad(LoadEntity load) {
        this.load = load;
    }
}
