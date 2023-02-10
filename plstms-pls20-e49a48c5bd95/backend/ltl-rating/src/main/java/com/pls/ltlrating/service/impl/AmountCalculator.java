package com.pls.ltlrating.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.ObjectUtils;

import com.pls.core.domain.enums.LtlAccessorialType;
import com.pls.ltlrating.domain.LtlGuaranteedPriceEntity.ChargeRuleTypeEnum;
import com.pls.ltlrating.domain.enums.UnitType;
import com.pls.ltlrating.domain.enums.UpchargeType;
import com.pls.ltlrating.shared.GetOrderRatesCO;

/**
 * Class to calculate the final rates based on the input values.
 * @author Hima Bindu Challa
 *
 */
public final class AmountCalculator {

    private AmountCalculator() {

    }

    /**
     *
     * Calculate the final cost amount based on input values.
     *
     * @param costUnitType - cost unit type Ex: Flat, per mile, discount, per piece, per 100 weight
     * @param costUnitValue - amount
     * @param basePrice - base price to calculate final price.
     * @param miles - number of miles
     * @param weight - weight
     * @param pieces - pieces of product.
     * @return final amount
     */
    public static BigDecimal getCalculatedCost(UnitType costUnitType, BigDecimal costUnitValue, BigDecimal basePrice, Integer miles,
            BigDecimal weight, Integer pieces) {
        if (UnitType.FL == costUnitType) {
            return costUnitValue;
        } else if (isValidUnitType(UnitType.DC, costUnitType, basePrice)) {
            return basePrice.subtract(basePrice.multiply(costUnitValue).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        } else if (isValidUnitType(UnitType.MI, costUnitType, miles)) {
            return multiply(costUnitValue, miles);
        } else if (isValidUnitType(UnitType.CW, costUnitType, weight)) {
            return weight.multiply(costUnitValue).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        } else if (isValidUnitType(UnitType.PE, costUnitType, pieces)) {
            return multiply(costUnitValue, pieces);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Calculate the final revenue amount based on input values.
     *
     * @param unitType - cost unit type Ex: Flat, per mile, margin percent, per piece, per 100 weight
     * @param unitValue - amount
     * @param cost - cost to calculate final revenue.
     * @param miles - number of miles
     * @param weight - weight
     * @param pieces - pieces of product.
     * @return final amount
     */
    public static BigDecimal getCalculatedRevenue(UnitType unitType, BigDecimal unitValue, BigDecimal cost, Integer miles, BigDecimal weight,
            Integer pieces) {
        if (cost == null || unitType == null || !isPositive(unitValue)) {
            return ObjectUtils.defaultIfNull(cost, BigDecimal.ZERO);
        }

        switch (unitType) {
        case FL:
            return cost.add(unitValue);
        case MC:
            return multiply(cost, 100).divide(new BigDecimal(100).subtract(unitValue), 2, RoundingMode.HALF_UP);
        case MI:
            return calculateRevenue(unitValue, cost, miles);
        case CW:
            return calculateCostPerHundredWeight(unitValue, cost, weight);
        case PE:
            return calculateRevenue(unitValue, cost, pieces);
        default:
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculate the final revenue amount based on default margin percentage.
     *
     * @param defaultPercentage - Default margin percentage.
     * @param cost - cost to calculate final revenue.
     * @return final amount
     */
    public static BigDecimal getCalcRevByDefaultMarginPerc(BigDecimal defaultPercentage, BigDecimal cost) {

        if (cost == null) {
            return BigDecimal.ZERO;
        }
        //This is Markup calculation and we dont need this. We need margin calculation.
        //Hence commenting it. Uncomment it later in case you need it
//            amount = (cost != null && defaultPercentage != null && defaultPercentage > 0.0)
//                ? ((cost * defaultPercentage) / 100) : 0.0;

        //Margin Percent calculation
        /*
         * (Revenue - Cost / Revenue) * 100 = Margin %
         * ((100 * Revenue) - (100  * Cost)) / Revenue = Margin %
         * (100 * Revenue) - (100 * Cost) = Margin % * Revenue
         * (100 * Revenue) - (Margin % * Revenue) = 100 * Cost
         * Revenue (100 - Margin %) = 100 * Cost
         * Revenue = (100 * Cost) / 100 - Margin %
         */
        return isPositive(defaultPercentage)
                ? multiply(cost, 100).divide(new BigDecimal(100).subtract(defaultPercentage), 2, RoundingMode.HALF_UP) : cost;
    }

    /**
     *
     * Calculate the final cost amount based on input values.
     *
     * @param unitType - cost unit type Ex: Flat Charge, Flat Percent.
     * @param unitValue - amount
     * @param basePrice - base price to calculate final price.
     * @param minimum - minimum cost
     * @param maximum - maximum cost
     * @return final amount
     */
    public static BigDecimal getCalculatedGuaranteedAmount(String unitType, BigDecimal unitValue, BigDecimal basePrice, BigDecimal minimum,
            BigDecimal maximum) {

        BigDecimal amount = BigDecimal.ZERO;

        //Assuming we have only Flat charge and Flat charge by percent options.
        if (ChargeRuleTypeEnum.FL.name().equals(unitType) && unitValue != null) {
            amount = unitValue;
        } else if (ChargeRuleTypeEnum.PC.name().equals(unitType) && basePrice != null && unitValue != null) {
            amount = basePrice.multiply(unitValue).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        }

        if (isNotZero(minimum)) {
            amount = amount.max(minimum);
        }
        if (isNotZero(maximum)) {
            amount = amount.min(maximum);
        }

        return amount;
    }

    /**
     *
     * Calculate the final Fuel Surcharge amount based on input values.
     *
     * @param carrierLinehaul - The calculated Carrier linehaul.
     * @param surchargePct - Surcharge amount that needs to be applied on linehaul.
     * @return final amount
     */
    public static BigDecimal getCalculatedCarrierFSAmount(BigDecimal carrierLinehaul, BigDecimal surchargePct) {
        if (surchargePct == null || carrierLinehaul == null) {
            return BigDecimal.ZERO;
        }
        return surchargePct.multiply(carrierLinehaul).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     *
     * Calculate the final Fuel Surcharge amount based on input values.
     *
     * @param carrierFSAmount - The calculated Carrier Fuel Surcharge.
     * @param upchargeType - If any upcharge to be applied, then upcharge amount.
     * @param upchargeFlat - The upcharge flat percent.
     * @param upchargePercent - The upcharge percent on top of calculated amount.
     * @return final amount
     */
    public static BigDecimal getCalculatedShipperFSAmount(BigDecimal carrierFSAmount,
            String upchargeType, BigDecimal upchargeFlat, BigDecimal upchargePercent) {

        if (carrierFSAmount == null) {
            return BigDecimal.ZERO;
        }

        if (upchargeType == null) {
            return carrierFSAmount;
        }

        //Assuming we have only Upcharge Flat % and Upcharge % options.
        if (UpchargeType.FL.name().equals(upchargeType)) {
            return upchargeFlat != null ? upchargeFlat.add(carrierFSAmount) : carrierFSAmount;

        } else if (UpchargeType.PC.name().equals(upchargeType)) {
            return !isPositive(upchargePercent) ? carrierFSAmount
                    : multiply(carrierFSAmount, 100).divide(new BigDecimal(100).subtract(upchargePercent), 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     *
     * Calculate the final accessorial cost amount based on input values.
     *
     * @param costUnitType
     *            - cost unit type Ex: Flat, per mile, discount, per piece, per 100 weight
     * @param costUnitValue
     *            - amount
     * @param basePrice
     *            - base price to calculate final price.
     * @param miles
     *            - number of miles
     * @param minCost
     *            - Minimum Cost.
     * @param maxCost
     *            - Maximum Cost.
     * @param minLength
     *            minimum length
     * @param ratesCO
     *            for total pieces and max length
     * @param accessorialType
     *            - accessorial type
     * @return final amount
     */
    public static BigDecimal getAccCalculatedCost(UnitType costUnitType, BigDecimal costUnitValue, BigDecimal basePrice, Integer miles,
            BigDecimal minCost, BigDecimal maxCost, BigDecimal minLength, BigDecimal minWidth, GetOrderRatesCO ratesCO, String accessorialType) {

        BigDecimal amount = BigDecimal.ZERO;
        if (costUnitValue != null) {
            amount = getAccessorialsCost(costUnitType, costUnitValue, basePrice, miles, getAccessorialCostMultiplier(accessorialType, ratesCO),
                    ratesCO.getTotalWeight());
        }

        if (isNotZero(minCost)) {
            amount = amount.max(minCost);
        }
        if (isNotZero(maxCost)) {
            amount = amount.min(maxCost);
        }

        //If the user enters length less than the minimum length specified by a carrier in
        //accessorial pricing details, then display the carrier in the 'get quotes' result
        //screen but with the cost and revenue for the accessorial set to zero dollars.
        if ((minLength != null && ratesCO.getMaxLength().compareTo(minLength) < 0) &&
        		(minWidth != null && ratesCO.getMaxWidth().compareTo(minWidth) < 0)) {
            amount = BigDecimal.ZERO;
        }

        return amount;
    }

    /**
     * Method return quantity for over dimension accessorial type, otherwise - pieces.
     * 
     * @param accessorialType - accessorial type
     * @param ratesCO - for total quantity or pieces
     * 
     * @return cost multiplier
     */
    public static Integer getAccessorialCostMultiplier(String accessorialType, GetOrderRatesCO ratesCO) {
        return LtlAccessorialType.OVER_DIMENSION.getCode().equals(accessorialType) ? ratesCO.getTotalQuantity() : ratesCO.getTotalPieces();
    }

    /**
     * Method to get the calculation notes to give an idea to user on how the rate is calculated.
     * @param unitType - The unit type used in calculation
     * @param unitCost - Unit cost used in calculation
     * @param id - The record id for identification which record was picked.
     * @return calculated note.
     */
    public static String getCalulatedNote(String unitType, BigDecimal unitCost, Long id) {
        if (unitCost == null || id == null) {
            return "";
        }
        if (unitType == null) {
            return "No Margin set up";
        }

        return getNote(unitType, id, unitCost.setScale(2, BigDecimal.ROUND_UP).toString());
    }

    private static BigDecimal getAccessorialsCost(UnitType costUnitType, BigDecimal costUnitValue, BigDecimal basePrice, Integer miles,
            Integer totalPieces, BigDecimal totalWeight) {
        if (UnitType.FL == costUnitType) {
            return costUnitValue;
        } else if (isValidUnitType(UnitType.PE, costUnitType, totalPieces)) {
            return multiply(costUnitValue, totalPieces);
        } else if (isValidUnitType(UnitType.MI, costUnitType, miles)) {
            return multiply(costUnitValue, miles);
        } else if (isValidUnitType(UnitType.PC, costUnitType, basePrice)) {
            return basePrice.multiply(costUnitValue).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        } else if (isValidUnitType(UnitType.CW, costUnitType, totalWeight)) {
            return costUnitValue.multiply(totalWeight).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private static String getNote(String unitType, Long id, String unitCost) {
        switch (unitType) {
        case "FL":
            return "Flat Fee" + getIDString(id);
        case "PC":
            return unitCost + "% of Freight rate" + getIDString(id);
        case "MI":
            return "$" + unitCost + "/Mile" + getIDString(id);
        case "PE":
            return "$" + unitCost + "/Piece" + getIDString(id);
        case "CW":
            return "$" + unitCost + "/100 Weight" + getIDString(id);
        case "DC":
            return unitCost + "% Discount" + getIDString(id);
        case "MC":
            return unitCost + "% Margin" + getIDString(id);
        case "GS":
            return unitCost + "% PLS Percent - Gainshare";
        default:
            return "";
        }
    }

    private static boolean isValidUnitType(UnitType unitType, UnitType costUnitType, Object costItem) {
        return unitType.equals(costUnitType) && costItem != null;
    }

    private static BigDecimal multiply(BigDecimal multiplicand1, Integer multiplicand2) {
        return multiplicand1.multiply(new BigDecimal(multiplicand2));
    }

    private static BigDecimal calculateCostPerHundredWeight(BigDecimal unitValue, BigDecimal cost, BigDecimal weight) {
        return weight != null ? cost.add(weight.multiply(unitValue).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)) : cost;
    }

    private static BigDecimal calculateRevenue(BigDecimal unitValue, BigDecimal cost, Integer units) {
        return units != null ? cost.add(multiply(unitValue, units)) : cost;
    }

    private static boolean isPositive(BigDecimal unitValue) {
        return unitValue != null && unitValue.compareTo(BigDecimal.ZERO) > 0;
    }

    private static boolean isNotZero(BigDecimal minimum) {
        return minimum != null && minimum.compareTo(BigDecimal.ZERO) != 0;
    }

    private static String getIDString(Long id) {
        return " (ID:" + id.toString() + ")";
    }

}
