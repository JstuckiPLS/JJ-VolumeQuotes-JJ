package com.pls.ltlrating.domain.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.ChargeRuleTypeEnum;

/**
 * Business object that is used to hold the list of the Guaranteed services for active/expired/archived tabs.
 *
 * @author Pavani Challa
 *
 */
public class GuaranteedPriceListItemVO implements Serializable {

    private static final long serialVersionUID = 7824984472563290217L;

    private Long id;

    private Long ltlPricProfDetailId;

    private ChargeRuleTypeEnum chargeRuleType;

    private BigDecimal unitCost;

    private BigDecimal minCost = BigDecimal.ZERO;

    private Long time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLtlPricProfDetailId() {
        return ltlPricProfDetailId;
    }

    public void setLtlPricProfDetailId(Long ltlPricProfDetailId) {
        this.ltlPricProfDetailId = ltlPricProfDetailId;
    }

    public ChargeRuleTypeEnum getChargeRuleType() {
        return chargeRuleType;
    }

    public void setChargeRuleType(ChargeRuleTypeEnum chargeRuleType) {
        this.chargeRuleType = chargeRuleType;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getMinCost() {
        return minCost;
    }

    public void setMinCost(BigDecimal minCost) {
        this.minCost = minCost;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


}
