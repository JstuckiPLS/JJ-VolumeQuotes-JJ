package com.pls.core.domain.enums;

/**
 * Indicator of density, and each possible commodity is assigned to a class based on its dimensions and weight.
 * The lower the class, the denser the material. This enables a carrier to decide the cost of a shipment.
 *
 * @author Maxim Medvedev
 */
public enum CommodityClass {
    CLASS_50("50"),
    CLASS_55("55"),
    CLASS_60("60"),
    CLASS_65("65"),
    CLASS_70("70"),
    CLASS_77_5("77.5"),
    CLASS_85("85"),
    CLASS_92_5("92.5"),
    CLASS_100("100"),
    CLASS_110("110"),
    CLASS_125("125"),
    CLASS_150("150"),
    CLASS_175("175"),
    CLASS_200("200"),
    CLASS_250("250"),
    CLASS_300("300"),
    CLASS_400("400"),
    CLASS_500("500");

    private String dbCode;

    /**
     * Constructor.
     *
     * @param dbCode String code that is used for DB. All possible values see in "COMMODITY_CLASS_CODES" table.
     */
    CommodityClass(String dbCode) {
        this.dbCode = dbCode;
    }

    /**
     * String code that is used for DB. All possible values see in "COMMODITY_CLASS_CODES" table.
     *
     * @return Not <code>null</code> {@link String}.
     */
    public String getDbCode() {
        return dbCode;
    }

    /**
     * Converts string representation to enum value.
     *
     * @param dbCode Database code.
     * @return Not <code>null</code> {@link CommodityClass} instance if dbCode is valid. Otherwise throws {@link IllegalArgumentException}
     */
    public static CommodityClass convertFromDbCode(String dbCode) {
        for (CommodityClass commodityClass : CommodityClass.values()) {
            if (commodityClass.getDbCode().equals(dbCode)) {
                return commodityClass;
            }
        }
        throw new IllegalArgumentException("Unexpected '" + dbCode + "' value for commodity class");
    }

    /**
     * Converts string representation to enum value.
     *
     * @param dbCode
     *            Database code.
     * @return {@link CommodityClass} instance if dbCode is valid, <code>null</code> otherwise.
     */
    public static CommodityClass convertFromDbCodeSafe(String dbCode) {
        for (CommodityClass commodityClass : CommodityClass.values()) {
            if (commodityClass.getDbCode().equals(dbCode)) {
                return commodityClass;
            }
        }
        return null;
    }
}
