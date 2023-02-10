package com.pls.dto.address;

import java.math.BigDecimal;
import java.util.List;

import com.pls.core.domain.bo.PhoneBO;
import com.pls.dto.ShipmentNotificationsDTO;

/**
 * DTO for Address Book entry.
 * 
 * @author Alexey Tarasyuk
 */
public class AddressBookEntryDTO {

    private Long id;

    private Long addressId;

    private String addressName;

    private String contactName;

    private CountryDTO country;

    private String address1;

    private String address2;

    private ZipDTO zip;

    private String addressCode;

    private PhoneBO phone;

    private PhoneBO fax;

    private String email;

    private PickupAndDeliveryWindowDTO pickupWindowFrom;

    private PickupAndDeliveryWindowDTO pickupWindowTo;

    private PickupAndDeliveryWindowDTO deliveryWindowFrom;

    private PickupAndDeliveryWindowDTO deliveryWindowTo;

    private String pickupNotes;

    private String deliveryNotes;

    private String internalPickupNotes;

    private String internalDeliveryNotes;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Long version;

    private boolean sharedAddress;

    private Long createdBy;

    private String type;

    private Boolean isDefault;

    private List<ShipmentNotificationsDTO> shipmentNotifications;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * Country getter.
     * @return {@link CountryDTO}
     */
    //TODO remove redundant field 'country'. Use appropriate zip.getCountry() method
    public CountryDTO getCountry() {
        if (getZip() != null && getZip().getCountry() != null) {
            return getZip().getCountry();
        }
        return this.country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
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

    public ZipDTO getZip() {
        return zip;
    }

    public void setZip(ZipDTO zip) {
        this.zip = zip;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public PhoneBO getPhone() {
        return phone;
    }

    public void setPhone(PhoneBO phone) {
        this.phone = phone;
    }

    public PhoneBO getFax() {
        return fax;
    }

    public void setFax(PhoneBO fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PickupAndDeliveryWindowDTO getPickupWindowFrom() {
        return pickupWindowFrom;
    }

    public void setPickupWindowFrom(PickupAndDeliveryWindowDTO pickupWindowFrom) {
        this.pickupWindowFrom = pickupWindowFrom;
    }

    public PickupAndDeliveryWindowDTO getPickupWindowTo() {
        return pickupWindowTo;
    }

    public void setPickupWindowTo(PickupAndDeliveryWindowDTO pickupWindowTo) {
        this.pickupWindowTo = pickupWindowTo;
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

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isSharedAddress() {
        return sharedAddress;
    }

    public void setSharedAddress(boolean sharedAddress) {
        this.sharedAddress = sharedAddress;
    }

    public PickupAndDeliveryWindowDTO getDeliveryWindowFrom() {
        return deliveryWindowFrom;
    }

    public void setDeliveryWindowFrom(PickupAndDeliveryWindowDTO deliveryWindowFrom) {
        this.deliveryWindowFrom = deliveryWindowFrom;
    }

    public PickupAndDeliveryWindowDTO getDeliveryWindowTo() {
        return deliveryWindowTo;
    }

    public void setDeliveryWindowTo(PickupAndDeliveryWindowDTO deliveryWindowTo) {
        this.deliveryWindowTo = deliveryWindowTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public List<ShipmentNotificationsDTO> getShipmentNotifications() {
        return shipmentNotifications;
    }

    public void setShipmentNotifications(List<ShipmentNotificationsDTO> shipmentNotifications) {
        this.shipmentNotifications = shipmentNotifications;
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
