package com.pls.smc3.dto;

import java.util.List;

import com.pls.core.shared.AddressVO;

/**
 * DTO object for terminal details.
 * 
 * @author PAVANI CHALLA
 * 
 */
public class TerminalDetailsDTO {

    private String scac;
    private List<AddressVO> shipmentAddresses;
    private String method;
    private String carrierName;
    private List<String> scacList;

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public List<AddressVO> getShipmentAddresses() {
        return shipmentAddresses;
    }

    public void setShipmentAddresses(List<AddressVO> shipmentAddresses) {
        this.shipmentAddresses = shipmentAddresses;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public List<String> getScacList() {
        return scacList;
    }

    public void setScacList(List<String> scacList) {
        this.scacList = scacList;
    }

}
