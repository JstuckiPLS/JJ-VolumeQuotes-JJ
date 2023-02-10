package com.pls.ltlrating.domain.enums;


/**
 * Type of Geo Code.
 *
 * @author Pavani Challa
 */
public enum GeoType {
    ORIGIN(1), DESTINATION(2);

    private int type;

    GeoType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    /**
     * Get Geo Type of current enum by type.
     *
     * @param type GeoType to find
     * @return instance of current enum
     */
    public static GeoType getGeoTypeBy(int type) {
        for (GeoType geoType : values()) {
            if (geoType.type == type) {
                return geoType;
            }
        }

        throw new IllegalArgumentException(String.format("Cannot get GeoType object by type:%s", type));
    }

}
