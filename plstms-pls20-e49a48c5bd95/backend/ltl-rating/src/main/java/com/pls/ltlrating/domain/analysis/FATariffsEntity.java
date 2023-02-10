package com.pls.ltlrating.domain.analysis;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import com.pls.core.domain.Identifiable;
import com.pls.core.domain.organization.CustomerEntity;
import com.pls.ltlrating.domain.LtlPricingProfileEntity;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Tariff for Freight Analysis.
 * 
 * @author Svetlana Kulish
 */
@Entity
@Table(name = "FA_TARIFFS")
public class FATariffsEntity implements Identifiable<Long> {
    private static final long serialVersionUID = -4343578286791529937L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FA_FINANCIAL_ANALYSIS_SEQUENCE")
    @SequenceGenerator(name = "FA_FINANCIAL_ANALYSIS_SEQUENCE", sequenceName = "FA_FINANCIAL_ANALYSIS_SEQ", allocationSize = 1)
    @Column(name = "TARIFF_ID")
    private Long id;

    @Column(name = "TARIFF_NAME")
    private String tariffName;

    @Column(name = "TARIFF_TYPE")
    @Enumerated(EnumType.STRING)
    private PricingType tariffType;

    @Column(name = "LTL_PRICING_PROFILE_ID")
    private Long pricingProfileId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "LTL_PRICING_PROFILE_ID", insertable = false, updatable = false)
    private LtlPricingProfileEntity pricingProfile;

    @Column(name = "ORG_ID")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORG_ID", insertable = false, updatable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANALYSIS_ID", nullable = false)
    private FAFinancialAnalysisEntity analysis;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public PricingType getTariffType() {
        return tariffType;
    }

    public void setTariffType(PricingType tariffType) {
        this.tariffType = tariffType;
    }


    public Long getPricingProfileId() {
        return pricingProfileId;
    }

    public void setPricingProfileId(Long pricingProfileId) {
        this.pricingProfileId = pricingProfileId;
    }

    public LtlPricingProfileEntity getPricingProfile() {
        return pricingProfile;
    }

    public void setPricingProfile(LtlPricingProfileEntity pricingProfile) {
        this.pricingProfile = pricingProfile;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public FAFinancialAnalysisEntity getAnalysis() {
        return analysis;
    }

    public void setAnalysis(FAFinancialAnalysisEntity analysis) {
        this.analysis = analysis;
    }
}
