package com.pls.ltlrating.domain;

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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.shared.Status;

/**
 * When a carrier profile rate breakdown and also the rate information need to be hidden from Quoting
 * for specific customer, the information is saved in this entity.
 *
 * @author Hima Bindu Challa
 */
@Entity
@Table(name = "LTL_CUST_HIDE_PRIC_DETAILS")
public class LtlCustHidePricDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = 2396674345125354508L;

    public static final String ARCHIVE_HIDE_CUST_PRIC = "com.pls.ltlrating.domain.LtlCustHidePricDetailsEntity.ARCHIVE_HIDE_CUST_PRIC";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_cust_hide_pric_det_seq")
    @SequenceGenerator(name = "ltl_cust_hide_pric_det_seq", sequenceName = "LTL_CUST_HIDE_PRIC_DET_SEQ", allocationSize = 1)
    @Column(name = "CUST_HIDE_PRIC_DETAIL_ID", nullable = false)
    private Long id;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;

    @Column(name = "LTL_PRICING_PROFILE_ID")
    private Long ltlPricingProfileId;

    @Column(name = "SHIPPER_ORG_ID")
    private Long shipperOrgId;

    @Column(name = "VERSION", nullable = false)
    private Integer version = 1;

    @Embedded
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

    public Long getLtlPricingProfileId() {
        return ltlPricingProfileId;
    }

    public void setLtlPricingProfileId(Long ltlPricingProfileId) {
        this.ltlPricingProfileId = ltlPricingProfileId;
    }

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public PlainModificationObject getModification() {
        return modification;
    }

    public void setModification(PlainModificationObject modification) {
        this.modification = modification;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getShipperOrgId())
                .append(this.getLtlPricingProfileId()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LtlCustHidePricDetailsEntity other = (LtlCustHidePricDetailsEntity) obj;
        return new EqualsBuilder().append(this.getShipperOrgId(), other.getShipperOrgId())
                .append(this.getLtlPricingProfileId(), other.getLtlPricingProfileId())
                .isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("shipperOrgId", getShipperOrgId())
                .append("ltlPricingProfileId", getLtlPricingProfileId())
                .append("status", getStatus())
                .append("modification", getModification());

        return builder.toString();
    }
}
