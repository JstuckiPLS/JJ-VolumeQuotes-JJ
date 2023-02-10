package com.pls.core.domain.enums;

import java.util.Calendar;

/**
 * Days of the week.
 * 
 * @author Stas Norochevskiy
 *
 */
public enum WeekDays {
    Monday("MON", Calendar.MONDAY),
    Tuesday("TUE", Calendar.TUESDAY),
    Wednesday("WED", Calendar.WEDNESDAY),
    Thursday("THU", Calendar.THURSDAY),
    Friday("FRI", Calendar.FRIDAY),
    Saturday("SAT", Calendar.SATURDAY),
    Sunday("SUN", Calendar.SUNDAY);

    private String dayCode;

    private int calendarCode;

    WeekDays(String dayCode, int calendarCode) {
        this.dayCode = dayCode;
        this.calendarCode = calendarCode;
    }

    /**
     * Return week day by day code.
     * 
     * @param dayCode day code 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'
     * @return WeekDays object
     */
    public static WeekDays getWeekDayByCode(String dayCode) {
        for (WeekDays day : values()) {
            if (day.dayCode.equals(dayCode)) {
                return day;
            }
        }

        throw new IllegalArgumentException("Cannot get day of week object by day code: '" + dayCode + "'");
    }

    /**
     * Return week day by calendar week day.
     *
     * @param calendarCode calendar week day code from 1 to 7 where 1 is Sunday and 7 is Saturday
     * @return {@link WeekDays}
     */
    public static WeekDays getWeekDayByCalendarWeekDay(int calendarCode) {
        for (WeekDays day : values()) {
            if (day.calendarCode == calendarCode) {
                return day;
            }
        }

        throw new IllegalArgumentException("Cannot get day of week object by calendar day of week: '" + calendarCode + "'");
    }

    public String getWeekDayCode() {
        return dayCode;
    }

    public int getCalendarCode() {
        return calendarCode;
    }

    @Override
    public String toString() {
        return dayCode;
    }
}
