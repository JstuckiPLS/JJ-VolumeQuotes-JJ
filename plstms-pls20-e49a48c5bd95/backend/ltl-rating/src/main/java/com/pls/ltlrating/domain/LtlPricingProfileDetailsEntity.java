package com.pls.ltlrating.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.organization.OrganizationAPIDetailsEntity;
import com.pls.ltlrating.domain.enums.PricingDetailType;

/**
 * Pricing profile detail.
 *
 * @author Sergey Kirichenko
 */
@Entity
@Table(name = "LTL_PRICING_PROFILE_DETAILS")
public class LtlPricingProfileDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -8533979359265800766L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pric_prof_detail_sequence")
    @SequenceGenerator(name = "ltl_pric_prof_detail_sequence", sequenceName = "LTL_PRIC_PROF_DETAIL_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRIC_PROF_DETAIL_ID")
    private Long id;

    @Column(name = "LTL_PRICING_PROFILE_ID", nullable = false, insertable = false, updatable = false)
    private Long ltlPricingProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "PRICING_DETAIL_TYPE")
    private PricingDetailType pricingDetailType;

    @Column(name = "LTL_RATING_CARRIER_TYPE")
    private String carrierType; //TODO: Should be removed after confirmation with Wendy

    @Column(name = "MILEAGE_TYPE")
    private String mileageType;

    @Column(name = "MILEAGE_VERSION")
    private String mileageVersion;

    @Column(name = "SMC3_SCAC")
    private String smc3Carrier;

    @Column(name = "SMC3_TARIFF")
    private String smc3Tariff;

    @Column(name = "MSCALE")
    private String mScale;

    @Column(name = "USE_BLANKET")
    private String useBlanket;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ORGANIZATION_API_DETAIL_ID")
    private OrganizationAPIDetailsEntity carrierAPIDetails;

    @Embedded
    private PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    private Integer version = 1;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public PricingDetailType getPricingDetailType() {
        return pricingDetailType;
    }

    public void setPricingDetailType(PricingDetailType pricingDetailType) {
        this.pricingDetailType = pricingDetailType;
    }

    public String getCarrierType() {
        return carrierType;
    }

    public void setCarrierType(String carrierType) {
        this.carrierType = carrierType;
    }

    public String getSmc3Carrier() {
        return smc3Carrier;
    }

    public void setSmc3Carrier(String smc3Carrier) {
        this.smc3Carrier = smc3Carrier;
    }

    public String getSmc3Tariff() {
        return smc3Tariff;
    }

    public void setSmc3Tariff(String smc3Tariff) {
        this.smc3Tariff = smc3Tariff;
    }

    public OrganizationAPIDetailsEntity getCarrierAPIDetails() {
        return carrierAPIDetails;
    }

    public void setCarrierAPIDetails(OrganizationAPIDetailsEntity carrierAPIDetails) {
        this.carrierAPIDetails = carrierAPIDetails;
    }

    public String getMScale() {
        return mScale;
    }

    public void setMScale(String mScale) {
        this.mScale = mScale;
    }

    public Long getLtlPricingProfileId() {
        return ltlPricingProfileId;
    }

    public void setLtlPricingProfileId(Long ltlPricingProfileId) {
        this.ltlPricingProfileId = ltlPricingProfileId;
    }

    public String getMileageType() {
        return mileageType;
    }

    public void setMileageType(String mileageType) {
        this.mileageType = mileageType;
    }

    public String getMileageVersion() {
        return mileageVersion;
    }

    public void setMileageVersion(String mileageVersion) {
        this.mileageVersion = mileageVersion;
    }

    public String getUseBlanket() {
        return useBlanket;
    }

    public void setUseBlanket(String useBlanket) {
        this.useBlanket = useBlanket;
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
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("ltlPricingProfileId", getLtlPricingProfileId())
                .append("pricingDetailType", getPricingDetailType())
                .append("carrierType", getCarrierType())
                .append("mileageType", getMileageType())
                .append("mileageVersion", getMileageVersion())
                .append("smc3Carrier", getSmc3Carrier())
                .append("smc3Tariff", getSmc3Tariff())
                .append("mScale", getMScale())
                .append("carrierAPIDetails", getCarrierAPIDetails())
                .append("modification", getModification());

        return builder.toString();
    }
}
