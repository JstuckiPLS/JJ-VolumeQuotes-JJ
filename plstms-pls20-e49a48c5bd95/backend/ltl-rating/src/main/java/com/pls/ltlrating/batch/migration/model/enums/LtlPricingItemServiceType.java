package com.pls.ltlrating.batch.migration.model.enums;

import com.pls.core.domain.enums.LtlServiceType;

/**
 * Enum to holds value for service type for price export/import.
 *
 * @author Alex Krychenko
 */
public enum LtlPricingItemServiceType implements PersistentLabeledEnum<LtlServiceType> {
    DIRECT(LtlServiceType.DIRECT), INDIRECT(LtlServiceType.INDIRECT), BOTH(LtlServiceType.BOTH);

    private final LtlServiceType persistentEnum;

    /**
     * Constructor.
     *
     * @param persistentEnum - persistent enum
     */
    LtlPricingItemServiceType(LtlServiceType persistentEnum) {
        this.persistentEnum = persistentEnum;
    }

    @Override
    public LtlServiceType getPersistentEnum() {
        return persistentEnum;
    }

    @Override
    public String getLabel() {
        return persistentEnum.getDescription();
    }
}
