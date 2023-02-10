package com.pls.ltlrating.domain.bo.proposal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pls.core.domain.bo.proposal.CarrierDTO;
import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;

/**
 * Contains carrier specific proposition for given quote.
 *
 * @author Gleb Zgonikov
 */
public class ShipmentProposalBO implements Serializable {
    private static final long serialVersionUID = 5645793336211334656L;

    private String guid;

    private Long id;

    private Long pricingProfileId;

    private Integer version;

    private Date estimatedTransitDate;

    /**
     * Estimated transit time in minutes.
     */
    private Long estimatedTransitTime;

    private LtlServiceType serviceType;
    
    /** In case of LTLLC quote, the service level (e.g. ACCELERATED, EXPEDITE, CAPLOAD), which needs to be used when dispatching */
    private String serviceLevelCode; 
    private String serviceLevelDescription;

    private CarrierDTO carrier;

    private BigDecimal newLiability;

    private BigDecimal usedLiability;

    private String prohibited;

    private String guaranteedNameForBOL;

    private String addlGuaranInfo;

    private Integer mileage;

    private boolean includeBenchmarkAccessorials;

    private boolean hideCostDetails;

    private boolean hideTerminalDetails;

    private BigDecimal carrierInitialCost;

    private BigDecimal carrierDiscount;

    private BigDecimal shipperInitialCost;

    private BigDecimal shipperDiscount;

    private String currencyCode;

    private String tariffName;

    private List<CostDetailItemBO> costDetailItems;

    private StatusYesNo revenueOverride;

    private StatusYesNo costOverride;

    private BigDecimal defaultMarginAmt;

    private BigDecimal minLinehaulMarginAmt;

    private BigDecimal appliedLinehaulMarginAmt;

    private BigDecimal linehaulMarginPerc;

    private BigDecimal totalCarrierAmt;

    private BigDecimal totalShipperAmt;

    private BigDecimal totalBenchmarkAmt;

    private PricingDetailsBO pricingDetails;

    private StatusYesNo blockedFrmBkng;

    private CarrierIntegrationType integrationType;
    
    private String ratingCarrierType;
    
    private String externalUuid;
    
    private String carrierQuoteNumber;
    
    private ShipmentType shipmentType = ShipmentType.LTL;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPricingProfileId() {
        return pricingProfileId;
    }

