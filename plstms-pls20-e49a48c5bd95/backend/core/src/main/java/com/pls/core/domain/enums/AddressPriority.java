package com.pls.core.domain.enums;

/**
 * Enumeration for address priority. The highest priority when least value.
 * 
 * @author Brichak Aleksandr
 */
public enum AddressPriority {

    EMPTY_ADDRESS(0),
    ORIGIN_ZIP(1),
    DESTINATION_ZIP(2),
    ORIGIN_CITY(3),
    DESTINATION_CITY(4),
    ORIGIN_STATE(5),
    DESTINATION_STATE(6),
    ORIGIN_COUNTRY(7),
    DESTINATION_COUNTRY(8);

    private int priority;

    public int getPriority() {
        return priority;
    }

    AddressPriority(int priority) {
        this.priority = priority;
    }
}
