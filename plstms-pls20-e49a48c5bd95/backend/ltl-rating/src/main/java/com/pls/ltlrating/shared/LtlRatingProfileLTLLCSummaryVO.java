package com.pls.ltlrating.shared;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.enums.Currency;
import com.pls.ltlrating.domain.enums.PricingType;

/**
 * Contains basic info about active profiles for getting rates from LTLLC
 */
public class LtlRatingProfileLTLLCSummaryVO implements Serializable {

    private static final long serialVersionUID = -3521686016509381064L;

    private String scac;
    private PricingType pricingType;
    private Currency currencyCode;

    public String getScac() {
        return scac;
    }

    public void setScac(String scac) {
        this.scac = scac;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(String pricingType) {
        this.pricingType = StringUtils.isBlank(pricingType) ? null : PricingType.valueOf(pricingType);
    }

    public void setPricingTypeEnum(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = StringUtils.isBlank(currencyCode) ? null : Currency.valueOf(currencyCode);
    }

    public void setCurrencyCodeEnum(Currency currencyCode) {
        this.currencyCode = currencyCode;
    }

}
