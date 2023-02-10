package com.pls.ltlrating.domain.enums;

/**
 * Ltl Cost type enumeration.
 *
 * @author Artem Arapov
 *
 */
public enum LtlCostType {

    DC("Discount %"), MI("Per Mile"), FL("Flat Fee"), PE("Per Piece"), CW("Per 100 Weight"), PC("% of Freight");

    private String description;

    LtlCostType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
