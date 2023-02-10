package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;

/**
 * Saved Quote Cost details entity.
 *
 * @author Mikhail Boldinov, 26/03/13
 */
@Entity
@Table(name = "SV_QT_COST_DETAILS")
public class SavedQuoteCostDetailsEntity implements Identifiable<Long>, HasModificationInfo {

    private static final long serialVersionUID = -1038013493484912092L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saved_quote_cost_details_sequence")
    @SequenceGenerator(name = "saved_quote_cost_details_sequence", sequenceName = "SAVED_QUOTE_COST_DET_SEQ", allocationSize = 1)
    @Column(name = "QUOTE_COST_DETAIL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUOTE_ID", nullable = false)
    private SavedQuoteEntity quote;

    @OneToMany(mappedBy = "costDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SavedQuoteCostDetailsItemEntity> costDetailsItems;

    @Column(name = "TOTAL_REVENUE")
    private BigDecimal totalRevenue;

    @Column(name = "TOTAL_COSTS")
    private BigDecimal totalCost;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.INACTIVE;

    @Column(name = "TARIFF_NAME")
    private String tariffName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EST_TRANSIT_DATE")
    private Date estTransitDate;

    /**
     * Estimate transit time in minutes. Because we need to save values like 2h 20m.
     */
    @Column(name = "TRAVEL_TIME")
    private Long estimatedTransitTime;

    @Column(name = "NEW_PROD_LIAB_AMT")
    private BigDecimal newLiability;

    @Column(name = "USED_PROD_LIAB_AMT")
    private BigDecimal usedLiability;

    /**
     * Service type (DIRECT/INDIRECT)
     */
    @Column(name = "SERVICE_TYPE")
    @Enumerated(EnumType.STRING)
    private LtlServiceType serviceType;

    @Column(name = "PROHIBITED_COMMODITIES")
    private String prohibitedCommodities;

    @Column(name = "GUARAN_TIME")
    private Long guaranteedBy;

    @Column(name = "GUARAN_BOL_NAME")
    private String guaranteedNameForBOL;

    @Column(name = "PRIC_PROF_DETAIL_ID")
    private Long pricingProfileDetailId;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

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

    public SavedQuoteEntity getQuote() {
        return quote;
    }

    public void setQuote(SavedQuoteEntity quote) {
        this.quote = quote;
    }

    public Set<SavedQuoteCostDetailsItemEntity> getCostDetailsItems() {
        return costDetailsItems;
    }

    public void setCostDetailsItems(Set<SavedQuoteCostDetailsItemEntity> costDetailsItems) {
        this.costDetailsItems = costDetailsItems;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public Date getEstTransitDate() {
        return estTransitDate;
    }

    public void setEstTransitDate(Date estTransitDate) {
        this.estTransitDate = estTransitDate;
    }

    /**
     * Get estimated transit time in minutes.
     * 
     * @return estimated transit time in minutes.
     */
    public Long getEstimatedTransitTime() {
        return estimatedTransitTime;
    }

    /**
     * Set estimated transit time in minutes.
     * 
     * @param estimatedTransitTime
     *            estimated transit time in minutes.
     */
    public void setEstimatedTransitTime(Long estimatedTransitTime) {
        this.estimatedTransitTime = estimatedTransitTime;
    }

    public BigDecimal getNewLiability() {
        return newLiability;
    }

    public void setNewLiability(BigDecimal newLiability) {
        this.newLiability = newLiability;
    }

    public BigDecimal getUsedLiability() {
        return usedLiability;
    }

    public void setUsedLiability(BigDecimal usedLiability) {
        this.usedLiability = usedLiability;
    }

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getProhibitedCommodities() {
        return prohibitedCommodities;
    }

    public void setProhibitedCommodities(String prohibitedCommodities) {
        this.prohibitedCommodities = prohibitedCommodities;
    }

    public Long getGuaranteedBy() {
        return guaranteedBy;
    }

    public void setGuaranteedBy(Long guaranteedBy) {
        this.guaranteedBy = guaranteedBy;
    }

    public String getGuaranteedNameForBOL() {
        return guaranteedNameForBOL;
    }

    public void setGuaranteedNameForBOL(String guaranteedNameForBOL) {
        this.guaranteedNameForBOL = guaranteedNameForBOL;
    }

    public Long getPricingProfileDetailId() {
        return pricingProfileDetailId;
    }

    public void setPricingProfileDetailId(Long pricingProfileDetailId) {
        this.pricingProfileDetailId = pricingProfileDetailId;
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
