package com.pls.ltlrating.domain.enums;

/**
 * Ltl Margin Type enumeration.
 *
 * @author Artem Arapov
 *
 */
public enum LtlMarginType {

    MC("Margin %"), MI("Per Mile"), FL("Flat Fee"), PE("Per Piece"), CW("Per 100 Weight");

    private String description;

    LtlMarginType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
