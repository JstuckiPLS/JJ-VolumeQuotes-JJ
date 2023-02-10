package com.pls.core.domain.organization;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.LtlBlkServCarrierType;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;
import com.pls.core.shared.StatusYesNo;

/**
 * This class contains pricing details for LTL Shippers - Is pricing active, Gainshare details etc.
 *
 * @author Hima Bindu Challa
 * @author Ashwini Neelgund
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "ORGANIZATION_PRICING")
public class OrganizationPricingEntity implements Identifiable<Long>, HasModificationInfo {

    public static final String Q_GET_MIN_ACCEPT_MARGIN = "com.pls.core.domain.organization.OrganizationPricingEntity.Q_GET_MIN_ACCEPT_MARGIN";

    private static final long serialVersionUID = 2718077523206419893L;

    @Id
    @Column(name = "ORG_ID")
    private Long id;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "MIN_ACCEPT_MARGIN")
    private BigDecimal minAcceptMargin;

    @Column(name = "DEFAULT_MARGIN")
    private BigDecimal defaultMargin;

    @Column(name = "DEFAULT_MIN_MARGIN_AMT", nullable = false)
    private BigDecimal defaultMinMarginAmt;

    @Column(name = "INCLUDE_BM_ACC")
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo includeBenchmarkAcc = StatusYesNo.NO;

    @Column(name = "GAINSHARE", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.YesNoUserType")
    private StatusYesNo gainshare = StatusYesNo.NO;

    @Column(name = "GS_PLS_PCT")
    private BigDecimal gsPlsPct = BigDecimal.ZERO;

    @Column(name = "GS_CUST_PCT")
    private BigDecimal gsCustPct = BigDecimal.ZERO;

    @Version
    @Column(name = "VERSION")
    private Integer version = 1;

    @Column(name = "BLK_SERV_CARRIER_TYPE", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private LtlBlkServCarrierType blkServCarrierType;

    @Column(name = "BLOCK_SERVICE_TYPE", nullable = true)
    @Enumerated(EnumType.STRING)
    private LtlServiceType blkServiceType;

    private PlainModificationObject modification = new PlainModificationObject();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getMinAcceptMargin() {
        return minAcceptMargin;
    }

    public void setMinAcceptMargin(BigDecimal minAcceptMargin) {
        this.minAcceptMargin = minAcceptMargin;
    }

    public BigDecimal getDefaultMargin() {
        return defaultMargin;
    }

    public void setDefaultMargin(BigDecimal defaultMargin) {
        this.defaultMargin = defaultMargin;
    }

    public BigDecimal getDefaultMinMarginAmt() {
        return defaultMinMarginAmt;
    }

    public void setDefaultMinMarginAmt(BigDecimal defaultMinMarginAmt) {
        this.defaultMinMarginAmt = defaultMinMarginAmt;
    }


    public StatusYesNo getIncludeBenchmarkAcc() {
        return includeBenchmarkAcc;
    }

    public void setIncludeBenchmarkAcc(StatusYesNo includeBenchmarkAcc) {
        this.includeBenchmarkAcc = includeBenchmarkAcc;
    }

    public StatusYesNo getGainshare() {
        return gainshare;
    }

    public void setGainshare(StatusYesNo gainshare) {
        this.gainshare = gainshare;
    }

    public BigDecimal getGsPlsPct() {
        return gsPlsPct;
    }

    public void setGsPlsPct(BigDecimal gsPlsPct) {
        this.gsPlsPct = gsPlsPct;
    }

    public BigDecimal getGsCustPct() {
        return gsCustPct;
    }

    public void setGsCustPct(BigDecimal gsCustPct) {
        this.gsCustPct = gsCustPct;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject modification) {
        this.modification = modification;
    }

    public LtlBlkServCarrierType getBlkServCarrierType() {
        return blkServCarrierType;
    }

    public void setBlkServCarrierType(LtlBlkServCarrierType carrierType) {
        this.blkServCarrierType = carrierType;
    }

    public LtlServiceType getBlkServiceType() {
        return blkServiceType;
    }

    public void setBlkServiceType(LtlServiceType blkServiceType) {
        this.blkServiceType = blkServiceType;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();

        builder.append(getId())
                .append(getStatus())
                .append(getMinAcceptMargin())
                .append(getGainshare())
                .append(getGsPlsPct())
                .append(getGsCustPct());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof OrganizationPricingEntity) {
            if (obj == this) {
                result = true;
            } else {
                OrganizationPricingEntity other = (OrganizationPricingEntity) obj;
                EqualsBuilder builder = new EqualsBuilder();
                builder.append(getId(), other.getId())
                        .append(getStatus(), other.getStatus())
                        .append(getMinAcceptMargin(), other.getMinAcceptMargin())
                        .append(getGainshare(), other.getGainshare())
                        .append(getGsPlsPct(), other.getGsPlsPct())
                        .append(getGsCustPct(), other.getGsCustPct());

                builder.append(getModification(), other.getModification());
                result = builder.isEquals();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("Org ID : ", getId())
                .append("Status : ", getStatus())
                .append("Min Acceptable Margin : ", getMinAcceptMargin())
                .append("Is Gainshare Account : ", getGainshare())
                .append("Gainshare PLS Percent : ", getGsPlsPct())
                .append("Gainshare Customer Percent : ", getGsCustPct())
                .append("modification", getModification());

        return builder.toString();
    }
}
