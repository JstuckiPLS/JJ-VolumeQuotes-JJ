package com.pls.extint.shared;

/**
 * VO for create pick up request.
 * 
 * @author PAVANI CHALLA
 *
 */
public class CreatePickupRequestVO {

    private String customerReferenceNumber;

    private Integer weight;

    private Integer handlingUnits;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String stateCode;

    private String zipCode;

    private String zipCode4;

    private String destinationZipCode;

    private String contact;

    private String contactPhone;

    private String pickupInstructions;

    private String billingInstructions;

    private Long loadId;

    private String bol;

    public String getCustomerReferenceNumber() {
        return customerReferenceNumber;
    }

    public void setCustomerReferenceNumber(String customerReferenceNumber) {
        this.customerReferenceNumber = customerReferenceNumber;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHandlingUnits() {
        return handlingUnits;
    }

    public void setHandlingUnits(Integer handlingUnits) {
        this.handlingUnits = handlingUnits;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCode4() {
        return zipCode4;
    }

    public void setZipCode4(String zipCode4) {
        this.zipCode4 = zipCode4;
    }

    public String getDestinationZipCode() {
        return destinationZipCode;
    }

    public void setDestinationZipCode(String destinationZipCode) {
        this.destinationZipCode = destinationZipCode;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getPickupInstructions() {
        return pickupInstructions;
    }

    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions;
    }

    public String getBillingInstructions() {
        return billingInstructions;
    }

    public void setBillingInstructions(String billingInstructions) {
        this.billingInstructions = billingInstructions;
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


}
