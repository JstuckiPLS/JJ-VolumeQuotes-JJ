package com.pls.core.domain.enums;

/**
 * Point type enum for Required Field entity. Can have values - O, D, B.
 * O stands for origin point type, D - for destination point type, B - both.
 *
 * @author Alexnader Nalapko
 */
public enum RequiredFieldPointType {
    ORIGIN('O'), DESTINATION('D'), BOTH('B');

    private char code;

    RequiredFieldPointType(char code) {
        this.code = code;
    }

    /**
     * Get pointType of current enum by char type.
     *
     * @param code pointType to find
     * @return instance of current enum
     */
    public static RequiredFieldPointType getByCode(char code) {
        for (RequiredFieldPointType pointType : values()) {
            if (pointType.code == code) {
                return pointType;
            }
        }

        throw new IllegalArgumentException("Cannot get PointType object by value: '" + code + "'");
    }

    public char getCode() {
        return code;
    }
}
