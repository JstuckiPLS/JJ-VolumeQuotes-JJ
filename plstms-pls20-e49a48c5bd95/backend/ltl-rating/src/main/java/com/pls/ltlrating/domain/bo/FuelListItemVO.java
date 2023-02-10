package com.pls.ltlrating.domain.bo;

import java.io.Serializable;
import java.util.Date;

import com.pls.ltlrating.domain.enums.FuelWeekDays;

/**
 * Business object that is used to hold the list of the fuel triggers for active/expired/archived tabs.
 *
 * @author Pavani Challa
 *
 */
public class FuelListItemVO implements Serializable {

    private static final long serialVersionUID = -1147209361886217990L;

    private Long id;

    private Long pricingProfileId;

    private String origin;

    private String region;

    private String effectiveDay;

    private Date expirationDate;

    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEffectiveDay() {
        return effectiveDay;
    }

    public void setEffectiveDay(String effectiveDay) {
        this.effectiveDay = FuelWeekDays.valueOf(effectiveDay).getDescription();
    }

    public Long getPricingProfileId() {
        return pricingProfileId;
    }

    public void setPricingProfileId(Long pricingProfileId) {
        this.pricingProfileId = pricingProfileId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
