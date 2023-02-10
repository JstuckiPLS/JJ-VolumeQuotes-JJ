package com.pls.core.domain;

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

import com.pls.core.domain.address.AddressEntity;

/**
 * Freight bill pay to entity.
 * 
 * @author Aleksandr Leshchenko
 */
@Entity
@Table(name = "FREIGHT_BILL_PAY_TO")
public class FreightBillPayToEntity implements Identifiable<Long>, HasModificationInfo, HasVersion {
    private static final long serialVersionUID = 3689222265354168249L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FREIGHT_BILL_PAY_TO_SEQ")
    @SequenceGenerator(name = "FREIGHT_BILL_PAY_TO_SEQ", sequenceName = "FREIGHT_BILL_PAY_TO_SEQ", allocationSize = 1)
    @Column(name = "FRT_BILL_PAY_TO_ID")
    private Long id;

    @Column(name = "COMPANY")
    private String company;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @Column(name = "ACCOUNT_NUM")
    private String accountNum;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PHONE_ID")
    private PhoneEntity phone;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FAX_ID")
    private PhoneEntity fax;

    @Column(name = "EMAIL_ADDRESS")
    private String email;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
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

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject modification) {
        this.modification = modification;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
