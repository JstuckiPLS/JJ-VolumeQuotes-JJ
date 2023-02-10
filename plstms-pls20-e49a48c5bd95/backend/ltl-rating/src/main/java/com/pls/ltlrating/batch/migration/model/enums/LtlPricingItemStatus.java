package com.pls.ltlrating.batch.migration.model.enums;

import com.pls.core.shared.Status;

/**
 * Enum to display item status on EXCEL file and to convert it to persistent value.
 *
 * @author Alex Krychenko
 */
public enum LtlPricingItemStatus implements PersistentLabeledEnum<Status> {
    ACTIVE("Active", Status.ACTIVE), INACTIVE("Inactive", Status.INACTIVE);

    private final String label;
    private final Status persistentEnum;

    /**
     * Constructor.
     *
     * @param label          - label for enum
     * @param persistentEnum - refference to persistent enum
     */
    LtlPricingItemStatus(String label, Status persistentEnum) {
        this.label = label;
        this.persistentEnum = persistentEnum;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Status getPersistentEnum() {
        return persistentEnum;
    }
}
