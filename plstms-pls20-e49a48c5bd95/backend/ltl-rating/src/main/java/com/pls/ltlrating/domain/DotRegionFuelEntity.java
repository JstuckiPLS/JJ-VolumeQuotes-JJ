package com.pls.ltlrating.domain;

import java.math.BigDecimal;
import java.util.Date;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * DotRegionFuelEntity.
 *
 * @author Stas Norochevskiy
 *
 */
@Entity
@Table(name = "DOT_REGION_FUEL")
public class DotRegionFuelEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 1L;

    public static final String GET_BY_DATE =
            "com.pls.ltlrating.domain.DotRegionFuelEntity.GET_BY_DATE";

    public static final String GET_BY_DATE_AND_REGION_ID =
            "com.pls.ltlrating.domain.DotRegionFuelEntity.GET_BY_DATE_AND_REGION_ID";

    public static final String GET_ACTIVE_REGIONS_QUERY = "com.pls.ltlrating.domain.DotRegionFuelEntity.GET_ACTIVE_REGIONS_QUERY";

    public static final String EXPIRATE_STATEMENT = "com.pls.ltlrating.domain.DotRegionFuelEntity.EXPIRATE_STATEMENT";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dot_region_fuel_seq")
    @SequenceGenerator(name = "dot_region_fuel_seq",
                       sequenceName = "DOT_REGION_FUEL_SEQ", allocationSize = 1)
    @Column(name = "DOT_REGION_FUEL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DOT_REGION_ID")
    private DotRegionEntity dotRegion;

    @Column(name = "DOT_REGION_ID", updatable = false, insertable = false)
    private Long dotRegionId;

    @Column(name = "FUEL_CHARGE")
    private BigDecimal fuelCharge;

    @Column(name = "EFF_DATE", nullable = false)
    private Date effectiveDate;

    @Column(name = "EXP_DATE")
    private Date expirationDate;

    @Column(name = "STATUS")
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Version
    @Column(name = "VERSION")
    private Long version = 1L;

    public Long getVersion() {
        return version;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public DotRegionEntity getDotRegion() {
        return dotRegion;
    }

    /**
     * Set dotRegion and dotRegionId.
     *
     * @param dotRegion new DotRegionEntity object
     */
    public void setDotRegion(DotRegionEntity dotRegion) {
        this.dotRegion = dotRegion;
        this.dotRegionId = (dotRegion != null) ? dotRegion.getId() : null;
    }

    public BigDecimal getFuelCharge() {
        return fuelCharge;
    }

    public void setFuelCharge(BigDecimal fuelCharge) {
        this.fuelCharge = fuelCharge;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public PlainModificationObject getModification() {
        return modification;
    }

    public Long getDotRegionId() {
        return dotRegionId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(this.dotRegionId).
                append(this.fuelCharge).
                append(this.expirationDate).
                append(this.effectiveDate).
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
        final DotRegionFuelEntity other = (DotRegionFuelEntity) obj;
        return new EqualsBuilder().
                append(this.dotRegionId, other.dotRegionId).
                append(this.fuelCharge, other.fuelCharge).
                append(this.expirationDate, other.expirationDate).
                append(this.effectiveDate, other.effectiveDate).
                append(this.status, other.status).
                isEquals();
    }

}
