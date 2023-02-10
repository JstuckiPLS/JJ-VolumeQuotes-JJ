package com.pls.core.domain.bo.proposal;

import java.io.Serializable;

import com.pls.core.domain.enums.Currency;

/**
 * Carrier DTO.
 *
 * @author Gleb Zgonikov
 */
public class CarrierDTO implements Serializable {
    private static final long serialVersionUID = 3101860643193461877L;

    private Long id;

    private String scac;

    private String name;

    private Currency currencyCode;

    private String logoPath;

    private String specialMessage;

    private boolean apiCapable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
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

    public String getLogoPath() {
        return logoPath;
    }

    /**
     * Sets path to the carrier's logo image.
     *
     * @param logoPath the path to the logo image.
     */
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getSpecialMessage() {
        return specialMessage;
    }

    /**
     * Sets special message.
     *
     * @param specialMessage the carrier's special message.
     */
    public void setSpecialMessage(String specialMessage) {
        this.specialMessage = specialMessage;
    }

    public boolean isApiCapable() {
        return apiCapable;
    }

    public void setApiCapable(boolean apiCapable) {
        this.apiCapable = apiCapable;
    }
}
