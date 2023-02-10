package com.pls.smc3.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pls.smc3.domain.SMCErrorCodeEntity;

/**
 * LTL RATE DTO object. Used for both response and request.
 * 
 * @author Pavani Challa
 * 
 */
public class LTLRateShipmentDTO {
    private Integer id;
    private String destinationCountry;
    private String destinationPostalCode;
    private String destinationCity;
    private String destinationState;
    private List<LTLDetailDTO> details;
    private String effectiveDate;
    private String errorCode;
    private String originCountry;
    private String originPostalCode;
    private String originCity;
    private String originState;
    private String shipmentID;
    private String tariffName;
    private String totalCharge;
    private BigDecimal totalWeight;
    private String discountPercent;
    private BigDecimal totalChargeFromDetails;
    private BigDecimal deficitCharge;
    private BigDecimal deficitRate;
    private BigDecimal deficitWeight;
    private String deficitNmfcClass;
    private BigDecimal actualWeight;
    private BigDecimal billedWeight;
    private BigDecimal minimumCharge;

    // Used to set the shipment date to the request.
    private Date shipmentDate;
    // Response back from service.
    private String shipmentDateCCYYMMDD;
    private SMCErrorCodeEntity errorCodeEntity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getDestinationPostalCode() {
        return destinationPostalCode;
    }

    public void setDestinationPostalCode(String destinationPostalCode) {
        this.destinationPostalCode = destinationPostalCode;
    }

    public List<LTLDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<LTLDetailDTO> details) {
        this.details = details;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getOriginPostalCode() {
        return originPostalCode;
    }

    public void setOriginPostalCode(String originPostalCode) {
        this.originPostalCode = originPostalCode;
    }

    public String getShipmentDateCCYYMMDD() {
        return shipmentDateCCYYMMDD;
    }

    public void setShipmentDateCCYYMMDD(String shipmentDateCCYYMMDD) {
        this.shipmentDateCCYYMMDD = shipmentDateCCYYMMDD;
    }

    public String getShipmentID() {
        return shipmentID;
    }

    public void setShipmentID(String shipmentID) {
        this.shipmentID = shipmentID;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public String getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(String totalCharge) {
        this.totalCharge = totalCharge;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public SMCErrorCodeEntity getErrorCodeEntity() {
        return errorCodeEntity;
    }

    public void setErrorCodeEntity(SMCErrorCodeEntity errorCodeEntity) {
        this.errorCodeEntity = errorCodeEntity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(String destinationState) {
        this.destinationState = destinationState;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginState() {
        return originState;
    }

    public void setOriginState(String originState) {
        this.originState = originState;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getTotalChargeFromDetails() {
        return totalChargeFromDetails;
    }

    public void setTotalChargeFromDetails(BigDecimal totalChargeFromDetails) {
        this.totalChargeFromDetails = totalChargeFromDetails;
    }

    public BigDecimal getDeficitCharge() {
        return deficitCharge;
    }

    public void setDeficitCharge(BigDecimal deficitCharge) {
        this.deficitCharge = deficitCharge;
    }

    public BigDecimal getDeficitRate() {
        return deficitRate;
    }

    public void setDeficitRate(BigDecimal deficitRate) {
        this.deficitRate = deficitRate;
    }

    public BigDecimal getDeficitWeight() {
        return deficitWeight;
    }

    public void setDeficitWeight(BigDecimal deficitWeight) {
        this.deficitWeight = deficitWeight;
    }

    public String getDeficitNmfcClass() {
        return deficitNmfcClass;
    }

    public void setDeficitNmfcClass(String deficitNmfcClass) {
        this.deficitNmfcClass = deficitNmfcClass;
    }

    public BigDecimal getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(BigDecimal actualWeight) {
        this.actualWeight = actualWeight;
    }

    public BigDecimal getBilledWeight() {
        return billedWeight;
    }

    public void setBilledWeight(BigDecimal billedWeight) {
        this.billedWeight = billedWeight;
    }

    public BigDecimal getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(BigDecimal minimumCharge) {
        this.minimumCharge = minimumCharge;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
