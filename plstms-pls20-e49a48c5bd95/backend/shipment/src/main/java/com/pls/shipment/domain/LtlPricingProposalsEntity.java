package com.pls.shipment.domain;

import java.math.BigDecimal;
import java.util.HashSet;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.pls.core.domain.HasModificationInfo;
import com.pls.core.domain.Identifiable;
import com.pls.core.domain.PlainModificationObject;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.Status;

/**
 * Entity for Ltl pricing proposals.
 * @author Ashwini Neelgund
 */
@Entity
@Table(name = "LTL_PRICING_PROPOSALS")
public class LtlPricingProposalsEntity implements Identifiable<Long>, HasModificationInfo, Cloneable {
    private static final long serialVersionUID = 277463966408386633L;

    public static final String U_INACTIVATE_FOR_LOAD = "com.pls.shipment.domain.LtlPricingProposalsEntity.U_INACTIVATE_FOR_LOAD";
    public static final String Q_GET_BY_QUOTE = "com.pls.shipment.domain.LtlPricingProposalsEntity.Q_GET_BY_QUOTE";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ltl_pric_prop_seq")
    @SequenceGenerator(name = "ltl_pric_prop_seq", sequenceName = "LTL_PRICING_PROPOSALS_SEQ", allocationSize = 1)
    @Column(name = "LTL_PRIC_PROPOSAL_ID")
    private Long ltlPricPropId;

    @Column(name = "LOAD_ID")
    private Long loadId;

    @Column(name = "QUOTE_ID")
    private Long quoteId;

    @Column(name = "LTL_PRICING_PROFILE_ID", nullable = false)
    private Long ltlPricProfId;

    @Column(name = "GUARANTEED_TIME")
    private Long guaranteedTime;

    @Column(name = "HAZMAT_FLAG")
    @Type(type = "yes_no")
    private Boolean hazmat = false;

    @Column(name = "TOTAL_WEIGHT", nullable = false)
    private BigDecimal totalWeight;

    @Column(name = "TOTAL_PIECES")
    private Integer totalPieces;

    @Column(name = "TOTAL_QUANTITY", nullable = false)
    private Integer totalQuantity;

    @Column(name = "TOTAL_REVENUE", nullable = false)
    private BigDecimal totalRevenue;

    @Column(name = "TOTAL_COST", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "TOTAL_BENCHMARK")
    private BigDecimal totalBenchmark;

    @Column(name = "TRANSIT_TIME", nullable = false)
    private Long transitTime;

    @Column(name = "SERVICE_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private LtlServiceType serviceType;

    @Column(name = "NEW_PROD_LIAB_AMT")
    private BigDecimal newProdLiabAmt;

    @Column(name = "USED_PROD_LIAB_AMT")
    private BigDecimal usedProdLiabAmt;

    @Column(name = "PROHIBITED_COMMODITIES")
    private String prohibitedCommodities;

    @Column(name = "PRIC_PROF_NOTE")
    private String pricProfNote;

    @Column(name = "ADDL_GUARAN_INFO")
    private String addlGuaranInfo;

    @Column(name = "GUARAN_BOL_NAME")
    private String guaranBolName;

    @Column(name = "SMC3_TARIFF_NAME")
    private String smc3TariffName;

    @Column(name = "PALLET_PACKAGE_TYPE")
    @Type(type = "yes_no")
    private Boolean palletPackageType = false;

    @Column(name = "PROPOSAL_SELECTED")
    @Type(type = "yes_no")
    private Boolean proposalSelected = false;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "ltlPricProposalEntity")
    private Set<LtlPricPropCostDetailsEntity> pricPropDtlEntity;

    @Column(name = "STATUS", nullable = false)
    @Type(type = "com.pls.core.domain.usertype.StatusUserType")
    private Status status = Status.ACTIVE;
    
    @Column(name = "CARRIER_QUOTE_NUMBER")
    private String carrierQuoteNumber;
    
    @Column(name = "SERVICE_LEVEL_CODE")
    private String serviceLevelCode;
    
    @Column(name = "SERVICE_LEVEL_DESC")
    private String serviceLevelDescription;

    @Embedded
    private final PlainModificationObject modification = new PlainModificationObject();

    @Column(name = "VERSION", nullable = false)
    private long version = 1;

    @Override
    public Long getId() {
        return ltlPricPropId;
    }

