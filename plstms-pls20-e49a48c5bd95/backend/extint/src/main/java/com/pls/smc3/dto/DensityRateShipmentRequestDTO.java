package com.pls.smc3.dto;

import java.util.Date;
import java.util.List;

/**
 * Density rate shipment request DTO.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class DensityRateShipmentRequestDTO {

    private Date shipmentDateCCYYMMDD;
    private String originCountry;
    private String originPostalCode;
    private String originCity;
    private String originState;
    private String destinationCountry;
    private String destinationPostalCode;
    private String destinationCity;
    private String destinationState;
    private String tariffName;
    private String detailType;
    private List<DensityRequestDetailDTO> details;

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

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public List<DensityRequestDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<DensityRequestDetailDTO> details) {
        this.details = details;
    }

    public Date getShipmentDateCCYYMMDD() {
        return shipmentDateCCYYMMDD;
    }

    public void setShipmentDateCCYYMMDD(Date shipmentDateCCYYMMDD) {
        this.shipmentDateCCYYMMDD = shipmentDateCCYYMMDD;
    }

}
