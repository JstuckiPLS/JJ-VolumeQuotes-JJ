package com.pls.restful.shipment.webservices.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response handler class for LTL Load Message request.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "LtlLoadMessageResponse")
public class LtlLoadMessageResponse {

    @XmlElement(name = "Response", required = true)
    private String response;

    /**
     * Default Constructor.
     */
    public LtlLoadMessageResponse() {

    }

    /**
     * Constructor with response code.
     * 
     * @param response
     *            response code to be returned to the web service caller.
     */
    public LtlLoadMessageResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
