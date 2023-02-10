package com.pls.shipment.domain.sterling;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.sterling.bo.IntegrationMessageBO;

/**
 * Load update message updating a load with specific identifier, with values
 * which are provided as non-null.
 */
@XmlRootElement(name = "LtlLoadUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoadUpdateJaxbBO implements IntegrationMessageBO {

    private static final long serialVersionUID = -1494731122598556916L;

    @XmlElement(name = "MessageType")
    private String messageType;

    @XmlElement(name = "PersonId")
    private Long personId;

    @XmlElement(name = "CustomerOrgId")
    private Long customerOrgId;

    @XmlElement(name = "ShipmentNo")
    private String shipmentNo;

    @XmlElement(name = "PoNumber")
    private String poNumber;

    @Override
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getCustomerOrgId() {
        return customerOrgId;
    }

    public void setCustomerOrgId(Long customerOrgId) {
        this.customerOrgId = customerOrgId;
    }

    public String getShipmentNo() {
        return shipmentNo;
    }

    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getMessageType()).append(getPersonId()).append(getCustomerOrgId())
                .append(getShipmentNo()).append(getPoNumber());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LoadUpdateJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                LoadUpdateJaxbBO other = (LoadUpdateJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getMessageType(), other.getMessageType()).append(getPersonId(), other.getPersonId())
                        .append(getCustomerOrgId(), other.getCustomerOrgId())
                        .append(getShipmentNo(), other.getShipmentNo()).append(getPoNumber(), other.getPoNumber());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("MessageType", getMessageType()).append("PersonId", getPersonId())
                .append("CustomerOrgId", getCustomerOrgId()).append("ShipmentNo", getShipmentNo())
                .append("PoNumber", getPoNumber());

        return builder.toString();
    }

    @Override
    public String getScac() {
        return null; // Not available
    }

    @Override
    public void setScac(String scac) {
        // Not available
    }

    @Override
    public Long getLoadId() {
        return null; // Not available
    }

    @Override
    public void setLoadId(Long loadId) {
        // Not available
    }

    @Override
    public String getBol() {
        return null;// Not available
    }

    @Override
    public void setBol(String bol) {
        // Not available
    }
}
