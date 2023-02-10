package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * VO that contains actual costs of WS response.
 *
 * @author Hima Bindu Challa
 *
 */
public class LtlPricingCostDetailsWSResult implements Serializable {

    private static final long serialVersionUID = 2719169125193561234L;

    private BigDecimal initialLinehaul;
    private BigDecimal discount;
    private BigDecimal finalLinehaul;
    private BigDecimal fuelSurcharge;
    private List<LtlPricingAccWSResult> accessorials;
    private List<LtlPricingAddlCostsWSResult> additionalCosts;

    public BigDecimal getInitialLinehaul() {
        return initialLinehaul;
    }
    public void setInitialLinehaul(BigDecimal initialLinehaul) {
        this.initialLinehaul = initialLinehaul;
    }
    public BigDecimal getDiscount() {
        return discount;
    }
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
    public BigDecimal getFinalLinehaul() {
        return finalLinehaul;
    }
    public void setFinalLinehaul(BigDecimal finalLinehaul) {
        this.finalLinehaul = finalLinehaul;
    }
    public BigDecimal getFuelSurcharge() {
        return fuelSurcharge;
    }
    public void setFuelSurcharge(BigDecimal fuelSurcharge) {
        this.fuelSurcharge = fuelSurcharge;
    }
    public List<LtlPricingAccWSResult> getAccessorials() {
        return accessorials;
    }
    public void setAccessorials(List<LtlPricingAccWSResult> accessorials) {
        this.accessorials = accessorials;
    }
    public List<LtlPricingAddlCostsWSResult> getAdditionalCosts() {
        return additionalCosts;
    }
    public void setAdditionalCosts(List<LtlPricingAddlCostsWSResult> additionalCosts) {
        this.additionalCosts = additionalCosts;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("initialLinehaul", initialLinehaul)
                .append("discount", discount)
                .append("finalLinehaul", finalLinehaul)
                .append("fuelSurcharge", fuelSurcharge)
                .append("accessorials", accessorials)
                .append("additionalCosts", additionalCosts);
        return builder.toString();
    }

}
