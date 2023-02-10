package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.enums.CarrierIntegrationType;
import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.Currency;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.StatusYesNo;
import com.pls.ltlrating.domain.bo.LTLDayAndRossRateBO;
import com.pls.ltlrating.domain.enums.MoveType;
import com.pls.ltlrating.domain.enums.PricingDetailType;
import com.pls.ltlrating.domain.enums.PricingType;
import com.pls.ltlrating.domain.enums.UnitType;
import com.pls.ltlrating.integration.ltllifecycle.dto.response.QuoteResultDTO;
import com.pls.smc3.dto.LTLRateShipmentDTO;

/**
 * Result VO that contains selected rate profile details.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlRatingProfileVO implements Serializable {

    private static final long serialVersionUID = 7815637861236323L;

    private Long profileId;
    private Long profileDetailId;
    private Long pricingDetailId;
    private Long palletPricingDetailId;
    private String prohibitedCommodities;
    private Long shipperOrgId;
    private Long carrierOrgId;
    private PricingType pricingType;
    private String aliasName;
    private String note;
    private PricingDetailType pricingDetailType;
    private String ratingCarrierType;
    private String mileageType;
    private String mileageVersion;
    private String smc3Tariff;
    private String mscale;
    private String carrierName;
    private String scac;
    private String freightClass;
    private CommodityClass singleFakMappingClass;
    private String fakMappingAvailable;
    private Map<CommodityClass, CommodityClass> fakMapping;
    private UnitType costType;
    private BigDecimal unitCost;
    private BigDecimal minCost;
    private UnitType marginType;
    private BigDecimal unitMargin;
    private BigDecimal minMarginFlat;
    private LtlServiceType serviceType;
    private Integer geoLevel;
    private Integer minMiles;
    private Integer maxMiles;
    private String hideDetails;
    private String hideTerminalDetails;
    private Currency currencyCode;
    private Integer transitTime = 0;
    private Date transitDate;
    private MoveType movementType;
    private String profileName;
    private Date effectiveDate;
    private String useBlanket;
    private Long blanketProfileId;
    private String actualCarrierScac;
    private StatusYesNo blockedFrmBkng;
    private Boolean isExcludeFuel;
    
    private CarrierIntegrationType integrationType;
    private Boolean dispatchWithLtllc;
    
    /** Response we got from smc3 for this carrier (if profile is using SMC3 for ratingCarrierType)*/
    private LTLRateShipmentDTO smc3Response;
    /** Response we got from day&ross for this carrier (if profile is using Carrier API for ratingCarrierType) */
    private LTLDayAndRossRateBO dayAndRossResponse;
    /** Response we get from ltl-lifecycle for the carrier (if profile is using LTLLC for ratingCarrierType)*/
    private List<QuoteResultDTO> ltllcResponses;
    /** A single ltllc response, which needs to be processed */
    private QuoteResultDTO ltllcResponse;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getPricingDetailId() {
        return pricingDetailId;
    }

    public void setPricingDetailId(Long pricingDetailId) {
        this.pricingDetailId = pricingDetailId;
    }

    public String getProhibitedCommodities() {
        return prohibitedCommodities;
    }

    public void setProhibitedCommodities(String prohibitedCommodities) {
        this.prohibitedCommodities = prohibitedCommodities;
    }

    public Long getShipperOrgId() {
        return shipperOrgId;
    }

    public void setShipperOrgId(Long shipperOrgId) {
        this.shipperOrgId = shipperOrgId;
    }

    public Long getCarrierOrgId() {
        return carrierOrgId;
    }

    public void setCarrierOrgId(Long carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(String pricingType) {
        this.pricingType = StringUtils.isBlank(pricingType) ? null : PricingType.valueOf(pricingType);
    }

    public void setPricingTypeEnum(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public PricingDetailType getPricingDetailType() {
        return pricingDetailType;
    }

    public void setPricingDetailType(String pricingDetailType) {
        this.pricingDetailType = StringUtils.isBlank(pricingDetailType) ? null : PricingDetailType.valueOf(pricingDetailType);
    }

    public void setPricingDetailTypeEnum(PricingDetailType pricingDetailType) {
        this.pricingDetailType = pricingDetailType;
    }

    public String getRatingCarrierType() {
        return ratingCarrierType;
    }

    public void setRatingCarrierType(String ratingCarrierType) {
        this.ratingCarrierType = ratingCarrierType;
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

    public String getSmc3Tariff() {
        return smc3Tariff;
    }

    public void setSmc3Tariff(String smc3Tariff) {
        this.smc3Tariff = smc3Tariff;
    }

    public String getMscale() {
        return mscale;
    }

    public void setMscale(String mscale) {
        this.mscale = mscale;
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

    public UnitType getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = StringUtils.isBlank(costType) ? null : UnitType.getByName(costType);
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getMinCost() {
        return minCost;
    }

    public void setMinCost(BigDecimal minCost) {
        this.minCost = minCost;
    }

    public UnitType getMarginType() {
        return marginType;
    }

    public void setMarginType(String marginType) {
        this.marginType = StringUtils.isBlank(marginType) ? null : UnitType.getByName(marginType);
    }

    public BigDecimal getUnitMargin() {
        return unitMargin;
    }

    public void setUnitMargin(BigDecimal unitMargin) {
        this.unitMargin = unitMargin;
    }

    public BigDecimal getMinMarginFlat() {
        return minMarginFlat;
    }

    public void setMinMarginFlat(BigDecimal minMarginFlat) {
        this.minMarginFlat = minMarginFlat;
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

    public Integer getMinMiles() {
        return minMiles;
    }

    public void setMinMiles(BigDecimal minMiles) {
        this.minMiles = minMiles == null ? null : minMiles.intValue();
    }

    public Integer getMaxMiles() {
        return maxMiles;
    }

    public void setMaxMiles(BigDecimal maxMiles) {
        this.maxMiles = maxMiles == null ? null : maxMiles.intValue();
    }

    public Long getProfileDetailId() {
        return profileDetailId;
    }

    public void setProfileDetailId(Long profileDetailId) {
        this.profileDetailId = profileDetailId;
    }

    public Long getPalletPricingDetailId() {
        return palletPricingDetailId;
    }

    public void setPalletPricingDetailId(Long palletPricingDetailId) {
        this.palletPricingDetailId = palletPricingDetailId;
    }

    public Integer getTransitTime() {
        return transitTime;
    }

    public void setTransitTime(Integer transitTime) {
        this.transitTime = transitTime;
    }

    public String getFreightClass() {
        return freightClass;
    }

    public void setFreightClass(String freightClass) {
        this.freightClass = freightClass;
    }

    public String getFakMappingAvailable() {
        return fakMappingAvailable;
    }

    public void setFakMappingAvailable(String fakMappingAvailable) {
        this.fakMappingAvailable = fakMappingAvailable;
    }

    public CommodityClass getSingleFakMappingClass() {
        return singleFakMappingClass;
    }

    public void setSingleFakMappingClass(String singleFakMappingClass) {
        this.singleFakMappingClass = StringUtils.isBlank(singleFakMappingClass) ? null : CommodityClass.valueOf(singleFakMappingClass);
    }

    public Map<CommodityClass, CommodityClass> getFakMapping() {
        return fakMapping;
    }

    public void setFakMapping(Map<CommodityClass, CommodityClass> fakMapping) {
        this.fakMapping = fakMapping;
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

    public Integer getGeoLevel() {
        return geoLevel;
    }

    public void setGeoLevel(Integer geoLevel) {
        this.geoLevel = geoLevel;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = StringUtils.isBlank(currencyCode) ? null : Currency.valueOf(currencyCode);
    }

    public Date getTransitDate() {
        return transitDate;
    }

    public void setTransitDate(Date transitDate) {
        this.transitDate = transitDate;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = StringUtils.isBlank(movementType) ? null : MoveType.valueOf(movementType);
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getUseBlanket() {
        return useBlanket;
    }

    public void setUseBlanket(String useBlanket) {
        this.useBlanket = useBlanket;
    }

    public Long getBlanketProfileId() {
        return blanketProfileId;
    }

    public void setBlanketProfileId(Long blanketProfileId) {
        this.blanketProfileId = blanketProfileId;
    }

    public String getActualCarrierScac() {
        return actualCarrierScac;
    }

    public void setActualCarrierScac(String actualCarrierScac) {
        this.actualCarrierScac = actualCarrierScac;
    }

    public StatusYesNo getBlockedFrmBkng() {
        return blockedFrmBkng;
    }

    public void setBlockedFrmBkng(String blockedFrmBkng) {
        this.blockedFrmBkng = "Y".equalsIgnoreCase(blockedFrmBkng) ? StatusYesNo.YES : StatusYesNo.NO;
    }

    public Boolean getIsExcludeFuel() {
        return isExcludeFuel;
    }

    public void setIsExcludeFuel(String isExcludeFuel) {
        this.isExcludeFuel = StringUtils.isBlank(isExcludeFuel) ? null : Boolean.valueOf(isExcludeFuel);
    }

    public CarrierIntegrationType getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(String integrationType) {
        this.integrationType = StringUtils.isBlank(integrationType) ? null : CarrierIntegrationType.valueOf(integrationType);
    }
    
    public void setIntegrationTypeEnum(CarrierIntegrationType integrationType) {
        this.integrationType = integrationType;
    }

    public Boolean getDispatchWithLtllc() {
        return dispatchWithLtllc;
    }

    public void setDispatchWithLtllc(Boolean dispatchWithLtllc) {
        this.dispatchWithLtllc = dispatchWithLtllc;
    }

    public LTLRateShipmentDTO getSmc3Response() {
        return smc3Response;
    }

    public void setSmc3Response(LTLRateShipmentDTO smc3Response) {
        this.smc3Response = smc3Response;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public LTLDayAndRossRateBO getDayAndRossResponse() {
        return dayAndRossResponse;
    }

    public void setDayAndRossResponse(LTLDayAndRossRateBO dayAndRossResponse) {
        this.dayAndRossResponse = dayAndRossResponse;
    }

    public List<QuoteResultDTO> getLtllcResponses() {
        return ltllcResponses;
    }

    public void setLtllcResponses(List<QuoteResultDTO> ltllcResponses) {
        this.ltllcResponses = ltllcResponses;
    }
    
    public QuoteResultDTO getLtllcResponse() {
        return ltllcResponse;
    }

    public void setLtllcResponse(QuoteResultDTO ltllcResponse) {
        this.ltllcResponse = ltllcResponse;
    }

}
