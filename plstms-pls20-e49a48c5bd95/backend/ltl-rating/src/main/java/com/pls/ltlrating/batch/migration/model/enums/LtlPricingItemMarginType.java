package com.pls.ltlrating.batch.migration.model.enums;

import com.pls.ltlrating.domain.enums.LtlMarginType;

/**
 * Enum to holds value for margin type for price export/import.
 *
 * @author Alex Krychenko
 */
public enum LtlPricingItemMarginType implements PersistentLabeledEnum<LtlMarginType> {

    /**
     * Margin percent type.
     */
    MC(LtlMarginType.MC),

    /**
     * Per mile margin type.
     */
    MI(LtlMarginType.MI),

    /**
     * Flat fee margin type.
     */
    FL(LtlMarginType.FL),

    /**
     * Per each margin type.
     */
    PE(LtlMarginType.PE),

    /**
     * Per hundred weight margin type.
     */
    CW(LtlMarginType.CW);

    private final LtlMarginType persistentEnum;

    /**
     * Constructor.
     *
     * @param persistentEnum - persistent enum
     */
    LtlPricingItemMarginType(LtlMarginType persistentEnum) {
        this.persistentEnum = persistentEnum;
    }

    @Override
    public LtlMarginType getPersistentEnum() {
        return persistentEnum;
    }

    @Override
    public String getLabel() {
        return persistentEnum.getDescription();
    }
}
