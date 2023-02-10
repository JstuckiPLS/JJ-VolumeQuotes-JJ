package com.pls.smc3.client.model;

import java.math.BigDecimal;

/**
 * Class holds carrier tariff information. Will be replaced with the functionality of the module 4.
 * 
 * @author Sergey Kirichenko
 */
public class CarrierTariff {
    private Long id;
    private String scac;
    private String name;
    private BigDecimal discount;
    private BigDecimal fuelSurcharge;
    private String logoPath;
    private String specialMessage;
    private String prohibitedCommodities;
    private String liability;

    public Long getId() {
        return id;
    }

    /**
     * Sets carrier's id.
     *
     * @param id the carrier's id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getScac() {
        return scac;
    }

    /**
     * Sets carrier's SCAC code.
     *
     * @param scac the carrier's SCAC code.
     */
    public void setScac(String scac) {
        this.scac = scac;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets carrier's name.
     *
     * @param name the carrier's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Sets tariff discount.
     *
     * @param discount the tariff discount.
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getFuelSurcharge() {
        return fuelSurcharge;
    }

    /**
     * Sets fuel surcharge.
     *
     * @param fuelSurcharge the fuel surcharge.
     */
    public void setFuelSurcharge(BigDecimal fuelSurcharge) {
        this.fuelSurcharge = fuelSurcharge;
    }

    public String getLogoPath() {
        return logoPath;
    }

    /**
     * Sets path to carrier's logo.
     *
     * @param logoPath the path to carrier's logo.
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
     * @param specialMessage the special message.
     */
    public void setSpecialMessage(String specialMessage) {
        this.specialMessage = specialMessage;
    }

    public String getProhibitedCommodities() {
        return prohibitedCommodities;
    }

    /**
     * Sets prohibited commodities.
     *
     * @param prohibitedCommodities the prohibited commodities.
     */
    public void setProhibitedCommodities(String prohibitedCommodities) {
        this.prohibitedCommodities = prohibitedCommodities;
    }

    public String getLiability() {
        return liability;
    }

    /**
     * Sets carrier's liability.
     *
     * @param liability the carrier's liability.
     */
    public void setLiability(String liability) {
        this.liability = liability;
    }
}
