package com.pls.extint.shared;

/**
 * 
 * VO class stores the name and scac of the available carriers.
 * 
 * @author PAVANI CHALLA
 *
 */
public class AvailableCarrierVO {

    private String name;
    private String scac;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    @Override
    public String toString() {
        return "AvailableCarrierVO [name=" + name + ", scac=" + scac + "]";
    }

}
