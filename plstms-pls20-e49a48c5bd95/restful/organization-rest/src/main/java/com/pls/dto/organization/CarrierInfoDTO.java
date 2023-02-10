package com.pls.dto.organization;

import com.pls.core.domain.enums.Currency;

/**
 * DTO for carrier.
 * 
 * @author Alexander Nalapko
 */
public class CarrierInfoDTO {
    private Long id;
    private String name;
    private String scac;
    private Currency currencyCode;
    private Boolean apiCapable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Currency currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Boolean getApiCapable() {
        return apiCapable;
    }

    public void setApiCapable(Boolean apiCapable) {
        this.apiCapable = apiCapable;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }
}