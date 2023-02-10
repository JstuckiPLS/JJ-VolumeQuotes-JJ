package com.pls.ltlrating.domain.enums;

/**
 * Weight Unit of Measure.
 *
 * @author Artem Arapov
 *
 */
public enum WeightUOM {

    LB("Lbs"), KG("Kg");

    private String description;

    WeightUOM(String description) {
        this.description = description;
    }

    /**
     * Get Description of UOM.
     *
     * @return description.
     */
    public String getDescription() {
        return this.description;
    }
}
