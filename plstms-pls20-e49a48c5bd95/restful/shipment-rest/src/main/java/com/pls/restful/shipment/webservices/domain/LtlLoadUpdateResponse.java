package com.pls.restful.shipment.webservices.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "LtlLoadUpdateResponse")
public class LtlLoadUpdateResponse {

    @XmlElement(name = "Response", required = true)
    private String response;

    public LtlLoadUpdateResponse() {

    }

    /**
     * Creates a new response object.
     * 
     * @param response
     *            response code to be returned to the web service caller.
     */
    public LtlLoadUpdateResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
