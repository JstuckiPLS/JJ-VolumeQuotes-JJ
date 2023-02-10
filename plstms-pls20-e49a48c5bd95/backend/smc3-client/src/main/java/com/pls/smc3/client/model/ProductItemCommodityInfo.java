package com.pls.smc3.client.model;

/**
 * Class that holds product item information.
 *
 * @author Sergey Kirichenko
 */
public class ProductItemCommodityInfo {

    private final String commodityClass;
    private final double weight;

    /**
     * Constructor.
     *
     * @param weight is the item weight
     * @param commodityClass is the density class of the item
     */
    public ProductItemCommodityInfo(double weight, String commodityClass) {
        this.weight = weight;
        this.commodityClass = commodityClass;
    }

    public String getCommodityClass() {
        return commodityClass;
    }

    public double getWeight() {
        return weight;
    }
}
