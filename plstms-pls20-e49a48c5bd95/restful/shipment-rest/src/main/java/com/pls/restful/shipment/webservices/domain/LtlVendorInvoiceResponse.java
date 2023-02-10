package com.pls.restful.shipment.webservices.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response handler class for LTL Vendor Invoice request.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "LtlVendorInvoiceResponse")
public class LtlVendorInvoiceResponse {

    @XmlElement(name = "Response", required = true)
    private String response;

    /**
     * Default Constructor.
     */
    public LtlVendorInvoiceResponse() {

    }

    /**
     * Constructor with response code.
     * 
     * @param response
     *            response code to be returned to the web service caller.
     */
    public LtlVendorInvoiceResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