    public void setPricingProfileId(Long pricingProfileId) {
        this.pricingProfileId = pricingProfileId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getEstimatedTransitDate() {
        return estimatedTransitDate;
    }

    /**
     * Sets estimated date of transit.
     *
     * @param estimatedTransitDate the estimated date of the transit.
     */
    public void setEstimatedTransitDate(Date estimatedTransitDate) {
        this.estimatedTransitDate = estimatedTransitDate;
    }

    public Long getEstimatedTransitTime() {
        return estimatedTransitTime;
    }

    public void setEstimatedTransitTime(Long estimatedTransitTime) {
        this.estimatedTransitTime = estimatedTransitTime;
    }

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
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

    public void setServiceLevelDescription(String serviceLevelDesscription) {
        this.serviceLevelDescription = serviceLevelDesscription;
    }

    public CarrierDTO getCarrier() {
        return carrier;
    }

    /**
     * Sets carrier.
     *
     * @param carrier the time option.
     */
    public void setCarrier(CarrierDTO carrier) {
        this.carrier = carrier;
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

    public String getProhibited() {
        return prohibited;
    }

    public void setProhibited(String prohibited) {
        this.prohibited = prohibited;
    }

    public String getGuaranteedNameForBOL() {
        return guaranteedNameForBOL;
    }

    public void setGuaranteedNameForBOL(String guaranteedNameForBOL) {
        this.guaranteedNameForBOL = guaranteedNameForBOL;
    }

    public String getAddlGuaranInfo() {
        return addlGuaranInfo;
    }

    public void setAddlGuaranInfo(String addlGuaranInfo) {
        this.addlGuaranInfo = addlGuaranInfo;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public boolean isIncludeBenchmarkAccessorials() {
        return includeBenchmarkAccessorials;
    }

    public void setIncludeBenchmarkAccessorials(boolean includeBenchmarkAccessorials) {
        this.includeBenchmarkAccessorials = includeBenchmarkAccessorials;
    }

    public boolean isHideCostDetails() {
        return hideCostDetails;
    }

    public void setHideCostDetails(boolean hideCostDetails) {
        this.hideCostDetails = hideCostDetails;
    }

    public boolean isHideTerminalDetails() {
        return hideTerminalDetails;
    }

    public void setHideTerminalDetails(boolean hideTerminalDetails) {
        this.hideTerminalDetails = hideTerminalDetails;
    }

    public BigDecimal getCarrierInitialCost() {
        return carrierInitialCost;
    }

    public void setCarrierInitialCost(BigDecimal carrierInitialCost) {
        this.carrierInitialCost = carrierInitialCost;
    }

    public BigDecimal getCarrierDiscount() {
        return carrierDiscount;
    }

    public void setCarrierDiscount(BigDecimal carrierDiscount) {
        this.carrierDiscount = carrierDiscount;
    }

    public BigDecimal getShipperInitialCost() {
        return shipperInitialCost;
    }

    public void setShipperInitialCost(BigDecimal shipperInitialCost) {
        this.shipperInitialCost = shipperInitialCost;
    }

    public BigDecimal getShipperDiscount() {
        return shipperDiscount;
    }

    public void setShipperDiscount(BigDecimal shipperDiscount) {
        this.shipperDiscount = shipperDiscount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public List<CostDetailItemBO> getCostDetailItems() {
        return costDetailItems;
    }

    public void setCostDetailItems(List<CostDetailItemBO> costDetailItems) {
        this.costDetailItems = costDetailItems;
    }

    public BigDecimal getDefaultMarginAmt() {
        return defaultMarginAmt;
    }

    public void setDefaultMarginAmt(BigDecimal defaultMarginAmt) {
        this.defaultMarginAmt = defaultMarginAmt;
    }

    public StatusYesNo getRevenueOverride() {
        return revenueOverride;
    }

    public void setRevenueOverride(StatusYesNo revenueOverride) {
        this.revenueOverride = revenueOverride;
    }

    public StatusYesNo getCostOverride() {
        return costOverride;
    }

    public void setCostOverride(StatusYesNo costOverride) {
        this.costOverride = costOverride;
    }

    public BigDecimal getMinLinehaulMarginAmt() {
        return minLinehaulMarginAmt;
    }

    public void setMinLinehaulMarginAmt(BigDecimal minLinehaulMarginAmt) {
        this.minLinehaulMarginAmt = minLinehaulMarginAmt;
    }

    public BigDecimal getAppliedLinehaulMarginAmt() {
        return appliedLinehaulMarginAmt;
    }

    public void setAppliedLinehaulMarginAmt(BigDecimal appliedLinehaulMarginAmt) {
        this.appliedLinehaulMarginAmt = appliedLinehaulMarginAmt;
    }

    public BigDecimal getLinehaulMarginPerc() {
        return linehaulMarginPerc;
    }

    public void setLinehaulMarginPerc(BigDecimal linehaulMarginPerc) {
        this.linehaulMarginPerc = linehaulMarginPerc;
    }

    public BigDecimal getTotalCarrierAmt() {
        return totalCarrierAmt;
    }

    public void setTotalCarrierAmt(BigDecimal totalCarrierAmt) {
        this.totalCarrierAmt = totalCarrierAmt;
    }

    public BigDecimal getTotalShipperAmt() {
        return totalShipperAmt;
    }

    public void setTotalShipperAmt(BigDecimal totalShipperAmt) {
        this.totalShipperAmt = totalShipperAmt;
    }

    public BigDecimal getTotalBenchmarkAmt() {
        return totalBenchmarkAmt;
    }

    public void setTotalBenchmarkAmt(BigDecimal totalBenchmarkAmt) {
        this.totalBenchmarkAmt = totalBenchmarkAmt;
    }

    public PricingDetailsBO getPricingDetails() {
        return pricingDetails;
    }

    public void setPricingDetails(PricingDetailsBO pricingDetails) {
        this.pricingDetails = pricingDetails;
    }

    public StatusYesNo getBlockedFrmBkng() {
        return blockedFrmBkng;
    }

    public void setBlockedFrmBkng(StatusYesNo blockedFrmBkng) {
        this.blockedFrmBkng = blockedFrmBkng;
    }

    public CarrierIntegrationType getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(CarrierIntegrationType integrationType) {
        this.integrationType = integrationType;
    }

    public String getRatingCarrierType() {
        return ratingCarrierType;
    }

    public void setRatingCarrierType(String ratingCarrierType) {
        this.ratingCarrierType = ratingCarrierType;
    }

    public String getExternalUuid() {
        return externalUuid;
    }

    public void setExternalUuid(String externalUuid) {
        this.externalUuid = externalUuid;
    }

    public String getCarrierQuoteNumber() {
        return carrierQuoteNumber;
    }

    public void setCarrierQuoteNumber(String carrierQuoteNumber) {
        this.carrierQuoteNumber = carrierQuoteNumber;
    }

    public ShipmentType getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(ShipmentType shipmentType) {
        this.shipmentType = shipmentType;
    }

}
