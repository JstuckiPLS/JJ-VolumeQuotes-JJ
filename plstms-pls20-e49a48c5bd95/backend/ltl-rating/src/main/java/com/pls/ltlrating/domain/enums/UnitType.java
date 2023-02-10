package com.pls.ltlrating.domain.enums;

import java.util.stream.Stream;

/**
 * Contains all Unit Types to calculate the rate for an order.
 *
 * @author Hima Bindu Challa
 */
public enum UnitType {

    PC("% of Freight"),
    MI("Per Mile"),
    FL("Flat Fee"),
    PE("Per Piece"),
    CW("Per 100 Weight"),
    DC("Discount %"),
    MC("Margin %"),
    GS("Gainshare");

    private String description;

    UnitType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get {@link UnitType} by name.
     *
     * @param name
     *            {@link UnitType#name()}
     * @return {@link UnitType}
     */
    public static UnitType getByName(final String name) {
        return Stream.of(UnitType.values()).filter(value -> value.name().equals(name)).findFirst().orElse(null);
    }
}