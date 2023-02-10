package com.pls.restful.shipment.webservices.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pls.shipment.domain.sterling.LoadAcknowledgementJaxbBO;

/**
 * Request handler class for LTL Load Tender Acknowledgment request.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ltlLoadTenderAck" })
@XmlRootElement(name = "LtlLoadTenderAckRequest")
public class LtlLoadTenderAckRequest {

    @XmlElement(namespace = "http://com.pls.load", name = "LtlLoadTenderAck", required = true)
    private LoadAcknowledgementJaxbBO ltlLoadTenderAck;

    public LoadAcknowledgementJaxbBO getLtlLoadTenderAck() {
        return ltlLoadTenderAck;
    }

    public void setLtlLoadTenderAck(LoadAcknowledgementJaxbBO ltlLoadTenderAck) {
        this.ltlLoadTenderAck = ltlLoadTenderAck;
    }
}
