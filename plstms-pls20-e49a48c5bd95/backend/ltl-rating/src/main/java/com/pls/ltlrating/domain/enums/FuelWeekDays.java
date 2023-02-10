package com.pls.ltlrating.domain.enums;


/**
 * Fuel Week Days list.
 *
 * @author Hima Bindu Challa
 */
public enum FuelWeekDays {
    MON("Monday"),
    TUE("Tuesday"),
    WED("Wednesday"),
    THU("Thursday"),
    FRI("Friday"),
    SAT("Saturday"),
    SUN("Sunday"),
    MON_1("Last week Monday"),
    TUE_1("Last week Tuesday"),
    WED_1("Last week Wednesday"),
    THU_1("Last week Thursday"),
    FRI_1("Last week Friday"),
    SAT_1("Last week Saturday"),
    SUN_1("Last week Sunday");

    private String description;

    FuelWeekDays(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
