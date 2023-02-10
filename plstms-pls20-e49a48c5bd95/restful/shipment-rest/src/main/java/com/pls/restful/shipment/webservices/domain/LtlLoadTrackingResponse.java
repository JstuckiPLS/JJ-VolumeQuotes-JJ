package com.pls.restful.shipment.webservices.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response handler class for LTL Load Tracking request.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "LtlLoadTrackingResponse")
public class LtlLoadTrackingResponse {

    @XmlElement(name = "Response", required = true)
    private String response;

    /**
     * Default Constructor.
     */
    public LtlLoadTrackingResponse() {

    }

    /**
     * Constructor with response code.
     * 
     * @param response
     *            response code to be returned to the web service caller.
     */
    public LtlLoadTrackingResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
