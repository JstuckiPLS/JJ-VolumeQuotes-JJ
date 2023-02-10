package com.pls.ltlrating.shared;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * VO for accessorials pricing details of one accessorial type.
 *
 * @author Aleksandr Leshchenko
 */
public class AccessorialPricingVO {
    private Integer geoLevel;

    private LtlRatingAccessorialsVO bestPrice;
    private BigDecimal bestCost;

    private final List<LtlRatingAccessorialsVO> prices = new ArrayList<>();

    public Integer getGeoLevel() {
        return geoLevel;
    }

    public void setGeoLevel(Integer geoLevel) {
        this.geoLevel = geoLevel;
    }

    public LtlRatingAccessorialsVO getBestPrice() {
        return bestPrice;
    }

    public void setBestPrice(LtlRatingAccessorialsVO bestPrice) {
        this.bestPrice = bestPrice;
    }

    public BigDecimal getBestCost() {
        return bestCost;
    }

    public void setBestCost(BigDecimal bestCost) {
        this.bestCost = bestCost;
    }

    public List<LtlRatingAccessorialsVO> getPrices() {
        return prices;
    }
}
