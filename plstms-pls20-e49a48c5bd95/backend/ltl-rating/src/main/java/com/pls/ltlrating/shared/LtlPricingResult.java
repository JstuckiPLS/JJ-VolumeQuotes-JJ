package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.integration.ltllifecycle.dto.ShipmentType;
import com.pls.smc3.dto.LTLDetailDTO;

/**
 * VO that contains all objects to create an LTL Rating Profile.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingResult implements Serializable, Comparable<LtlPricingResult> {

    private static final long serialVersionUID = 3725371261232132234L;

    // The Profile Id of the profile picked for carrier.
    // In CTSI it is "Movement ID" and user wants this to be displayed in the popup of the pricing details.
    private Long profileId;
    private Long carrierOrgId;
    private String carrierName;
    private String scac;
    private CarrierIntegrationType integrationType;
    private String ratingCarrierType;
    private Long profileDetailId;
    private PricingType pricingType;
    // HideDetails: Values will be Y/N. When it is "Y", the pricing details including the cost/revenue should
    // not be shown on screen.
    // Only the carrier information like name/scac/prohibitted commodities, etc should be shown.
    private String hideDetails;
    private String hideTerminalDetails = "N"; // Flag to display terminals or not.
    private String prohibitedCommodities;
    private String carrierNote; // The Carrier note to shipper.
    private LtlServiceType serviceType; // Direct/Indirect
    private BigDecimal newProdLiability;
    private BigDecimal usedProdLiability;
    private Integer transitTime = 0;
    private Date transitDate;
    private Integer carrierTotalMiles;
    private Integer shipperTotalMiles;
    private Integer totalMiles;
    private boolean gainshareCustomer = false;
    private Currency currencyCode;
    private String tariffName;

    private Integer guaranteedTime;
    private String bolCarrierName; // If this value is available, it should be shown on the BOL. This is only
                                   // for guaranteed quotes.

    private BigDecimal carrierInitialLinehaul; // The amount retrieved from SMC3.
    private BigDecimal carrierLinehaulDiscount; // The discount amount.
    private BigDecimal carrierFinalLinehaul; // Final base price that needs to be paid to carrier
    private BigDecimal carrierFuelSurcharge;
    private Long carrierPricingDetailId;
    private Long shipperPricingDetailId;
    private Long carrierFSId;
    private BigDecimal carrierFuelDiscount;

    private BigDecimal shipperInitialLinehaul; // NOT USED and doesnt make sense but just added a field as it
                                               // is available on screen.
    private BigDecimal shipperLinehaulDiscount; // NOT USED and doesnt make sense but just added a field as it
                                                // is available on screen.
    private BigDecimal shipperFinalLinehaul; // Final base price that needs to be collected from shipper.
    private BigDecimal shipperFuelSurcharge;

    private BigDecimal benchmarkFinalLinehaul;
    private BigDecimal benchmarkFuelSurcharge;
    private Long benchmarkPricingProfileId;

    private BigDecimal carrierLinehaulForMargin;

    private BigDecimal minLinehaulMarginAmt;
    private BigDecimal appliedLinehaulMarginAmt;
    private BigDecimal linehaulMarginPerc;

    private String includeBenchmarkAcc;
    private MoveType movementType;
    private Date effectiveDate;

    // Accessorials
    private List<LtlPricingAccessorialResult> accessorials = new ArrayList<LtlPricingAccessorialResult>();

    // Guaranteed accessorial details
    private LtlPricingAccessorialResult guaranteed;

    // Guaranteed Additional Information, if available.
    private List<LtlPricingGuaranteedResult> guaranteedAddlInfo;

    private BigDecimal totalCarrierCost; // Nothing but final Carrier Cost
    private BigDecimal totalShipperCost; // Nothing but final Shipper Cost
    private BigDecimal totalBenchmarkCost; // Nothing but final Benchmark Cost

    private BigDecimal defaultMarginAmt;

    private StatusYesNo blockedFrmBkng;

    private List<LTLDetailDTO> costDetails;
    private BigDecimal smc3MinimumCharge;
    private BigDecimal totalChargeFromSmc3;
    private BigDecimal deficitChargeFromSmc3;
    private BigDecimal costAfterDiscount;
    private BigDecimal minimumCost;
    private BigDecimal costDiscount;
    private BigDecimal shipperCostWithDiscount;
    
    private String externalQuoteUuid;
    private String carrierQuoteNumber;
    private String serviceLevelCode; // in case of ltllc rates, we have to use this when dispatching load (other than standard, e.g. EXPEDITE, CAPLOAD, ACCELERATED)
    private String serviceLevelDescription;
    
    private ShipmentType shipmentType = ShipmentType.LTL;

    public Long getCarrierOrgId() {
        return carrierOrgId;
    }

    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
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

    public BigDecimal getCarrierInitialLinehaul() {
        return carrierInitialLinehaul;
    }

    public void setCarrierInitialLinehaul(BigDecimal carrierInitialLinehaul) {
        this.carrierInitialLinehaul = carrierInitialLinehaul;
    }

    public BigDecimal getCarrierLinehaulDiscount() {
        return carrierLinehaulDiscount;
    }

    public void setCarrierLinehaulDiscount(BigDecimal carrierLinehaulDiscount) {
        this.carrierLinehaulDiscount = carrierLinehaulDiscount;
    }

    public BigDecimal getCarrierFinalLinehaul() {
        return carrierFinalLinehaul;
    }

    public void setCarrierFinalLinehaul(BigDecimal carrierFinalLinehaul) {
        this.carrierFinalLinehaul = carrierFinalLinehaul;
    }

    public BigDecimal getCarrierFuelSurcharge() {
        return carrierFuelSurcharge;
    }

    public void setCarrierFuelSurcharge(BigDecimal carrierFuelSurcharge) {
        this.carrierFuelSurcharge = carrierFuelSurcharge;
    }

    public BigDecimal getShipperInitialLinehaul() {
        return shipperInitialLinehaul;
    }

    public void setShipperInitialLinehaul(BigDecimal shipperInitialLinehaul) {
        this.shipperInitialLinehaul = shipperInitialLinehaul;
    }

    public BigDecimal getShipperLinehaulDiscount() {
        return shipperLinehaulDiscount;
    }

    public void setShipperLinehaulDiscount(BigDecimal shipperLinehaulDiscount) {
        this.shipperLinehaulDiscount = shipperLinehaulDiscount;
    }

    public BigDecimal getShipperFinalLinehaul() {
        return shipperFinalLinehaul;
    }

    public void setShipperFinalLinehaul(BigDecimal shipperFinalLinehaul) {
        this.shipperFinalLinehaul = shipperFinalLinehaul;
    }

    public BigDecimal getShipperFuelSurcharge() {
        return shipperFuelSurcharge;
    }

    public void setShipperFuelSurcharge(BigDecimal shipperFuelSurcharge) {
        this.shipperFuelSurcharge = shipperFuelSurcharge;
    }

    public List<LtlPricingAccessorialResult> getAccessorials() {
        return accessorials;
    }

    public void setAccessorials(List<LtlPricingAccessorialResult> accessorials) {
        this.accessorials = accessorials;
    }

    public String getProhibitedCommodities() {
        return prohibitedCommodities;
    }

    public void setProhibitedCommodities(String prohibitedCommodities) {
        this.prohibitedCommodities = prohibitedCommodities;
    }

    public String getCarrierNote() {
        return carrierNote;
    }

    public void setCarrierNote(String carrierNote) {
        this.carrierNote = carrierNote;
    }

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = StringUtils.isBlank(serviceType) ? null : LtlServiceType.valueOf(serviceType);
    }

    public void setServiceTypeEnum(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getTotalCarrierCost() {
        return totalCarrierCost;
    }

    public void setTotalCarrierCost(BigDecimal totalCarrierCost) {
        this.totalCarrierCost = totalCarrierCost;
    }

    public BigDecimal getTotalShipperCost() {
        return totalShipperCost;
    }

    public void setTotalShipperCost(BigDecimal totalShipperCost) {
        this.totalShipperCost = totalShipperCost;
    }

    public Integer getGuaranteedTime() {
        return guaranteedTime;
    }

    public void setGuaranteedTime(Integer guaranteedTime) {
        this.guaranteedTime = guaranteedTime;
    }

    public LtlPricingAccessorialResult getGuaranteed() {
        return guaranteed;
    }

    public void setGuaranteed(LtlPricingAccessorialResult guaranteed) {
        this.guaranteed = guaranteed;
    }

    public List<LtlPricingGuaranteedResult> getGuaranteedAddlInfo() {
        return guaranteedAddlInfo;
    }

    public void setGuaranteedAddlInfo(List<LtlPricingGuaranteedResult> guaranteedAddlInfo) {
        this.guaranteedAddlInfo = guaranteedAddlInfo;
    }

    public String getBolCarrierName() {
        return bolCarrierName;
    }

    public void setBolCarrierName(String bolCarrierName) {
        this.bolCarrierName = bolCarrierName;
    }

    public Integer getTransitTime() {
        return transitTime;
    }

    public void setTransitTime(Integer transitTime) {
        this.transitTime = transitTime;
    }

    public BigDecimal getBenchmarkFinalLinehaul() {
        return benchmarkFinalLinehaul;
    }

    public void setBenchmarkFinalLinehaul(BigDecimal benchmarkFinalLinehaul) {
        this.benchmarkFinalLinehaul = benchmarkFinalLinehaul;
    }

    public BigDecimal getBenchmarkFuelSurcharge() {
        return benchmarkFuelSurcharge;
    }

    public void setBenchmarkFuelSurcharge(BigDecimal benchmarkFuelSurcharge) {
        this.benchmarkFuelSurcharge = benchmarkFuelSurcharge;
    }

    public Long getProfileDetailId() {
        return profileDetailId;
    }

    public void setProfileDetailId(Long profileDetailId) {
        this.profileDetailId = profileDetailId;
    }

    public Date getTransitDate() {
        return transitDate;
    }

    public void setTransitDate(Date transitDate) {
        this.transitDate = transitDate;
    }

    public BigDecimal getUsedProdLiability() {
        return usedProdLiability;
    }

    public void setUsedProdLiability(BigDecimal usedProdLiability) {
        this.usedProdLiability = usedProdLiability;
    }

    public BigDecimal getNewProdLiability() {
        return newProdLiability;
    }

    public void setNewProdLiability(BigDecimal newProdLiability) {
        this.newProdLiability = newProdLiability;
    }

    public Integer getTotalMiles() {
        return totalMiles;
    }

    public void setTotalMiles(Integer totalMiles) {
        this.totalMiles = totalMiles;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public boolean isGainshareCustomer() {
        return gainshareCustomer;
    }

    public void setGainshareCustomer(boolean gainshareCustomer) {
        this.gainshareCustomer = gainshareCustomer;
    }

    public BigDecimal getTotalBenchmarkCost() {
        return totalBenchmarkCost;
    }

    public void setTotalBenchmarkCost(BigDecimal totalBenchmarkCost) {
        this.totalBenchmarkCost = totalBenchmarkCost;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getHideDetails() {
        return hideDetails;
    }

    public void setHideDetails(String hideDetails) {
        this.hideDetails = hideDetails;
    }

    public String getHideTerminalDetails() {
        return hideTerminalDetails;
    }

    public void setHideTerminalDetails(String hideTerminalDetails) {
        this.hideTerminalDetails = hideTerminalDetails;
    }

    public String getIncludeBenchmarkAcc() {
        return includeBenchmarkAcc;
    }

    public void setIncludeBenchmarkAcc(String includeBenchmarkAcc) {
        this.includeBenchmarkAcc = includeBenchmarkAcc;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Currency currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getCarrierLinehaulForMargin() {
        return carrierLinehaulForMargin;
    }

    public void setCarrierLinehaulForMargin(BigDecimal carrierLinehaulForMargin) {
        this.carrierLinehaulForMargin = carrierLinehaulForMargin;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public BigDecimal getDefaultMarginAmt() {
        return defaultMarginAmt;
    }

    public void setDefaultMarginAmt(BigDecimal defaultMarginAmt) {
        this.defaultMarginAmt = defaultMarginAmt;
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

    public Integer getCarrierTotalMiles() {
        return carrierTotalMiles;
    }

    public void setCarrierTotalMiles(Integer carrierTotalMiles) {
        this.carrierTotalMiles = carrierTotalMiles;
    }

    public Integer getShipperTotalMiles() {
        return shipperTotalMiles;
    }

    public void setShipperTotalMiles(Integer shipperTotalMiles) {
        this.shipperTotalMiles = shipperTotalMiles;
    }

    public Long getCarrierPricingDetailId() {
        return carrierPricingDetailId;
    }

    public void setCarrierPricingDetailId(Long carrierPricingDetailId) {
        this.carrierPricingDetailId = carrierPricingDetailId;
    }

    public Long getShipperPricingDetailId() {
        return shipperPricingDetailId;
    }

    public void setShipperPricingDetailId(Long shipperPricingDetailId) {
        this.shipperPricingDetailId = shipperPricingDetailId;
    }

    public Long getBenchmarkPricingProfileId() {
        return benchmarkPricingProfileId;
    }

    public void setBenchmarkPricingProfileId(Long benchmarkPricingProfileId) {
        this.benchmarkPricingProfileId = benchmarkPricingProfileId;
    }

    public Long getCarrierFSId() {
        return carrierFSId;
    }

    public void setCarrierFSId(Long carrierFSId) {
        this.carrierFSId = carrierFSId;
    }

    public List<LTLDetailDTO> getCostDetails() {
        return costDetails;
    }

    public void setCostDetails(List<LTLDetailDTO> costDetails) {
        this.costDetails = costDetails;
    }

    public BigDecimal getSmc3MinimumCharge() {
        return smc3MinimumCharge;
    }

    public void setSmc3MinimumCharge(BigDecimal smc3MinimumCharge) {
        this.smc3MinimumCharge = smc3MinimumCharge;
    }

    public BigDecimal getTotalChargeFromSmc3() {
        return totalChargeFromSmc3;
    }

    public void setTotalChargeFromSmc3(BigDecimal totalChargeFromSmc3) {
        this.totalChargeFromSmc3 = totalChargeFromSmc3;
    }

    public BigDecimal getCostAfterDiscount() {
        return costAfterDiscount;
    }

    public void setCostAfterDiscount(BigDecimal costAfterDiscount) {
        this.costAfterDiscount = costAfterDiscount;
    }

    public BigDecimal getDeficitChargeFromSmc3() {
        return deficitChargeFromSmc3;
    }

    public void setDeficitChargeFromSmc3(BigDecimal deficitChargeFromSmc3) {
        this.deficitChargeFromSmc3 = deficitChargeFromSmc3;
    }

    public BigDecimal getMinimumCost() {
        return minimumCost;
    }

    public void setMinimumCost(BigDecimal minimumCost) {
        this.minimumCost = minimumCost;
    }

    public BigDecimal getCostDiscount() {
        return costDiscount;
    }

    public void setCostDiscount(BigDecimal costDiscount) {
        this.costDiscount = costDiscount;
    }

    public BigDecimal getCarrierFuelDiscount() {
        return carrierFuelDiscount;
    }

    public void setCarrierFuelDiscount(BigDecimal carrierFuelDiscount) {
        this.carrierFuelDiscount = carrierFuelDiscount;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(MoveType movementType) {
        this.movementType = movementType;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public StatusYesNo getBlockedFrmBkng() {
        return blockedFrmBkng;
    }

    public void setBlockedFrmBkng(StatusYesNo blockedFrmBkng) {
        this.blockedFrmBkng = blockedFrmBkng;
    }

    /**
     * Method to compare object and find the least shipper cost carriers and order the results in ascending
     * order.
     *
     * @param comparableObj
     *            - the object to be compared.
     * @return return least cost object.
     */
    public int compareTo(LtlPricingResult comparableObj) {
        // ascending order
        return new CompareToBuilder().append(totalShipperCost, comparableObj.totalShipperCost).toComparison();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public BigDecimal getShipperCostWithDiscount() {
        return shipperCostWithDiscount;
    }

    public void setShipperCostWithDiscount(BigDecimal shipperCostWithDiscount) {
        this.shipperCostWithDiscount = shipperCostWithDiscount;
    }

    public String getExternalQuoteUuid() {
        return externalQuoteUuid;
    }

    public void setExternalQuoteUuid(String externalQuoteUuid) {
        this.externalQuoteUuid = externalQuoteUuid;
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

    public ShipmentType getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(ShipmentType shipmentType) {
        this.shipmentType = shipmentType;
    }

}
