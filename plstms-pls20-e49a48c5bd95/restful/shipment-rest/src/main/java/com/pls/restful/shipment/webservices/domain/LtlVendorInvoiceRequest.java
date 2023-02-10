package com.pls.restful.shipment.webservices.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;

/**
 * Request handler class for LTL Vendor Invoice request.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ltlVendorInvoice" })
@XmlRootElement(name = "LtlVendorInvoiceRequest")
public class LtlVendorInvoiceRequest {

    @XmlElement(namespace = "http://com.pls.load", name = "LtlVendorInvoice", required = true)
    private LoadMessageJaxbBO ltlVendorInvoice;

    public LoadMessageJaxbBO getLtlVendorInvoice() {
        return ltlVendorInvoice;
    }

    public void setLtlVendorInvoice(LoadMessageJaxbBO ltlVendorInvoice) {
        this.ltlVendorInvoice = ltlVendorInvoice;
    }
}
