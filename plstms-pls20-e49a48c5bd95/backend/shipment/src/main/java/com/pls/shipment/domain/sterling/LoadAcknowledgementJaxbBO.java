package com.pls.shipment.domain.sterling;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.domain.sterling.utils.DateTimeAdapter;

/**
 * BO representing the LTL Load Tender Acknowledgement (EDI 990) both when received from carrier and when sent to customer from PSLPRO.
 *
 * Variable status is the code we receive to identify whether the tender has been accepted or rejected, such as A, B, C, ... Variable declined reason
 * code is the code we get declaring why the load has not been accepted.
 *
 * @author Yasaman Palumbo
 *
 */
@XmlRootElement(name = "LTLLoadTenderAck")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoadAcknowledgementJaxbBO implements IntegrationMessageBO {

    private static final long serialVersionUID = -2597541957666411920L;

    @XmlElement(name = "Scac")
    private String scac;

    @XmlElement(name = "CustomerOrgId")
    private Long customerOrgId;

    @XmlElement(name = "LoadId")
    private Long loadId;

    @XmlElement(name = "Bol")
    private String bol;

    @XmlElement(name = "ProNumber")
    private String proNumber;

    @XmlElement(name = "ShipmentNo")
    private String shipmentNo;

    @XmlElement(name = "PersonId")
    private Long personId;

    @XmlElement(name = "TrailerNum")
    private String trailerNum;

    @XmlElement(name = "MessageType")
    private String messageType;

    // Accepted or declined (A/D)
    @XmlElement(name = "Status")
    private String status;

    @XmlElement(name = "StatusNotes")
    private String statusNotes;

    // The reason code for rejection
    @XmlElement(name = "DeclineReasonCd")
    private String declineReasonCd;

    // Description of the reason code for rejection
    @XmlElement(name = "DeclineReasonDesc")
    private String declineReasonDesc;

    @XmlElement(name = "B2biFileName")
    private String b2biFileName;

    @XmlElement(name = "RecvDateTime")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date recvDateTime;

    @XmlElementWrapper(name = "AddlRefNumbers")
    @XmlElement(name = "AddlRefNumber")
    private List<AddlRefNumberJaxbBO> addlRefNumbers;

    @Override
    public String getScac() {
        return scac;
    }

    @Override
    public void setScac(String scac) {
        this.scac = scac;
    }

    @Override
    public String getBol() {
        return bol;
    }

    @Override
    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getProNumber() {
        return proNumber;
    }

    public void setProNumber(String proNumber) {
        this.proNumber = proNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Long getCustomerOrgId() {
        return customerOrgId;
    }

    @Override
    public void setCustomerOrgId(Long customerOrgId) {
        this.customerOrgId = customerOrgId;
    }

    public String getB2biFileName() {
        return b2biFileName;
    }

    public void setB2biFileName(String b2biFileName) {
        this.b2biFileName = b2biFileName;
    }

    @Override
    public Long getLoadId() {
        return loadId;
    }

    @Override
    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    @Override
    public String getShipmentNo() {
        return shipmentNo;
    }

    @Override
    public void setShipmentNo(String shipmentNo) {
        this.shipmentNo = shipmentNo;
    }

    public String getDeclineReasonCd() {
        return declineReasonCd;
    }

    public void setDeclineReasonCd(String declineReasonCd) {
        this.declineReasonCd = declineReasonCd;
    }

    public String getDeclineReasonDesc() {
        return declineReasonDesc;
    }

    public void setDeclineReasonDesc(String declineReasonDesc) {
        this.declineReasonDesc = declineReasonDesc;
    }

    public String getTrailerNum() {
        return trailerNum;
    }

    public void setTrailerNum(String trailerNum) {
        this.trailerNum = trailerNum;
    }

    public Date getRecvDateTime() {
        return recvDateTime;
    }

    public void setRecvDateTime(Date recvDateTime) {
        this.recvDateTime = recvDateTime;
    }

    public List<AddlRefNumberJaxbBO> getAddlRefNumbers() {
        return addlRefNumbers;
    }

    public void setAddlRefNumbers(List<AddlRefNumberJaxbBO> addlRefNumbers) {
        this.addlRefNumbers = addlRefNumbers;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append(getScac()).append(getCustomerOrgId()).append(getB2biFileName()).append(getLoadId())
                .append(getBol()).append(getProNumber()).append(getShipmentNo()).append(getPersonId()).append(getRecvDateTime()).append(getStatus())
                .append(getDeclineReasonCd()).append(getDeclineReasonDesc()).append(getTrailerNum()).append(getAddlRefNumbers())
                .append(getStatusNotes());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LoadAcknowledgementJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                LoadAcknowledgementJaxbBO other = (LoadAcknowledgementJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder().append(getScac(), other.getScac()).append(getCustomerOrgId(), other.getCustomerOrgId())
                        .append(getB2biFileName(), other.getB2biFileName()).append(getLoadId(), other.getLoadId()).append(getBol(), other.getBol())
                        .append(getProNumber(), other.getProNumber()).append(getShipmentNo(), other.getShipmentNo())
                        .append(getPersonId(), other.getPersonId()).append(getStatus(), other.getStatus())
                        .append(getDeclineReasonCd(), other.getDeclineReasonCd()).append(getDeclineReasonDesc(), other.getDeclineReasonDesc())
                        .append(getTrailerNum(), other.getTrailerNum()).append(getAddlRefNumbers(), other.getAddlRefNumbers())
                        .append(getStatusNotes(), other.getStatusNotes());

                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this).append("Scac", getScac()).append("CustomerOrgId", getCustomerOrgId())
                .append("B2BiFileName", getB2biFileName()).append("LoadId", getLoadId()).append("Bol", getBol()).append("ProNumber", getProNumber())
                .append("ShipmentNo", getShipmentNo()).append("PersonId", getPersonId()).append("Status", getStatus())
                .append("DeclineReasonCode", getDeclineReasonCd()).append("DeclineReasonDesc", getDeclineReasonDesc())
                .append("TrailerNum", getTrailerNum()).append("AddlRefNumbers", getAddlRefNumbers()).append("StatusNotes", getStatusNotes());

        return builder.toString();
    }

    public String getStatusNotes() {
        return statusNotes;
    }

    public void setStatusNotes(String statusNotes) {
        this.statusNotes = statusNotes;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
