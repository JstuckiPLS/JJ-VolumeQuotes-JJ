package com.pls.smc3.dto;

import java.util.List;

/**
 * Response DTO.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class TerminalResponseDetailDTO {

    private String scac;
    private String carrierName;
    private String serviceMethod;
    private List<CarrierTerminalDetailDTO> details;

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public List<CarrierTerminalDetailDTO> getDetails() {
        return details;
    }

    public void setDetails(List<CarrierTerminalDetailDTO> details) {
        this.details = details;
    }

}
