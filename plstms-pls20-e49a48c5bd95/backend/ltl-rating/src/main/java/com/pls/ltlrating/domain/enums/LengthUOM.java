package com.pls.ltlrating.domain.enums;

/**
 * Unit of Measurement for Length.
 * @author Ashwini Neelgund
 */
public enum LengthUOM {

    IN("Inch");

    private String description;

    LengthUOM(String description) {
        this.description = description;
    }

    /**
     * Get Description of UOM.
     * @return description.
     */
    public String getDescription() {
        return this.description;
    }
}