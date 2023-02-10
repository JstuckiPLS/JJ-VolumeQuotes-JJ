package com.pls.core.domain.bo.proposal;

import java.io.Serializable;

/**
 * LTL Detail DTO object for carrier cost breakup.
 * 
 * @author Ashwini Neelgund
 */
public class Smc3CostDetailsDTO implements Serializable {

    private static final long serialVersionUID = 2966489093946770712L;
    private String charge;
    private String nmfcClass;
    private String enteredNmfcClass;
    private String rate;
    private String weight;
    private String description;
    private String nmfc;
    private String quantity;

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getNmfcClass() {
        return nmfcClass;
    }

    public void setNmfcClass(String nmfcClass) {
        this.nmfcClass = nmfcClass;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getEnteredNmfcClass() {
        return enteredNmfcClass;
    }

    public void setEnteredNmfcClass(String enteredNmfcClass) {
        this.enteredNmfcClass = enteredNmfcClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNmfc() {
        return nmfc;
    }

    public void setNmfc(String nmfc) {
        this.nmfc = nmfc;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
