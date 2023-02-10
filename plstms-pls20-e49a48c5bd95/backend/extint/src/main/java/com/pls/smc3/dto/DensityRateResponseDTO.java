package com.pls.smc3.dto;

import java.util.List;

import com.pls.smc3.domain.SMCErrorCodeEntity;

/**
 * Density rate response DTO.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class DensityRateResponseDTO {

    private String shipmentDateCCYYMMDD;
    private String originPostalCode;
    private String originCity;
    private String originState;
    private String originCountry;
    private String destinationPostalCode;
    private String destinationCity;
    private String destinationState;
    private String destinationCountry;
    private String lhGrossCharge;
    private String minimumCharge;
    private String totalCharge;
    private String tariffName;
    private String effectiveDate;
    private String errorCode;
    private List<DensityDetailDTO> details;
    private SMCErrorCodeEntity errorEntity;

    public String getShipmentDateCCYYMMDD() {
        return shipmentDateCCYYMMDD;
    }

    public void setShipmentDateCCYYMMDD(String shipmentDateCCYYMMDD) {
        this.shipmentDateCCYYMMDD = shipmentDateCCYYMMDD;
    }

    public String getOriginPostalCode() {
        return originPostalCode;
    }

    public void setOriginPostalCode(String originPostalCode) {
        this.originPostalCode = originPostalCode;
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

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getDestinationPostalCode() {
        return destinationPostalCode;
    }

    public void setDestinationPostalCode(String destinationPostalCode) {
        this.destinationPostalCode = destinationPostalCode;
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

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getLhGrossCharge() {
        return lhGrossCharge;
    }

    public void setLhGrossCharge(String lhGrossCharge) {
        this.lhGrossCharge = lhGrossCharge;
    }

    public String getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(String minimumCharge) {
        this.minimumCharge = minimumCharge;
    }

    public String getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(String totalCharge) {
        this.totalCharge = totalCharge;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public List<DensityDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<DensityDetailDTO> details) {
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "DensityRateResponseDTO [shipmentDateCCYYMMDD=" + shipmentDateCCYYMMDD + ", originPostalCode="
                + originPostalCode + ", originCity=" + originCity + ", originState=" + originState + ", originCountry="
                + originCountry + ", destinationPostalCode=" + destinationPostalCode + ", destinationCity="
                + destinationCity + ", destinationState=" + destinationState + ", destinationCountry="
                + destinationCountry + ", lhGrossCharge=" + lhGrossCharge + ", minimumCharge=" + minimumCharge
                + ", totalCharge=" + totalCharge + ", tariffName=" + tariffName + ", effectiveDate=" + effectiveDate
                + ", errorCode=" + errorCode + ", details=" + details + "]";
    }

    public SMCErrorCodeEntity getErrorEntity() {
        return errorEntity;
    }

    public void setErrorEntity(SMCErrorCodeEntity errorEntity) {
        this.errorEntity = errorEntity;
    }
}
