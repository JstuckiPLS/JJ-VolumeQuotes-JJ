package com.pls.ltlrating.domain;

import static com.google.common.base.Preconditions.checkNotNull;

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
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PhoneEntity;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.address.AddressEntity;
import com.pls.core.shared.Status;
/**
 * Third Party information.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(schema = "FLATBED", name = "LTL_PRICING_THIRD_PARTY_INFO")
public class LtlPricingThirdPartyInfoEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 6448667595561678689L;

    public static final String UPDATE_STATUS_STATEMENT = "com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity.UPDATE_STATUS_STATEMENT";

    public static final String FIND_CSP_ENTITY_BY_COPIED_FROM =
            "com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity.FIND_CSP_ENTITY_BY_COPIED_FROM";

    public static final String FIND_THIRD_PARTY_BY_PROFILE_ID =
            "com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity.FIND_THIRD_PARTY_BY_PROFILE_ID";

    public static final String INACTIVATE_CSP_BY_DETAIL_ID =
            "com.pls.ltlrating.domain.LtlPricingThirdPartyInfoEntity.INACTIVATE_CSP_BY_DETAIL_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRICING_THIRD_PARTY_INFO_SEQUENCE")
    @SequenceGenerator(name = "PRICING_THIRD_PARTY_INFO_SEQUENCE", sequenceName = "PRICING_THIRD_PARTY_INFO_SEQ", allocationSize = 1)
    @Column(name = "LTL_THIRD_PARTY_INFO_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID", nullable = false)
    private Long pricProfDetailId;

    @Column(name = "COMPANY")
    private String company;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PHONE_ID")
    private PhoneEntity phone;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FAX_ID")
    private PhoneEntity fax;

    @Column(name = "EMAIL_ADDRESS")
    private String email;

    @Column(name = "ACCOUNT_NUM")
    private String accountNum;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    @Version
    private Long version = 1L;

    /**
     * Default constructor.
     */
    public LtlPricingThirdPartyInfoEntity() {
    }

    /**
     * Copy constructor that returns a copy of given clone.
     *
     * @param clone
     *            entity to copy.
     */
    public LtlPricingThirdPartyInfoEntity(LtlPricingThirdPartyInfoEntity clone) {
        checkNotNull(clone, "LtlPricingThirdPartyInfoEntity object is required");

        this.accountNum = clone.getAccountNum();
        this.company = clone.getCompany();
        this.contactName = clone.getContactName();
        this.address = clone.getAddress();
        this.email = clone.getEmail();
        this.fax = clone.getFax();
        this.phone = clone.getPhone();
        this.status = Status.ACTIVE;
        this.copiedFrom = clone.getId();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPricProfDetailId() {
        return pricProfDetailId;
    }

    public void setPricProfDetailId(Long pricProfDetailId) {
        this.pricProfDetailId = pricProfDetailId;
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

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getCopiedFrom() {
        return copiedFrom;
    }

    public void setCopiedFrom(Long copiedFrom) {
        this.copiedFrom = copiedFrom;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
