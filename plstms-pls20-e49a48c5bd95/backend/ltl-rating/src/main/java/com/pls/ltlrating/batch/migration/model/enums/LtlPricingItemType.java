package com.pls.ltlrating.batch.migration.model.enums;

/**
 * Enum that depicts pricing item type.
 *
 * @author Alex Krychenko
 */
public enum LtlPricingItemType implements HasLabel {
    PRICE("Price"), ACCESSORIAL("Accessorial"), FUEL("Fuel surcharge"), PALLET("Pallet");

    private final String label;

    /**
     * Constructor.
     *
     * @param label - label
     */
    LtlPricingItemType(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Method to convert from string label to enum instance.
     *
     * @param label - label to find
     * @return - {@link LtlPricingItemType}
     */
    public LtlPricingItemType fromLabel(String label) {
        for (LtlPricingItemType type : LtlPricingItemType.values()) {
            if (type.getLabel().equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported label");
    }
}
