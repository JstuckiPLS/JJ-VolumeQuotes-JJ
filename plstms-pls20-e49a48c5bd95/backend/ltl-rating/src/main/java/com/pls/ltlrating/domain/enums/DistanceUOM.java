package com.pls.ltlrating.domain.enums;

/**
 * Distance Units of Measure.
 *
 * @author Artem Arapov
 *
 */
public enum DistanceUOM {

    ML("Miles"), KM("Km");

    private String description;

    DistanceUOM(String description) {
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
