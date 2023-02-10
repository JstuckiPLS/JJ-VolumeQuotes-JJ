package com.pls.restful.shipment.webservices.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pls.shipment.domain.sterling.AcknowledgementJaxbBO;

/**
 * Request handler class for Functional Acknowledgments.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ltlAcknowledgement" })
@XmlRootElement(name = "LtlAcknowledgementRequest")
public class LtlAcknowledgementRequest {

    @XmlElement(namespace = "http://com.pls.load", name = "LtlAcknowledgement", required = true)
    private AcknowledgementJaxbBO ltlAcknowledgement;

    public AcknowledgementJaxbBO getLtlAcknowledgement() {
        return ltlAcknowledgement;
    }

    public void setLtlAcknowledgement(AcknowledgementJaxbBO ltlAcknowledgement) {
        this.ltlAcknowledgement = ltlAcknowledgement;
    }
}
