package com.pls.core.domain.address;

import java.sql.Time;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.AddressType;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.core.shared.Status;

/**
 * Entity for LTL that stands for address for address book etc.
 *
 * @author Denis Zhupinsky (Team International)
 */
@Entity
@Table(name = "USER_ADDRESS_BOOK", schema = "FLATBED")
public class UserAddressBookEntity implements Identifiable<Long>, HasModificationInfo {

    public static final String Q_GET_NEXT_ADDR_NAME_CODE = "com.pls.core.domain.address.UserAddressBookEntity.Q_GET_NEXT_ADDR_NAME_CODE";
    public static final String Q_GET_NEXT_ADDRESS_CODE = "com.pls.core.domain.address.UserAddressBookEntity.Q_GET_NEXT_ADDRESS_CODE";
    public static final String Q_DELETE_ADDRESS_BOOK_ENTRY = "com.pls.core.domain.address.UserAddressBookEntity.Q_DELETE_ADDRESS_BOOK_ENTRY";
    public static final String Q_CHECK_ADDRESS_CODE_EXISTS = "com.pls.core.domain.address.UserAddressBookEntity.Q_CHECK_ADDRESS_CODE_EXISTS";
    public static final String Q_FIND_BY_ID = "com.pls.core.domain.address.UserAddressBookEntity.Q_FIND_BY_ID";
    public static final String Q_GET_CUSTOMER_ADDRESS_BOOK_FOR_USER =
            "com.pls.core.domain.address.UserAddressBookEntity.Q_GET_CUSTOMER_ADDRESS_BOOK_FOR_USER";
    public static final String Q_FIND_BY_NAME_AND_CODE = "com.pls.core.domain.address.UserAddressBookEntity.Q_FIND_BY_NAME_AND_CODE";
    public static final String Q_SEARCH_ADDRESSES = "com.pls.core.domain.address.UserAddressBookEntity.Q_SEARCH_ADDRESSES";
    public static final String Q_ADDRESS_AUTOCOMPLETE = "com.pls.core.domain.address.UserAddressBookEntity.Q_ADDRESSES_AUTOCOMPLETE";
    public static final String Q_GET_CUSTOMER_ADDRESS_BOOKS = "com.pls.core.domain.address.UserAddressBookEntity.Q_GET_CUSTOMER_ADDRESS_BOOKS";
    public static final String Q_FIND_BY_ORG_AND_CODE = "com.pls.core.domain.address.UserAddressBookEntity.Q_FIND_BY_ORG_AND_CODE";
    public static final String Q_GET_BY_ZIP = "com.pls.core.domain.address.UserAddressBookEntity.Q_GET_BY_ZIP";
    public static final String RESET_DEFAULT_ADDRESS_FOR_CUSTOMER =
            "com.pls.core.domain.address.UserAddressBookEntity.RESET_DEFAULT_ADDRESS_FOR_CUSTOMER";
    public static final String GET_DEFAULT_FREIGHT_BILL_PAY_TO_ADDRESS =
            "com.pls.core.domain.address.UserAddressBookEntity.GET_DEFAULT_FREIGHT_BILL_PAY_TO_ADDRESS";
    public static final String Q_GET_ADDRESS_BOOK_FOR_FREIGHT_BILL =
            "com.pls.core.domain.address.UserAddressBookEntity.Q_GET_ADDRESS_BOOK_FOR_FREIGHT_BILL";

    public static final int DELIVERY_NOTES_LENGTH = 3000;

    public static final int PICKUP_NOTES_LENGTH = 3000;

    private static final long serialVersionUID = 2786411601061677581L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usr_addr_book_sequence")
    @SequenceGenerator(name = "usr_addr_book_sequence", sequenceName = "USR_ADDR_BOOK_SEQ", allocationSize = 1)
    @Column(name = "ADDR_BOOK_ID")
    private Long id;

    @Column(name = "ADDRESS_NAME")
    private String addressName;

    @Column(name = "PERSON_ID", updatable = true, insertable = true)
    private Long personId;

    @Column(name = "ORG_ID", nullable = false, insertable = true, updatable = true)
    private Long orgId;

