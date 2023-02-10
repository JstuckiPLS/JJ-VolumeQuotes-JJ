package com.pls.shipment.domain.sterling;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.pls.core.domain.sterling.bo.IntegrationMessageBO;

/**
 * BO representing the list of invoices/loads in an invoice.
 * 
 * @author Pavani Challa
 * 
 */
@XmlRootElement(name = "LtlLoadInvoices")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoadInvoiceJaxbBO implements IntegrationMessageBO {

    private static final long serialVersionUID = -7442832408177946460L;

    @XmlElement(name = "LtlLoadInvoice")
    private List<LoadMessageJaxbBO> ltlLoadInvoices;

    @XmlTransient
    private Long personId;

    @XmlTransient
    private Long loadId;

    @XmlTransient
    private Long customerOrgId;

    @XmlTransient
    private String bol;

    @XmlTransient
    private String scac;

    @XmlTransient
    private String shipmentNo;

    @XmlTransient
    private String messageType;

    public List<LoadMessageJaxbBO> getLtlLoadInvoices() {
        return ltlLoadInvoices;
    }

    public void setLtlLoadInvoices(List<LoadMessageJaxbBO> ltlLoadInvoices) {
        this.ltlLoadInvoices = ltlLoadInvoices;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType =  messageType;
    }

    @Override
    public String getScac() {
        return scac;
    }

    @Override
    public void setScac(String scac) {
        this.scac = scac;
    }

    @Override
    public String getShipmentNo() {
        return shipmentNo;
    }

    @Override
    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;
    }

    @Override
    public String getBol() {
        return bol;
    }

    @Override
    public void setBol(String bol) {
        this.bol = bol;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId =  personId;
    }

    @Override
    public Long getCustomerOrgId() {
        return customerOrgId;
    }

    @Override
    public void setCustomerOrgId(Long customerOrgId) {
       this.customerOrgId = customerOrgId;
    }

    @Override
    public Long getLoadId() {
        return loadId;
    }

    @Override
    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }
}
