package com.pls.core.domain.enums;

/**
 * Point type enum for load details entity. Can have values - O, D, S,  P (seems P will not be used).
 * O stands for origin point type, D - for destination point type.
 *
 * @author Denis Zhupinsky (TEAM International)
 */
public enum PointType {
    ORIGIN('O'), DESTINATION('D'), STOP_OFF('S'), P('P');

    private char type;

    PointType(char type) {
        this.type = type;
    }

    /**
     * Get pointType of current enum by char type.
     *
     * @param type pointType to find
     * @return instance of current enum
     */
    public static PointType getPointTypeBy(char type) {
        for (PointType pointType : values()) {
            if (pointType.type == type) {
                return pointType;
            }
        }

        throw new IllegalArgumentException("Cannot get PointType object by value: '" + type + "'");
    }

    public char getPointType() {
        return type;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }
}
