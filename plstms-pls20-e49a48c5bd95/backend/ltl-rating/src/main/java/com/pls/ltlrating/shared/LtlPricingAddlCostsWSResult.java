package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * VO that contains additional costs of WS response.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingAddlCostsWSResult implements Serializable {

    private static final long serialVersionUID = 3712351834622132234L;

    private String addlCostType;
    private String description;
    private BigDecimal totalCost;

    public String getAddlCostType() {
        return addlCostType;
    }
    public void setAddlCostType(String addlCostType) {
        this.addlCostType = addlCostType;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("addlCostType", addlCostType)
                .append("description", description)
                .append("totalCost", totalCost);
        return builder.toString();
    }
}
