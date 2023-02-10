package com.pls.core.domain.enums;

/**
 * Rate type value.
 * 
 * @author Gleb Zgonikov
 */
public enum RateType {
    //TODO human readable labels should be removed from DOM objects.

    FL("Flat rate"),
    MI("Per mile rate"),
    CW("Per hundred-weight rate");

    private String description;

    RateType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
