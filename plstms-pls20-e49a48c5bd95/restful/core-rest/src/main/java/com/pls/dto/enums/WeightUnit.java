package com.pls.dto.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Weight Unit Enum.
 *
 * @author Gleb Zgonikov
 */
public enum WeightUnit {
    LBS("Lbs"),
    KG("Kg");

    /**
     * Returns list of all weight units.
     *
     * @return list of the {@link WeightUnit}
     */
    public static List<WeightUnit> getList() {
        return Arrays.asList(WeightUnit.values());
    }

    private String label;

    WeightUnit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
