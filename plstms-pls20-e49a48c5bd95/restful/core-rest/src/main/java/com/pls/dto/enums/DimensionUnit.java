package com.pls.dto.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Dimension Unit Enum.
 *
 * @author Gleb Zgonikov
 */
public enum DimensionUnit {
    INCH("Inch"),
    CMM("Cm");

    /**
     * Returns list of all dimension units.
     *
     * @return list of the {@link DimensionUnit}
     */
    public static List<DimensionUnit> getList() {
        return Arrays.asList(DimensionUnit.values());
    }

    private String label;

    DimensionUnit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
