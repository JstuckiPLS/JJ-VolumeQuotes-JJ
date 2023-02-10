package com.pls.ltlrating.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import com.pls.core.common.utils.DateUtility;
import com.pls.ltlrating.domain.enums.FuelWeekDays;

/**
 *  Key is day of week and value is the publish date. Day of week starts with "1" as Sunday and "7" as Saturday.
 *  Below is the output when I printed those dates.

    Pickup    |   Sunday  |   Monday         |   Tuesday        | Wednesday | Thursday  |   Friday  | Saturday
    -----------------------------------------------------------------------------------------------------------
    Sunday    | Last Week | Last Before Week | Last Before Week | Last Week | Last Week | Last Week | Last Week
    Monday    | Last Week | Last Week        | Last Before Week | Last Week | Last Week | Last Week | Last Week
    Tuesday   | Last Week | Last Week        | Last Week        | Last Week | Last Week | Last Week | Last Week
    Wednesday | Last Week | Last Week        | Last Week        | This Week | Last Week | Last Week | Last Week
    Thursday  | Last Week | Last Week        | Last Week        | This Week | This Week | Last Week | Last Week
    Friday    | Last Week | Last Week        | Last Week        | This Week | This Week | This Week | Last Week
    Saturday  | Last Week | Last Week        | Last Week        | This Week | This Week | This Week | This Week

 * @author Pavani Challa
 *
 */
public final class FuelSurchargeHelper {

    private FuelSurchargeHelper() {

    }

    /**
     * Calculates the published dates for pickup dates with every day of week as effective day.
     *
     * @param pickupDate
     *            pickup date for which published dates are to be calculated
     * @return the published dates for each day of week as a map
     */
    public static Map<FuelWeekDays, Date> getPublishedDates(Date pickupDate) {
        Map<FuelWeekDays, Date> publishDates = new HashMap<FuelWeekDays, Date>();

        for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
            publishDates.put(getDayOfWeek(dayOfWeek), getPublishedDate(pickupDate, dayOfWeek, false));
        }
        for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
            publishDates.put(getPrevDayOfWeek(dayOfWeek), getPublishedDate(pickupDate, dayOfWeek, true));
        }
        return publishDates;
    }

    private static FuelWeekDays getDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
        case Calendar.MONDAY:
            return FuelWeekDays.MON;
        case Calendar.TUESDAY:
            return FuelWeekDays.TUE;
        case Calendar.WEDNESDAY:
            return FuelWeekDays.WED;
        case Calendar.THURSDAY:
            return FuelWeekDays.THU;
        case Calendar.FRIDAY:
            return FuelWeekDays.FRI;
        case Calendar.SATURDAY:
            return FuelWeekDays.SAT;
        default:
            return FuelWeekDays.SUN;
        }
    }

    private static FuelWeekDays getPrevDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
        case Calendar.MONDAY:
            return FuelWeekDays.MON_1;
        case Calendar.TUESDAY:
            return FuelWeekDays.TUE_1;
        case Calendar.WEDNESDAY:
            return FuelWeekDays.WED_1;
        case Calendar.THURSDAY:
            return FuelWeekDays.THU_1;
        case Calendar.FRIDAY:
            return FuelWeekDays.FRI_1;
        case Calendar.SATURDAY:
            return FuelWeekDays.SAT_1;
        default:
            return FuelWeekDays.SUN_1;
        }
    }

    private static Date getPublishedDate(Date pickupDate, int dayOfWeek, boolean prevWeek) {
        /* The logic followed for calculated published date is as follows. First, we get the day of the week for the effective day.
         If the effective day is Wednesday, we get the date for Wednesday during the week of pickup date and we call that
         effective date (not day) of the week. Then we check if the pickup day is earlier than effective day for that week.
         If it is earlier than effective date, we move one weeks back to the effective date and get the Monday before that date.
         If the pickup date is same as effective date or the week or later than that, then we get the effective date
         of the week and get the Monday before that date. */

        // Get the date of the week as per the Effective day. Clear out the time as it is not needed.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pickupDate);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        Date effectiveDateOfWeek = DateUtility.getStartDateOfToday(calendar.getTime());
        Date finalMonday = null;

        if (prevWeek) {
            // Check if pickup date is earlier than effective date of the week.
            if (pickupDate.getTime() < effectiveDateOfWeek.getTime()) {
                finalMonday = getMondayOfPreviousWeek(effectiveDateOfWeek, -14);
            } else {
                finalMonday = getMondayOfPreviousWeek(effectiveDateOfWeek, -7);
            }
        } else {
            // Check if pickup date is earlier than effective date of the week.
            if (pickupDate.getTime() < effectiveDateOfWeek.getTime()) {
                finalMonday = getMondayOfPreviousWeek(effectiveDateOfWeek, -7);
            } else {
                finalMonday = getMondayOfPreviousWeek(effectiveDateOfWeek, 0);
            }
        }

        //Since the dates we retrieve and check is only "date" piece and not "time" piece, we should exclude time.
        while (DateUtility.getStartDateOfToday(finalMonday).compareTo(DateUtility.getStartDateOfToday(new Date())) >= 0) {
            finalMonday = getMondayOfPreviousWeek(finalMonday, -7);
        }

        return finalMonday;
    }

    private static Date getMondayOfPreviousWeek(Date effectiveDateOfWeek, int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(effectiveDateOfWeek);
        calendar.add(Calendar.DATE, daysToAdd);
        Long dayOfPreviousWeek = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        // If the day of week is on or before monday, get the monday of previous week
        if (dayOfPreviousWeek <= calendar.getTimeInMillis()) {
            calendar.add(Calendar.DATE, -7);
        }
        Date mondayDate = calendar.getTime();

        if (DateUtility.rollToBeginningOfDay(mondayDate).compareTo(DateUtility.rollToBeginningOfDay(new Date())) >= 0) {
            DateUtils.addDays(mondayDate, -7);
        }
        return mondayDate;
    }

}
