package com.pls.shipment.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.HasVersion;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.domain.enums.PointType;

/**
 * manual BOL address entity.
 * 
 * @author Alexander Nalаpko
 */
@Entity
@Table(name = "MANUAL_BOL_ADDRESS")
public class ManualBolAddressEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {

    private static final long serialVersionUID = -7188321048844728492L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manual_bol_address_sequence")
    @SequenceGenerator(name = "manual_bol_address_sequence", sequenceName = "MANUAL_BOL_ADDRESS_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANUAL_BOL_ID", nullable = false)
    private ManualBolEntity manualBol;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID", nullable = false)
    private AddressEntity address;

    @Column(name = "CONTACT")
    private String contact;

    @Column(name = "ADDRESS_CODE")
    private String addressCode;

    @Column(name = "ADDRESS1")
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "CONTACT_EMAIL")
    private String contactEmail;

    @Column(name = "CONTACT_FAX")
    private String contactFax;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @Column(name = "CONTACT_PHONE")
    private String contactPhone;

    @Column(name = "PICKUP_NOTES")
    private String pickupNotes;

    @Column(name = "DELIVERY_NOTES")
    private String deliveryNotes;

    @Column(name = "INT_PICKUP_NOTES")
    private String internalPickupNotes;

    @Column(name = "INT_DELIVERY_NOTES")
    private String internalDeliveryNotes;

    @Column(name = "POINT_TYPE", columnDefinition = "char(1)", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.PointType"),
            @Parameter(name = "identifierMethod", value = "getPointType"),
            @Parameter(name = "valueOfMethod", value = "getPointTypeBy") })
    private PointType pointType;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    /**
     * Default Constructor.
     * Will be used by Hibernate.
     */
    protected ManualBolAddressEntity() {
    }

    /**
     * Constructor to create entity with specified {@link PointType}.
     * 
     * @param pointType - Not <code>null</code> {@link PointType}.
     */
    public ManualBolAddressEntity(PointType pointType) {
        this.pointType = pointType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public PlainModificationObject getModification() {
        return this.modification;
    }

    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
       this.id = id;
    }

    public ManualBolEntity getManualBol() {
        return manualBol;
    }

    public void setManualBol(ManualBolEntity manualBol) {
        this.manualBol = manualBol;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactFax() {
        return contactFax;
    }

    public void setContactFax(String contactFax) {
        this.contactFax = contactFax;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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
