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

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.SimpleOrganizationEntity;
import com.pls.core.shared.Status;

/**
 * Applicable pricing customer.
 *
 * @author Gleb Zgonikov
 */
@Entity
@Table(name = "LTL_PRICING_APPL_CUST")
public class LtlPricingApplicableCustomersEntity implements Identifiable<Long> {

    private static final long serialVersionUID = 2172961960821461736L;

    public static final String Q_FIND_CUSTOMERS_BY_SMC3_TARIFF_NAME
            = "com.pls.ltlrating.domain.LtlPricingApplicableCustomersEntity.Q_FIND_CUSTOMERS_BY_SMC3_TARIFF_NAME";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pricing_appl_cust_seq")
    @SequenceGenerator(name = "ltl_pricing_appl_cust_seq", sequenceName = "LTL_PRICING_APPL_CUST_SEQ", allocationSize = 1)
    @Column(name = "LTL_APPL_CUST_ID", nullable = false)
    private Long id;

    @Column(name = "LTL_PRICING_PROFILE_ID", nullable = false, insertable = false, updatable = false)
    private Long ltlPricingProfileId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHIPPER_ORG_ID", nullable = false)
    private SimpleOrganizationEntity customer;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    private long version = 1;

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

    public SimpleOrganizationEntity getCustomer() {
        return customer;
    }

    public void setCustomer(SimpleOrganizationEntity customer) {
        this.customer = customer;
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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(this.getLtlPricingProfileId()).
                append(this.customer.getId()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LtlPricingApplicableCustomersEntity other = (LtlPricingApplicableCustomersEntity) obj;
        return new EqualsBuilder().append(this.getLtlPricingProfileId(), other.getLtlPricingProfileId()).
                append(this.customer.getId(), other.customer.getId()).isEquals();
    }
}

