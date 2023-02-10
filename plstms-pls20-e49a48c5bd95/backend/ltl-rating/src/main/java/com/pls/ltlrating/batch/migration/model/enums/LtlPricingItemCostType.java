package com.pls.ltlrating.batch.migration.model.enums;

import com.pls.ltlrating.domain.enums.LtlCostType;

/**
 * Enum to holds value for cost type for price export/import.
 *
 * @author Alex Krychenkoß.
 */
public enum LtlPricingItemCostType implements PersistentLabeledEnum<LtlCostType> {
    /**
     * Discount cost type.
     */
    DC(LtlCostType.DC),

    /**
     * Per mile cost type.
     */
    MI(LtlCostType.MI),

    /**
     * Flat fee cost type.
     */
    FL(LtlCostType.FL),

    /**
     * Per each cost type.
     */
    PE(LtlCostType.PE),

    /**
     * Per hundred weight cost type.
     */
    CW(LtlCostType.CW),

    /**
     * Percentage cost type.
     */
    PC(LtlCostType.PC);

    private final LtlCostType persistentEnum;

    /**
     * Constructor.
     *
     * @param persistentEnum - {@link LtlCostType}
     */
    LtlPricingItemCostType(LtlCostType persistentEnum) {
        this.persistentEnum = persistentEnum;
    }

    @Override
    public LtlCostType getPersistentEnum() {
        return persistentEnum;
    }

    @Override
    public String getLabel() {
        return persistentEnum.getDescription();
    }
}
