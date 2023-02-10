package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * VO that contains accessorial costs of WS response.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingAccWSResult implements Serializable {

    private static final long serialVersionUID = 923143724363132234L;

    private String accessorialType;
    private String description;
    private BigDecimal totalCost;

    public String getAccessorialType() {
        return accessorialType;
    }
    public void setAccessorialType(String accessorialType) {
        this.accessorialType = accessorialType;
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
        builder.append("accessorialType", accessorialType)
                .append("description", description)
                .append("totalCost", totalCost);
        return builder.toString();
    }
}