    @Override
    public void setId(Long ltlPricPropId) {
        this.ltlPricPropId = ltlPricPropId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public Long getLtlPricProfId() {
        return ltlPricProfId;
    }

    public void setLtlPricProfId(Long ltlPricProfId) {
        this.ltlPricProfId = ltlPricProfId;
    }

    public Long getGuaranteedTime() {
        return guaranteedTime;
    }

    public void setGuaranteedTime(Long guaranteedTime) {
        this.guaranteedTime = guaranteedTime;
    }

    public Boolean getHazmat() {
        return hazmat;
    }

    public void setHazmat(Boolean hazmat) {
        this.hazmat = hazmat;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Integer getTotalPieces() {
        return totalPieces;
    }

    public void setTotalPieces(Integer totalPieces) {
        this.totalPieces = totalPieces;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
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

    public BigDecimal getTotalBenchmark() {
        return totalBenchmark;
    }

    public void setTotalBenchmark(BigDecimal totalBenchmark) {
        this.totalBenchmark = totalBenchmark;
    }

    public Long getTransitTime() {
        return transitTime;
    }

    public void setTransitTime(Long transitTime) {
        this.transitTime = transitTime;
    }

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getNewProdLiabAmt() {
        return newProdLiabAmt;
    }

    public void setNewProdLiabAmt(BigDecimal newProdLiabAmt) {
        this.newProdLiabAmt = newProdLiabAmt;
    }

    public BigDecimal getUsedProdLiabAmt() {
        return usedProdLiabAmt;
    }

    public void setUsedProdLiabAmt(BigDecimal usedProdLiabAmt) {
        this.usedProdLiabAmt = usedProdLiabAmt;
    }

    public String getProhibitedCommodities() {
        return prohibitedCommodities;
    }

    public void setProhibitedCommodities(String prohibitedCommodities) {
        this.prohibitedCommodities = prohibitedCommodities;
    }

    public String getPricProfNote() {
        return pricProfNote;
    }

    public void setPricProfNote(String pricProfNote) {
        this.pricProfNote = pricProfNote;
    }

    public String getAddlGuaranInfo() {
        return addlGuaranInfo;
    }

    public void setAddlGuaranInfo(String addlGuaranInfo) {
        this.addlGuaranInfo = addlGuaranInfo;
    }

    public String getGuaranBolName() {
        return guaranBolName;
    }

    public void setGuaranBolName(String guaranBolName) {
        this.guaranBolName = guaranBolName;
    }

    public String getSmc3TariffName() {
        return smc3TariffName;
    }

    public void setSmc3TariffName(String smc3TariffName) {
        this.smc3TariffName = smc3TariffName;
    }

    public Boolean getPalletPackageType() {
        return palletPackageType;
    }

    public void setPalletPackageType(Boolean palletPackageType) {
        this.palletPackageType = palletPackageType;
    }

    public Boolean getProposalSelected() {
        return proposalSelected;
    }

    public void setProposalSelected(Boolean proposalSelected) {
        this.proposalSelected = proposalSelected;
    }

    public Set<LtlPricPropCostDetailsEntity> getPricPropDtlEntity() {
        return pricPropDtlEntity;
    }

    public void setPricPropDtlEntity(Set<LtlPricPropCostDetailsEntity> pricPropDtlEntity) {
        this.pricPropDtlEntity = pricPropDtlEntity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCarrierQuoteNumber() {
        return carrierQuoteNumber;
    }

    public void setCarrierQuoteNumber(String carrierQuoteNumber) {
        this.carrierQuoteNumber = carrierQuoteNumber;
    }

    public String getServiceLevelCode() {
        return serviceLevelCode;
    }

    public void setServiceLevelCode(String serviceLevelCode) {
        this.serviceLevelCode = serviceLevelCode;
    }

    public String getServiceLevelDescription() {
        return serviceLevelDescription;
    }

    public void setServiceLevelDescription(String serviceLevelDescription) {
        this.serviceLevelDescription = serviceLevelDescription;
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

   @Override
    public LtlPricingProposalsEntity clone() throws CloneNotSupportedException {
        LtlPricingProposalsEntity pricPropEntity = (LtlPricingProposalsEntity) super.clone();
        pricPropEntity.setId(null);
        Set<LtlPricPropCostDetailsEntity> pricPropDtlEntity = new HashSet<LtlPricPropCostDetailsEntity>();
        for (LtlPricPropCostDetailsEntity ltlPricPropCostDetailsEntity : this.getPricPropDtlEntity()) {
            LtlPricPropCostDetailsEntity pricPropCostDtlEntity = ltlPricPropCostDetailsEntity.clone();
            pricPropCostDtlEntity.setLtlPricProposalEntity(pricPropEntity);
            pricPropDtlEntity.add(pricPropCostDtlEntity);
        }
        pricPropEntity.setPricPropDtlEntity(pricPropDtlEntity);
        return pricPropEntity;
    }
}
