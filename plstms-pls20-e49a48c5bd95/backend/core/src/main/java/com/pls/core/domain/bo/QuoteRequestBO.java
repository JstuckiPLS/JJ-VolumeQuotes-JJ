package com.pls.core.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Request for quotes sent by customer. It is emails to GSM person.
 * 
 * @author Viacheslav Krot
 * 
 */
public class QuoteRequestBO {

    private String contactName;

    private String originCity;

    private String originState;

    private String destinationCity;

    private String destinationState;

    private String commodity;

    private Integer weight;

    private String containerDescription;

    private BigDecimal freightClass;

    private String contactPhone;

    private String contactEmail;

    private Date shippingDate;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginState() {
        return originState;
    }

    public void setOriginState(String originState) {
        this.originState = originState;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(String destinationState) {
        this.destinationState = destinationState;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getContainerDescription() {
        return containerDescription;
    }

    public void setContainerDescription(String containerDescription) {
        this.containerDescription = containerDescription;
    }

    public BigDecimal getFreightClass() {
        return freightClass;
    }

    public void setFreightClass(BigDecimal freightClass) {
        this.freightClass = freightClass;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Date getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(Date shippingDate) {
        this.shippingDate = shippingDate;
    }

}
