package com.pls.restful.shipment.webservices.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pls.shipment.domain.sterling.LoadMessageJaxbBO;

/**
 * Request handler class for LTL Load Message requests.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ltlLoadMessage" })
@XmlRootElement(name = "LtlLoadMessageRequest")
public class LtlLoadMessageRequest {

    @XmlElement(namespace = "http://com.pls.load", name = "LtlLoadMessage", required = true)
    private LoadMessageJaxbBO ltlLoadMessage;

    public LoadMessageJaxbBO getLtlLoadMessage() {
        return ltlLoadMessage;
    }

    public void setLtlLoadMessage(LoadMessageJaxbBO ltlLoadMessage) {
        this.ltlLoadMessage = ltlLoadMessage;
    }

}
