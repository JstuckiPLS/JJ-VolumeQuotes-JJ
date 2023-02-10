package com.pls.core.service.impl.file.enums;

/**
 * Units for KPI export.
 * @author Alexander Nalapko
 *
 */
public enum KPIUnits {
    CURRENCY("$"),
    LBS(" Lbs"),
    PERCENT("%");

    private String value;

    KPIUnits(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
