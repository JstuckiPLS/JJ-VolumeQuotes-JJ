package com.pls.ltlrating.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Class that captures margin settings for BLANKET profiles.
 *
 * @author Hima Bindu Challa
 */
public class LtlRatingMarginVO implements Serializable {

    private static final long serialVersionUID = 8278375123756234563L;

    private LtlRatingProfileVO pricingDetail;
    private LtlRatingFSTriggerVO fuelSurcharge;
    private LtlRatingGuaranteedVO guaranteed;
    private final Map<String, AccessorialPricingVO> accessorials = new HashMap<>();
    private BigDecimal defaultMarginPercent;
    private BigDecimal minMarginFlatAmount;

    public LtlRatingProfileVO getPricingDetail() {
        return pricingDetail;
    }
    public void setPricingDetail(LtlRatingProfileVO pricingDetail) {
        this.pricingDetail = pricingDetail;
    }
    public LtlRatingGuaranteedVO getGuaranteed() {
        return guaranteed;
    }
    public void setGuaranteed(LtlRatingGuaranteedVO guaranteed) {
        this.guaranteed = guaranteed;
    }
    public Map<String, AccessorialPricingVO> getAccessorials() {
        return accessorials;
    }
    public BigDecimal getDefaultMarginPercent() {
        return defaultMarginPercent;
    }

    public void setDefaultMarginPercent(BigDecimal defaultMarginPercent) {
        this.defaultMarginPercent = defaultMarginPercent;
    }

    public BigDecimal getMinMarginFlatAmount() {
        return minMarginFlatAmount;
    }

    public void setMinMarginFlatAmount(BigDecimal minMarginFlatAmount) {
        this.minMarginFlatAmount = minMarginFlatAmount;
    }
    public LtlRatingFSTriggerVO getFuelSurcharge() {
        return fuelSurcharge;
    }
    public void setFuelSurcharge(LtlRatingFSTriggerVO fuelSurcharge) {
        this.fuelSurcharge = fuelSurcharge;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("pricingDetail", pricingDetail)
                .append("fuelSurcharge", fuelSurcharge)
                .append("guaranteed", guaranteed)
                .append("accessorials", accessorials)
                .append("defaultMarginPercent", defaultMarginPercent);

        return builder.toString();
    }
}
