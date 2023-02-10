package com.pls.shipment.domain.sterling;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.domain.sterling.utils.DateTimeAdapter;

/**
 * Acknowledge message BO for EDI 997 received from carrier or customer.
 * 
 * @author Jasmin Dhamelia
 * 
 */
@XmlRootElement(name = "LTLAcknowledgement")
@XmlAccessorType(XmlAccessType.FIELD)
public class AcknowledgementJaxbBO implements IntegrationMessageBO {

    private static final long serialVersionUID = -2597541957666411920L;

    @XmlElement(name = "PersonId")
    private Long personId;

    @XmlElement(name = "MessageType")
    private String messageType;

    @XmlElement(name = "Scac")
    private String scac;

    @XmlElement(name = "ShipmentNo")
    private String shipmentNo;

    @XmlElement(name = "LoadId")
    private Long loadId;

    @XmlElement(name = "Bol")
    private String bol;

    @XmlElement(name = "CustomerOrgId")
    private Long customerOrgId;

    @XmlElement(name = "ControlNo")
    private String controlNo;

    @XmlElement(name = "TransactionSetId")
    private String transactionSetId;

    @XmlElement(name = "Status")
    private String status;

    @XmlElement(name = "SegmentError")
    private String segmentError;

    @XmlElement(name = "ElementError")
    private String elementError;

    @XmlElement(name = "ErrorCode")
    private String errorCode;

    @XmlElement(name = "ErrorMessage")
    private String errorMessage;

    @XmlElement(name = "B2biFileName")
    private String b2biFileName;

    @XmlElement(name = "RecvDateTime")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date recvDateTime;

    public String getControlNo() {
        return controlNo;
    }

    public void setControlNo(String controlNo) {
        this.controlNo = controlNo;
    }

    public String getTransactionSetId() {
        return transactionSetId;
    }

    public void setTransactionSetId(String transactionSetId) {
        this.transactionSetId = transactionSetId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSegmentError() {
        return segmentError;
    }

    public void setSegmentError(String segmentError) {
        this.segmentError = segmentError;
    }

    public String getElementError() {
        return elementError;
    }

    public void setElementError(String elementError) {
        this.elementError = elementError;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getB2biFileName() {
        return b2biFileName;
    }

    public void setB2biFileName(String b2biFileName) {
        this.b2biFileName = b2biFileName;
    }

    public Date getRecvDateTime() {
        return recvDateTime;
    }

    public void setRecvDateTime(Date recvDateTime) {
        this.recvDateTime = recvDateTime;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getShipmentNo() {
        return shipmentNo;
    }

    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public Long getCustomerOrgId() {
        return customerOrgId;
    }

    public void setCustomerOrgId(Long customerOrgId) {
        this.customerOrgId = customerOrgId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getPersonId()).append(getScac()).append(getShipmentNo()).append(getLoadId())
                .append(getBol()).append(getCustomerOrgId()).append(getControlNo()).append(getTransactionSetId()).append(getStatus())
                .append(getSegmentError()).append(getElementError()).append(getErrorCode()).append(getErrorMessage()).append(getB2biFileName())
                .append(getRecvDateTime());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof AcknowledgementJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                AcknowledgementJaxbBO other = (AcknowledgementJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getPersonId(), other.getPersonId()).append(getScac(), other.getScac())
                        .append(getShipmentNo(), other.getShipmentNo()).append(getLoadId(), other.getLoadId()).append(getBol(), other.getBol())
                        .append(getCustomerOrgId(), other.getCustomerOrgId()).append(getControlNo(), other.getControlNo())
                        .append(getTransactionSetId(), other.getTransactionSetId()).append(getStatus(), other.getStatus())
                        .append(getSegmentError(), other.getSegmentError()).append(getElementError(), other.getElementError())
                        .append(getErrorCode(), other.getErrorCode()).append(getErrorMessage(), other.getErrorMessage())
                        .append(getB2biFileName(), other.getB2biFileName()).append(getRecvDateTime(), other.getRecvDateTime());

                result = builder.isEquals();
            }
        }
        return result;
    }

}
