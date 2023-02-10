package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.core.domain.enums.CommodityClass;
import com.pls.core.domain.enums.LtlServiceType;
import com.pls.core.shared.AddressVO;
import com.pls.ltlrating.domain.enums.MoveType;

/**
 * Criteria class to get the rates for the selected customer.
 * @author Hima Bindu Challa
 *
 */
public class GetOrderRatesCO implements Serializable {

    private static final long serialVersionUID = -3235615272346384L;

    private Long shipperOrgId;
    private Long carrierOrgId; //Optional. Should be passed on Module 14, if specific carrier is selected.
    private Date shipDate;
    private AddressVO originAddress;
    private AddressVO destinationAddress;
    private List<RateMaterialCO> materials;
    private BigDecimal totalWeight;
    private Integer totalQuantity;
    private Integer totalPieces;
    private List<String> accessorialTypes;
    private Integer guaranteedTime;
    private boolean palletType;
    private Set<CommodityClass> commodityClassSet;
    private boolean gainshareAccount;
    private MoveType movementType;
    private Integer milemakerMiles;
    private Integer pcmilerMiles;
    private BigDecimal maxLength;
    private BigDecimal maxWidth;
    private LtlServiceType serviceType;
    private List<Long> pricingProfileIDs;
    
    private boolean requestLTLRates = true;
    private boolean requestVLTLRates;

    //For external Webservice calls
    private String userId;
    private String password;
    private String requestType = "LCC";
    private String scac;

    public BigDecimal getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(BigDecimal maxLength) {
        this.maxLength = maxLength;
    }
    
    public BigDecimal getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(BigDecimal maxWidth) {
        this.maxWidth = maxWidth;
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

    public AddressVO getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(AddressVO originAddress) {
        this.originAddress = originAddress;
    }

    public AddressVO getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(AddressVO destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public List<RateMaterialCO> getMaterials() {
        return materials;
    }

    public void setMaterials(List<RateMaterialCO> materials) {
        this.materials = materials;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getTotalPieces() {
        return totalPieces;
    }

    public void setTotalPieces(Integer totalPieces) {
        this.totalPieces = totalPieces;
    }

    public Integer getGuaranteedTime() {
        return guaranteedTime;
    }

    public void setGuaranteedTime(Integer guaranteedTime) {
        this.guaranteedTime = guaranteedTime;
    }

    public List<String> getAccessorialTypes() {
        return accessorialTypes;
    }

    public void setAccessorialTypes(List<String> accessorialTypes) {
        this.accessorialTypes = accessorialTypes;
    }

    public boolean isPalletType() {
        return palletType;
    }

    public void setPalletType(boolean palletType) {
        this.palletType = palletType;
    }

    public Set<CommodityClass> getCommodityClassSet() {
        return commodityClassSet;
    }

    public void setCommodityClassSet(Set<CommodityClass> commodityClassSet) {
        this.commodityClassSet = commodityClassSet;
    }

    public boolean isGainshareAccount() {
        return gainshareAccount;
    }

    public void setGainshareAccount(boolean gainshareAccount) {
        this.gainshareAccount = gainshareAccount;
    }

    public MoveType getMovementType() {
        return movementType;
    }

    public void setMovementType(MoveType movementType) {
        this.movementType = movementType;
    }


    public Integer getMilemakerMiles() {
        return milemakerMiles;
    }

    public void setMilemakerMiles(Integer milemakerMiles) {
        this.milemakerMiles = milemakerMiles;
    }

    public Integer getPcmilerMiles() {
        return pcmilerMiles;
    }

    public void setPcmilerMiles(Integer pcmilerMiles) {
        this.pcmilerMiles = pcmilerMiles;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public LtlServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(LtlServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public List<Long> getPricingProfileIDs() {
        return pricingProfileIDs;
    }

    public void setPricingProfileIDs(List<Long> pricingProfileIDs) {
        this.pricingProfileIDs = pricingProfileIDs;
    }

    public boolean isRequestVLTLRates() {
        return requestVLTLRates;
    }

    public void setRequestVLTLRates(boolean requestVLTLRates) {
        this.requestVLTLRates = requestVLTLRates;
    }

    public boolean isRequestLTLRates() {
        return requestLTLRates;
    }

    public void setRequestLTLRates(boolean requestLTLRates) {
        this.requestLTLRates = requestLTLRates;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("shipperOrgId", shipperOrgId)
                .append("carrierOrgId", carrierOrgId)
                .append("shipDate", shipDate)
                .append("originAddress Postal Code", originAddress.getPostalCode())
                .append("originAddress City", originAddress.getCity())
                .append("originAddress State", originAddress.getStateCode())
                .append("originAddress Country", originAddress.getCountryCode())
                .append("destinationAddress Postal Code", destinationAddress.getPostalCode())
                .append("destinationAddress City", destinationAddress.getCity())
                .append("destinationAddress State", destinationAddress.getStateCode())
                .append("destinationAddress Country", destinationAddress.getCountryCode())
                .append("materials", materials)
                .append("totalWeight", totalWeight)
                .append("totalQuantity", totalQuantity)
                .append("totalPieces", totalPieces)
                .append("accessorialTypes", accessorialTypes)
                .append("guaranteedTime", guaranteedTime)
                .append("palletType", palletType)
                .append("commodityClassSet", commodityClassSet)
                .append("movementType", movementType)
                .append("milemakerMiles", milemakerMiles)
                .append("pcmilerMiles", pcmilerMiles)
                .append("userId", userId)
                .append("password", password)
                .append("requestType", requestType)
                .append("scac", scac)
                .append("maxLength", maxLength);

        return builder.toString();
    }
}
