package com.pls.ltlrating.batch.migration.model.enums;

/**
 * Enum describes what tariff is geo type.
 *
 * @author Alex Kyrychenko.
 */
public enum TariffGeoType {
    US_ONLY("US/US"), CANADA_ONLY("CN/CN"), US_CANADA("US/CN");

    private final String code;

    TariffGeoType(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
