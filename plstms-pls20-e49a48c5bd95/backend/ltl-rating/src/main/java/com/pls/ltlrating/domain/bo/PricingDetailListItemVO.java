package com.pls.ltlrating.domain.bo;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.pls.ltlrating.domain.enums.LtlCostType;

/**
 * Business object that is used to hold the list of the pricing details for active/expired/archived tabs.
 *
 * @author Pavani Challa
 *
 */
public class PricingDetailListItemVO {

    private Long id;

    private Long profileId;

    private String origin;

    private String destination;

    private String plsCost;

    private String minCost;

    private String costType;

    private BigDecimal unitCost;

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

    public String getMinCost() {
        return this.minCost;
    }

    /**
     * Set the min cost on the pricing detail. If the min cost is not null, adds the currency symbol.
     *
     * @param minCost
     *            min cost to be set.
     */
    public void setMinCost(String minCost) {
        if (StringUtils.isEmpty(minCost)) {
            return;
        }

        this.minCost = "$" + minCost;
    }

    public String getCostType() {
        return costType;
    }

    /**
     * Sets the cost type on the pricing detail.
     *
     * @param costType
     *            cost type to set.
     */
    public void setCostType(String costType) {
        this.costType = costType;

        if (this.unitCost != null && this.costType != null) {
            preparePlsCostValue();
        }
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    /**
     * Sets the unit cost on the pricing detail.
     *
     * @param unitCost
     *            unit cost to set
     */
    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;

        if (this.unitCost != null && this.costType != null) {
            preparePlsCostValue();
        }
    }

    private void preparePlsCostValue() {
        if (getCostType() == null || getUnitCost() == null) {
            return;
        }

        String formattedUnitCost = getUnitCost().toString();
        String plsCostValue = "";

        switch (LtlCostType.valueOf(getCostType())) {
        case FL:
            plsCostValue = "$" + formattedUnitCost;
            break;
        case DC:
            plsCostValue = formattedUnitCost + "%";
            break;
        case CW:
            plsCostValue = formattedUnitCost + "/CW";
            break;
        case MI:
            plsCostValue = formattedUnitCost + "/MI";
            break;
        case PE:
            plsCostValue = formattedUnitCost + "/Piece";
            break;
        default:
            plsCostValue = "";
            break;
        }

        setPlsCost(plsCostValue);
    }
}
