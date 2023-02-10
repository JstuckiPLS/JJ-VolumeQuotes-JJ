package com.pls.dto;

import java.util.Date;

/**
 * DTO Object for Pricing Details List Item.
 * 
 * @author Artem Arapov
 * 
 */
public class PricingDetailListItemDTO {

    private Long id;

    private Long profileId;

    private String origin;

    private String destination;

    private String plsCost;

    private Date effectiveDate;

    private String minCost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPlsCost() {
        return plsCost;
    }

    public void setPlsCost(String plsCost) {
        this.plsCost = plsCost;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getMinCost() {
        return minCost;
    }

    public void setMinCost(String minCost) {
        this.minCost = minCost;
    }
}
