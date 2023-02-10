package com.pls.ltlrating.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * LtlFuelSurchargeEntity.
 *
 * @author Stas Norochevskiy
 *
 */
@Entity
@Table(name = "LTL_FUEL_SURCHARGE")
public class LtlFuelSurchargeEntity  implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 6795701831094096727L;

    public static final String ACTIVE_FUEL_SURCHARGE = "com.pls.domain.pricing.profile.ACTIVE_FUEL_SURCHARGE";

    public static final String GET_BY_FUEL_CHARGE =
            "com.pls.domain.pricing.profile.LtlFuelSurchargeEntity.GET_BY_FUEL_CHARGE";

    public static final String INACTIVATE_BY_PROFILE_STATEMENT =
            "com.pls.ltlrating.domain.LtlFuelSurchargeEntity.INACTIVATE_BY_PROFILE_STATEMENT";

    public static final String FIND_CSP_FUEL_SURCHARGE_COPIED_FROM =
            "com.pls.ltlrating.domain.LtlFuelSurchargeEntity.FIND_CSP_FUEL_SURCHARGE_COPIED_FROM";

    public static final String UPDATE_STATUS_STATEMENT = "com.pls.ltlrating.domain.LtlFuelSurchargeEntity.UPDATE_STATUS_STATEMENT";

    public static final String UPDATE_CSP_STATUS_STATEMENT =
            "com.pls.ltlrating.domain.LtlFuelSurchargeEntity.UPDATE_CSP_STATUS_STATEMENT";

    public static final String INACTIVATE_CSP_BY_DETAIL_ID =
            "com.pls.ltlrating.domain.LtlFuelSurchargeEntity.INACTIVATE_CSP_BY_DETAIL_ID";

    public static final String Q_FIND_ALL_BY_PROFILE_DETAIL_ID =
            "com.pls.ltlrating.domain.LtlFuelSurchargeEntity.Q_FIND_ALL_BY_PROFILE_DETAIL_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_fuel_surcharge")
    @SequenceGenerator(name = "ltl_fuel_surcharge",
                       sequenceName = "LTL_FUEL_SURCHARGE_SEQ", allocationSize = 1)
    @Column(name = "LTL_FUEL_SURCHARGE_ID")
    private Long id;

    @Column(name = "LTL_PRIC_PROF_DETAIL_ID")
    private Long ltlPricingProfileId;

    @Column(name = "MIN_RATE")
    private BigDecimal minRate;

    @Column(name = "MAX_RATE")
    private BigDecimal maxRate;

    @Column(name = "SURCHARGE")
    private BigDecimal surcharge;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "COPIED_FROM")
    private Long copiedFrom;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    /**
     * Default constructor.
     */
    public LtlFuelSurchargeEntity() {
    }

    /**
     * Copying constructor.
     *
     * @param source - source entity to be cloned.
     */
    public LtlFuelSurchargeEntity(LtlFuelSurchargeEntity source) {
        this.ltlPricingProfileId = source.getLtlPricingProfileId();
        this.maxRate = source.getMaxRate();
        this.minRate = source.getMinRate();
        this.status = source.getStatus();
        this.surcharge = source.getSurcharge();
        this.copiedFrom = source.getId();
    }

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

    public BigDecimal getMinRate() {
        return minRate;
    }

    public void setMinRate(BigDecimal minRate) {
        this.minRate = minRate;
    }

    public BigDecimal getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(BigDecimal maxRate) {
        this.maxRate = maxRate;
    }

    public BigDecimal getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(BigDecimal surcharge) {
        this.surcharge = surcharge;
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


    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(this.ltlPricingProfileId).
                append(this.minRate).
                append(this.maxRate).
                append(this.surcharge).
                append(this.status).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LtlFuelSurchargeEntity other = (LtlFuelSurchargeEntity) obj;
        return new EqualsBuilder().
                append(this.ltlPricingProfileId, other.ltlPricingProfileId).
                append(this.minRate, other.minRate).
                append(this.maxRate, other.maxRate).
                append(this.surcharge, other.surcharge).
                append(this.status, other.status).
                isEquals();
    }

}
