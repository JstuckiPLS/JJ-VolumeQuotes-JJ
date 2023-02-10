package com.pls.core.domain.enums;

/**
 * Ltl Service type enumeration.
 * 
 * @author Artem Arapov
 * 
 */
public enum LtlServiceType {

    DIRECT("Direct"), INDIRECT("Indirect"), BOTH("Both");

    private String description;

    LtlServiceType(String description) {
        this.description = description;
    }

    /**
     * Get description.
     * 
     * @return String
     * */
    public String getDescription() {
        return this.description;
    }
}
