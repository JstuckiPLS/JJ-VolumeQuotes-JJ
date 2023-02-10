package com.pls.extint.shared;

/**
 * 
 * VO class stores the name and scac of the available carriers.
 * 
 * @author Kircicegi Korkmaz
 *
 */
public class AvailableCarrierRequestVO {

    private String scac;

    private String serviceMethod;

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    @Override
    public String toString() {
        return "AvailableCarrierVO [serviceMethod=" + serviceMethod + ", scac=" + scac + "]";
    }

}
