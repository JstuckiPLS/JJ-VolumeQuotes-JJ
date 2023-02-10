package com.pls.restful.shipment.webservices.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pls.shipment.domain.sterling.LoadUpdateJaxbBO;

/**
 * Load update request for updating load's specific fields.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ltlLoadUpdates" })
@XmlRootElement(name = "LtlLoadUpdateRequest")
public class LtlLoadUpdateRequest {

    @XmlElementWrapper(name = "LtlLoadUpdates", namespace = "http://com.pls.load", required = true)
    @XmlElement(name = "LtlLoadUpdate")
    private List<LoadUpdateJaxbBO> ltlLoadUpdates;

    public List<LoadUpdateJaxbBO> getLtlLoadUpdates() {
        return ltlLoadUpdates;
    }

    public void setLtlLoadUpdates(List<LoadUpdateJaxbBO> ltlLoadUpdates) {
        this.ltlLoadUpdates = ltlLoadUpdates;
    }

}
