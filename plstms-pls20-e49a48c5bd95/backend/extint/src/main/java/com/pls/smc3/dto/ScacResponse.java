package com.pls.smc3.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * SCAC response provides the destination service, carrier name etc.
 * 
 * @author Pavani Challa
 * 
 */
public class ScacResponse {

    private int days;

    private String destinationServiceType;

    private String errorCode;

    private String method;

    private String originServiceType;

    private String name;

    private String scac;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getDestinationServiceType() {
        return destinationServiceType;
    }

    public void setDestinationServiceType(String destinationServiceType) {
        this.destinationServiceType = destinationServiceType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOriginServiceType() {
        return originServiceType;
    }

    public void setOriginServiceType(String originServiceType) {
        this.originServiceType = originServiceType;
    }

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
        return ToStringBuilder.reflectionToString(this);
    }
}
