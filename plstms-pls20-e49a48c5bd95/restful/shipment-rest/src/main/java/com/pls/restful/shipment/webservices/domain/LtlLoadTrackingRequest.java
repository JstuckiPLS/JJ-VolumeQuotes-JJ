package com.pls.restful.shipment.webservices.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pls.shipment.domain.sterling.LoadTrackingJaxbBO;

/**
 * Request handler class for LTL Load Tracking.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ltlLoadTracking" })
@XmlRootElement(name = "LtlLoadTrackingRequest")
public class LtlLoadTrackingRequest {

    @XmlElement(namespace = "http://com.pls.load", name = "LtlLoadTracking", required = true)
    private LoadTrackingJaxbBO ltlLoadTracking;

    public LoadTrackingJaxbBO getLtlLoadTracking() {
        return ltlLoadTracking;
    }

    public void setLtlLoadTracking(LoadTrackingJaxbBO ltlLoadTracking) {
        this.ltlLoadTracking = ltlLoadTracking;
    }
}
