package com.pls.dto.shipment;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.address.ZipDTO;


/**
 * Manual Bol Address DTO.
 *
 * @author Alexander Nalapko
 */
public class ManualBolAddressDTO {

    private Long id;

    private Long addressId;

    private String addressName;

    private String addressCode;

    private String address1;

    private String address2;

    private String email;

    private PhoneBO fax;

    private String contactName;

    private PhoneBO phone;

    private ZipDTO zip;

    private String pickupNotes;

    private String deliveryNotes;

    private String internalPickupNotes;

    private String internalDeliveryNotes;

    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PhoneBO getFax() {
        return fax;
    }

    public void setFax(PhoneBO fax) {
        this.fax = fax;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public PhoneBO getPhone() {
        return phone;
    }

    public void setPhone(PhoneBO phone) {
        this.phone = phone;
    }

    public ZipDTO getZip() {
        return zip;
    }

    public void setZip(ZipDTO zip) {
        this.zip = zip;
    }

    public String getPickupNotes() {
        return pickupNotes;
    }

    public void setPickupNotes(String pickupNotes) {
        this.pickupNotes = pickupNotes;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getInternalPickupNotes() {
        return internalPickupNotes;
    }

    public void setInternalPickupNotes(String internalPickupNotes) {
        this.internalPickupNotes = internalPickupNotes;
    }

    public String getInternalDeliveryNotes() {
        return internalDeliveryNotes;
    }

    public void setInternalDeliveryNotes(String internalDeliveryNotes) {
        this.internalDeliveryNotes = internalDeliveryNotes;
    }

}
