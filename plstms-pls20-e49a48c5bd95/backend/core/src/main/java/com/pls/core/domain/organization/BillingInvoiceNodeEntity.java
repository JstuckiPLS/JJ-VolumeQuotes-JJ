package com.pls.core.domain.organization;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.AddressEntity;

/**
 * Billing Invoice Node Entity.
 * The entity is in fact an implementation of Many-To-Many association.
 * Derived into separate entity to guarantee generating ID value.
 *
 * @author Mikhail Boldinov
 */
@Entity
@Table(name = "BILLING_INVOICE_NODE")
public class BillingInvoiceNodeEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -7426587368975705541L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_invoice_node_sequence")
    @SequenceGenerator(name = "billing_invoice_node_sequence", sequenceName = "BIE_SEQ", allocationSize = 1)
    @Column(name = "BILLING_NODE_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "BILL_TO_ID")
    private BillToEntity billTo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;

    @Column(name = "NETWORK_ID", insertable = true, updatable = false)
    private Long networkId;

    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "CUSTOMER_NUMBER")
    private String customerNumber;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PHONE_ID")
    private PhoneEntity phone;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FAX_ID")
    private PhoneEntity fax;

    @Column(name = "CONTACT_EMAIL")
    private String email;

    @Column(name = "VERSION")
    private long version = 1;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the field billTo.
     *
     * @return the value for the field billTo.
     */
    public BillToEntity getBillTo() {
        return billTo;
    }

    /**
     * Setter for the field billTo.
     *
     * @param billTo the value to set for the field.
     */
    public void setBillTo(BillToEntity billTo) {
        this.billTo = billTo;
    }

    /**
     * Getter for the field address.
     *
     * @return the value for the field address.
     */
    public AddressEntity getAddress() {
        return address;
    }

    /**
     * Setter for the field address.
     *
     * @param address the value to set for the field.
     */
    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    /**
     * Getter for the field customerId.
     *
     * @return the value for the field customerId.
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Setter for the field customerId.
     *
     * @param customerId the value to set for the field.
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Getter for the field customerNumber.
     *
     * @return the value for the field customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Setter for the field customerNumber.
     *
     * @param customerNumber the value to set for the field.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

}
