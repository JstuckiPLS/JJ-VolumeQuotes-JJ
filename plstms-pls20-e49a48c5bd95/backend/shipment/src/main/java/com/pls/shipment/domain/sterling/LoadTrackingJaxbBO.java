package com.pls.shipment.domain.sterling;

import java.util.ArrayList;
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

import com.pls.core.domain.sterling.bo.IntegrationMessageBO;
import com.pls.core.domain.sterling.utils.DateTimeAdapter;

/**
 * Load Tracking shipment DAO. Class designed to handle EDI 214 inbound communication.
 *
 * @author Jasmin Dhamelia
 */
@XmlRootElement(name = "LTLLoadTracking")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoadTrackingJaxbBO implements IntegrationMessageBO {

    private static final long serialVersionUID = 7741438329900645675L;

    @XmlElement(name = "MessageType")
    private String messageType;

    @XmlElement(name = "Scac")
    private String scac;

    @XmlElement(name = "PersonId")
    private Long personId;

    @XmlElement(name = "CustomerOrgId")
    private Long customerOrgId;

    @XmlElement(name = "LoadId")
    private Long loadId;

    @XmlElement(name = "Bol")
    private String bol;

    @XmlElement(name = "ShipmentNo")
    private String shipmentNo;

    @XmlElement(name = "ProNumber")
    private String proNumber;

    @XmlElement(name = "EdiAccountNum")
    private String ediAccountNum;

    @XmlElement(name = "B2biFileName")
    private String b2biFileName;

    @XmlElement(name = "RecvDateTime")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private Date recvDateTime;

    @XmlElementWrapper(name = "TrackingStatuses")
    @XmlElement(name = "TrackingStatus")
    private List<TrackingStatusJaxbBO> trackingStatuses;

    @XmlElementWrapper(name = "TrackingMaterials")
    @XmlElement(name = "TrackingMaterial")
    private List<TrackingMaterialJaxbBO> trackingMaterials;

    @XmlElementWrapper(name = "Addresses")
    @XmlElement(name = "Address")
    private List<AddressJaxbBO> addresses;

    public List<AddressJaxbBO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressJaxbBO> addresses) {
        this.addresses = addresses;
    }

    public List<TrackingStatusJaxbBO> getTrackingStatuses() {
        return trackingStatuses;
    }

    public void setTrackingStatuses(List<TrackingStatusJaxbBO> trackingStatuses) {
        this.trackingStatuses = trackingStatuses;
    }

    public List<TrackingMaterialJaxbBO> getTrackingMaterials() {
        return trackingMaterials;
    }

    public void setTrackingMaterials(List<TrackingMaterialJaxbBO> trackingMaterials) {
        this.trackingMaterials = trackingMaterials;
    }

    public Date getRecvDateTime() {
        return recvDateTime;
    }

    public void setRecvDateTime(Date recvDateTime) {
        this.recvDateTime = recvDateTime;
    }

    public String getB2biFileName() {
        return b2biFileName;
    }

    public void setB2biFileName(String b2biFileName) {
        this.b2biFileName = b2biFileName;
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

    public String getEdiAccountNum() {
        return ediAccountNum;
    }

    public void setEdiAccountNum(String ediAccountNum) {
        this.ediAccountNum = ediAccountNum;
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
    public Long getLoadId() {
        return loadId;
    }

    @Override
    public void setLoadId(Long loadId) {
        this.loadId = loadId;
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
    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
     * Adding address to list.
     *
     * @param address Address to add
     * */
    public void addAddress(AddressJaxbBO address) {
        if (addresses == null) {
            this.addresses = new ArrayList<AddressJaxbBO>();
        }
        this.addresses.add(address);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof LoadTrackingJaxbBO) {
            if (obj == this) {
                result = true;
            } else {
                LoadTrackingJaxbBO other = (LoadTrackingJaxbBO) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getTrackingStatuses(), other.getTrackingStatuses());
                builder.append(getTrackingMaterials(), other.getTrackingMaterials());
                builder.append(getB2biFileName(), other.getB2biFileName());
                builder.append(getScac(), other.getScac());
                builder.append(getBol(), other.getBol());
                builder.append(getProNumber(), other.getProNumber());
                builder.append(getEdiAccountNum(), other.getEdiAccountNum());
                builder.append(getShipmentNo(), other.getShipmentNo());
                builder.append(getLoadId(), other.getLoadId());
                builder.append(getCustomerOrgId(), other.getCustomerOrgId());
                builder.append(getPersonId(), other.getPersonId());
                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getTrackingStatuses());
        builder.append(getTrackingMaterials());
        builder.append(getRecvDateTime());
        builder.append(getB2biFileName());
        builder.append(getScac());
        builder.append(getBol());
        builder.append(getProNumber());
        builder.append(getEdiAccountNum());
        builder.append(getShipmentNo());
        builder.append(getLoadId());
        builder.append(getCustomerOrgId());
        builder.append(getPersonId());
        return builder.toHashCode();
    }
}
