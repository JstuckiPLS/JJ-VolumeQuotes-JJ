package com.pls.smc3.dto;

/**
 * Holds SCAC and method of the request. Method like LTL or TL etc.
 * 
 * @author Pavani Challa
 *
 */
public class ScacRequest {

    String scac;
    String method;

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
