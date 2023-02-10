package com.pls.dto.enums;

/**
 * Days of week Enum.
 *
 * @author Gleb Zgonikov
 */
public enum DayOfWeek {

    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private String label;

    DayOfWeek(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
