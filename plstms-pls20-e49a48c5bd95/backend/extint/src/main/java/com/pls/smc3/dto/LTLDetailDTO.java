package com.pls.smc3.dto;

import java.io.Serializable;

/**
 * LTL Detail DTO object. Used for both response and request.
 * 
 * @author PAVANI CHALLA
 */
public class LTLDetailDTO implements Serializable {
    private static final long serialVersionUID = -1429700972482333214L;

    private String charge;
    private String error;
    private String nmfcClass;
    private String enteredNmfcClass;
    private String rate;
    private String weight;

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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

    @Override
    public String toString() {
        return "LTLDetailDTO [charge=" + charge + ", error=" + error + ", nmfcClass=" + nmfcClass + ", rate=" + rate
                + ", weight=" + weight + "]";
    }

}