    @JoinColumn(name = "ORG_ID", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomerEntity customer;

    // we do not need to remove this entity with cascaded, but anyway we should not delete any entity at
    // system
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID", nullable = false)
    private AddressEntity address;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @Column(name = "PICKUP_FROM_TIME")
    private Time pickupFrom;

    @Column(name = "PICKUP_TO_TIME")
    private Time pickupTo;

    @Column(name = "DELIVERY_FROM_TIME")
    private Time deliveryFrom;

    @Column(name = "DELIVERY_TO_TIME")
    private Time deliveryTo;

    @Column(name = "ADDRESS_CODE")
    private String addressCode;

    @Column(name = "EMAIL_ADDRESS")
    private String email;

    @Column(name = "PICKUP_NOTES", length = PICKUP_NOTES_LENGTH)
    private String pickupNotes;

    @Column(name = "DELIVERY_NOTES", length = DELIVERY_NOTES_LENGTH)
    private String deliveryNotes;

    @Column(name = "INT_DELIVERY_NOTES", length = DELIVERY_NOTES_LENGTH)
    private String internalDeliveryNotes;

    @Column(name = "INT_PICKUP_NOTES", length = PICKUP_NOTES_LENGTH)
    private String internalPickupNotes;

    @ManyToOne
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST, org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "PHONE_ID")
    private PhoneEntity phone;

    @ManyToOne
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST, org.hibernate.annotations.CascadeType.MERGE,
            org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "FAX_ID")
    private PhoneEntity fax;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AddressNotificationsEntity> addressNotifications;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "IS_DEFAULT")
    @Type(type = "yes_no")
    private Boolean isDefault;

    @Column(name = "TYPE")
    @Type(type = "com.pls.core.domain.usertype.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "com.pls.core.domain.enums.AddressType"),
            @Parameter(name = "identifierMethod", value = "getCode"),
            @Parameter(name = "valueOfMethod", value = "getByCode")})
    private AddressType type = AddressType.SHIPPING;

    @Version
    @Column(name = "VERSION")
    private long version = 1L;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Time getPickupFrom() {
        return pickupFrom;
    }

    public void setPickupFrom(Time pickupFrom) {
        this.pickupFrom = pickupFrom;
    }

    public Time getPickupTo() {
        return pickupTo;
    }

    public void setPickupTo(Time pickupTo) {
        this.pickupTo = pickupTo;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public PhoneEntity getPhone() {
        return phone;
    }

    public void setPhone(PhoneEntity phone) {
        this.phone = phone;
    }

    public PhoneEntity getFax() {
        return fax;
    }

    public void setFax(PhoneEntity fax) {
        this.fax = fax;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long customerUserId) {
        this.personId = customerUserId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Time getDeliveryFrom() {
        return deliveryFrom;
    }

    public void setDeliveryFrom(Time deliveryFrom) {
        this.deliveryFrom = deliveryFrom;
    }

    public Time getDeliveryTo() {
        return deliveryTo;
    }

    public void setDeliveryTo(Time deliveryTo) {
        this.deliveryTo = deliveryTo;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public Set<AddressNotificationsEntity> getAddressNotifications() {
        return addressNotifications;
    }

    public void setAddressNotifications(Set<AddressNotificationsEntity> addressNotifications) {
        this.addressNotifications = addressNotifications;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getAddressName()).append(this.getPersonId()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserAddressBookEntity)) {
            return false;
        }
        final UserAddressBookEntity other = (UserAddressBookEntity) obj;
        return new EqualsBuilder().append(this.getAddressName(), other.getAddressName())
                .append(this.getPersonId(), other.getPersonId()).
                isEquals();
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setInternalDeliveryNotes(String internalDeliveryNotes) {
        this.internalDeliveryNotes = internalDeliveryNotes;
    }

    public String getInternalDeliveryNotes() {
        return this.internalDeliveryNotes;
    }

    public void setInternalPickupNotes(String internalPickupNotes) {
        this.internalPickupNotes = internalPickupNotes;
    }

    public String getInternalPickupNotes() {
        return this.internalPickupNotes;
    }
}
