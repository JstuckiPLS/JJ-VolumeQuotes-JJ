package com.pls.ltlrating.domain.enums;

/**
 * Mileage name enumeration.
 *
 * @author Mikhail Boldinov, 26/02/13
 */
public enum MileageType {
    MILE_MAKER("MileMaker"), PC_MILER("PC Miler");

    private String name;

    MileageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Get {@link MileageType} by {@link MileageType#name} value.
     *
     * @param name {@link MileageType#name} value.
     * @return Not <code>null</code> {@link MileageType}. {@link IllegalArgumentException} is thrown if not found.
     */
    public static MileageType getByName(String name) {
        if (name != null) {
            for (MileageType type : MileageType.values()) {
                if (type.name.equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException("Cannot get MileageType object by type name: '" + name + "'");
    }
}
