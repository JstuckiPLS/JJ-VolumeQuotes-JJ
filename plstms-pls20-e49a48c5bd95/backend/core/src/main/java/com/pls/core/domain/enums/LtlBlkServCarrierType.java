package com.pls.core.domain.enums;

/**
 * Ltl Carrier type enumeration.
 * 
 * @author Ashwini Neelgund
 * 
 */
public enum LtlBlkServCarrierType {

    NONE(0), ALL_CARRIER(1), SPECIFIC(2);

    private int description;

    LtlBlkServCarrierType(int description) {
        this.description = description;
    }

    /**
     * Get description.
     * 
     * @return String
     * */
    public int getDescription() {
        return this.description;
    }
}
