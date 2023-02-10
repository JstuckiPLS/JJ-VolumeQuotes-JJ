package com.pls.core.domain.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Applicable To value.
 * 
 * @author Artem Arapov
 * 
 */
public enum ApplicableToUnit {

    ALL("All"), LTL("Ltl"), PLS("PLS");

    private String description;

    ApplicableToUnit(String description) {
        this.description = description;
    }

    public static List<ApplicableToUnit> getList() {
        return Arrays.asList(ApplicableToUnit.values());
    }

    public String getDescription() {
        return this.description;
    }
}
