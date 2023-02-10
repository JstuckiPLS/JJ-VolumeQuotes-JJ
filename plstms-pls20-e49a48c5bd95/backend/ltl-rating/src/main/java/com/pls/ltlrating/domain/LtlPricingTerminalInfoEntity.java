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
 * Ltl Pricing Termonal Info Entity.
 *
 * @author Artem Arapov
 *
 */
@Entity
@Table(schema = "FLATBED", name = "LTL_PRICING_TERMINAL_INFO")
public class LtlPricingTerminalInfoEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -7298139416771358158L;

    public static final String UPDATE_STATUS_STATEMENT = "com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity.UPDATE_STATUS_STATEMENT";

    public static final String FIND_CSP_ENTITY_BY_COPIED_FROM =
            "com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity.FIND_CSP_ENTITY_BY_COPIED_FROM";

    public static final String INACTIVATE_CSP_BY_DETAIL_ID =
            "com.pls.ltlrating.domain.LtlPricingTerminalInfoEntity.INACTIVATE_CSP_BY_DETAIL_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRICING_TERMINAL_INFO_SEQUENCE")
    @SequenceGenerator(name = "PRICING_TERMINAL_INFO_SEQUENCE", sequenceName = "LTL_PRICING_TERMINAL_INFO_SEQ", allocationSize = 1)
    @Column(name = "LTL_TERMINAL_INFO_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID", nullable = false)
    private Long priceProfileId; // THIS IS PROFILE DETAIL ID!!!

    @Column(name = "TERMINAL")
    private String terminal;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @Column(name = "TRANSIT_TIME")
    private Long transiteTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;

    @Column(name = "VISIBLE")
    @Type(type = "yes_no")
    private Boolean visible = Boolean.TRUE;

    @Column(name = "EMAIL_ADDRESS")
    private String email;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PHONE_ID")
    private PhoneEntity phone;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FAX_ID")
    private PhoneEntity fax;

    @Column(name = "ACCOUNT_NUM")
    private String accountNum;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Column(name = "VERSION", nullable = false)
    @Version
    private Long version = 1L;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    /**
     * Default constructor.
     */
    public LtlPricingTerminalInfoEntity() {
    }

    /**
     * Copy constructor that returns a copy of given clone.
     *
     * @param clone
     *            entity to copy.
     */
    public LtlPricingTerminalInfoEntity(LtlPricingTerminalInfoEntity clone) {
        checkNotNull(clone, "LtlPricingTerminalInfoEntity object is required");

        this.accountNum = clone.getAccountNum();
        this.contactName = clone.getContactName();
        this.email = clone.getEmail();
        this.address = clone.getAddress();
        this.fax = clone.getFax();
        this.phone = clone.getPhone();
        this.status = Status.ACTIVE;
        this.terminal = clone.getTerminal();
        this.transiteTime = clone.getTransiteTime();
        this.visible = clone.getVisible();
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

    public Long getPriceProfileId() {
        return priceProfileId;
    }

    public void setPriceProfileId(Long priceProfileId) {
        this.priceProfileId = priceProfileId;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Long getTransiteTime() {
        return transiteTime;
    }

    public void setTransiteTime(Long transiteTime) {
        this.transiteTime = transiteTime;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

}
