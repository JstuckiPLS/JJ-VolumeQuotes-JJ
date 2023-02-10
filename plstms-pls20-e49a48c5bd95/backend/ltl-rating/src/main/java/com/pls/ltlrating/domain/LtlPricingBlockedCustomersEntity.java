package com.pls.ltlrating.domain;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.shared.Status;

/**
 * Blocked pricing customer.
 *
 * @author Hima Bindu Challa
 */
@Entity
@Table(name = "LTL_PRICING_BLOCKED_CUST")
public class LtlPricingBlockedCustomersEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 2972520266943940208L;

    public static final String ARCHIVE_BLOCKED_CUST = "com.pls.ltlrating.domain.LtlPricingBlockedCustomersEntity.ARCHIVE_BLOCKED_CUST";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pricing_blocked_customer_seq")
    @SequenceGenerator(name = "ltl_pricing_blocked_customer_seq", sequenceName = "LTL_PRICING_BLOCKED_CUST_SEQ", allocationSize = 1)
    @Column(name = "LTL_BLOCKED_CUST_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHIPPER_ORG_ID", insertable = false, updatable = false)
    private SimpleOrganizationEntity blockedCustomer;

    @Column(name = "SHIPPER_ORG_ID", nullable = false, insertable = true, updatable = true)
    private Long shipperOrgId;

    @Column(name = "LTL_PRICING_PROFILE_ID", nullable = false)
    private Long ltlPricingProfileId;

    @Column(name = "VERSION", nullable = false)
    private Integer version = 1;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getLtlPricingProfileId() {
        return ltlPricingProfileId;
    }

    public void setLtlPricingProfileId(Long ltlPricingProfileId) {
        this.ltlPricingProfileId = ltlPricingProfileId;
    }

    public SimpleOrganizationEntity getBlockedCustomer() {
        return blockedCustomer;
    }

    public void setBlockedCustomer(SimpleOrganizationEntity blockedCustomer) {
        this.blockedCustomer = blockedCustomer;
    }

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getLtlPricingProfileId())
                .append(this.getBlockedCustomer() != null ? this.getBlockedCustomer().getId() : 0).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LtlPricingBlockedCustomersEntity other = (LtlPricingBlockedCustomersEntity) obj;
        return new EqualsBuilder().append(this.getLtlPricingProfileId(), other.getLtlPricingProfileId())
                .append(this.getBlockedCustomer().getId(), other.getBlockedCustomer().getId()).isEquals();
    }
}
