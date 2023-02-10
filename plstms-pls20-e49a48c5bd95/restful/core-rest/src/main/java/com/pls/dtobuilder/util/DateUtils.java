package com.pls.dtobuilder.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pls.dto.enums.DateRange;

/**
 * Utility class which is used for formatting dates on REST layer.<br>
 * Date must be passed with timezone.
 * 
 * @author Aleksandr Leshchenko
 */
public final class DateUtils {
    /**
     * Cactory to obtain current date and time.
     * 
     * @author Maxim Medvedev
     */
    public interface CalendarProvider {
        /**
         * Get {@link Calendar} valuue that represent current date and time.
         * 
         * @return Not <code>null</code> {@link Calendar};
         */
        Calendar getNow();
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd Z";

    public static final String DATE_FORMAT_WITHOUT_TIMEZONE = "yyyy-MM-dd";

    public static final CalendarProvider DEFAULT_PROVIDER = new CalendarProvider() {

        @Override
        public Calendar getNow() {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTime(new Date());
            return calendar;
        }
    };
    public static final String TIME_FORMAT = "h:mm a";
    private static CalendarProvider calendarProvider = DEFAULT_PROVIDER;
    private static final int FIRST_QUARTER = 1;
    private static final int FOURTH_QUARTER = 4;

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);
    private static final int MONTH_IN_QUARTER = 3;
    private static final String NULL_PARAM = "null";

    private static final int SECCOND_QUARTER = 2;

    private static final int THIRD_QUARTER = 3;

    /**
     * Get string representation of date in {@link DateUtils#DATE_FORMAT} format.
     * 
     * @param date
     *            to be formatted.
     * @return string representation of date
     */
    public static String formatDate(Date date) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date);
    }

    /**
     * Get date for end of month based on received date.
     * 
     * @param date
     *            date to base on
     * @return end of month date
     */
    public static Date getEndDateOfMonth(Date date) {
        Calendar calendar = DateUtils.getCalendarForNow();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        DateUtils.setTimeToEndOfDate(calendar);

        return calendar.getTime();
    }

    /**
     * Get date for end of day based on received date.
     * 
     * @param date
     *            date to base on
     * @return end of day date
     */
    public static Date getEndDateOfToday(Date date) {
        Calendar calendar = DateUtils.getCalendarForNow();
        if (date != null) {
            calendar.setTime(date);
        }
        DateUtils.setTimeToEndOfDate(calendar);

        return calendar.getTime();
    }

    /**
     * Get date for end of week based on received date.
     * 
     * @param date
     *            date to base on
     * @return end of week date
     */
    public static Date getEndDateOfWeek(Date date) {
        Calendar calendar = DateUtils.getCalendarForNow();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        DateUtils.setTimeToEndOfDate(calendar);

        return calendar.getTime();
    }

    /**
     * Get start of date range based on current date and {@link DateRange}.
     * 
     * @param dateRange
     *            date range parameter.
     * @param fromDate
     *            used in case of {@link DateRange#DEFAULT}
     * @return {@link Date}
     */
    public static Date getFromDate(DateRange dateRange, String fromDate) {
        switch (dateRange) {
        case YEAR:
            return DateUtils.getStartDateOfYear();
        case MONTH:
            return DateUtils.getStartDateOfMonth();
        case WEEK:
            return DateUtils.getStartDateOfWeek();
        case TODAY:
            return DateUtils.getStartDateOfToday();
        case QUARTER:
            return DateUtils.getStartDateOfQuarter();
        case DEFAULT:
            return DateUtils.getDateFrom(DateUtils.prepareQueryValue(fromDate));
        default:
            throw new IllegalArgumentException("Unexpected '" + dateRange + "' time range");
        }
    }

    /**
     * Get date for start of month based on received date.
     * 
     * @param date
     *            date to base on
     * @return start of month date
     */
    public static Date getStartDateOfMonth(Date date) {
        Calendar calendar = DateUtils.getCalendarForNow();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        DateUtils.setTimeToBeginingOfTheDay(calendar);

        return calendar.getTime();
    }

    /**
     * Get date for start of day based on received date.
     * 
     * @param date
     *            date to base on
     * @return start of day date
     */
    public static Date getStartDateOfToday(Date date) {
        Calendar calendar = DateUtils.getCalendarForNow();
        if (date != null) {
            calendar.setTime(date);
        }
        DateUtils.setTimeToBeginingOfTheDay(calendar);

        return calendar.getTime();
    }

    /**
     * Get date for start of week based on received date.
     * 
     * @param date
     *            date to base on
     * @return start of week date
     */
    public static Date getStartDateOfWeek(Date date) {
        Calendar calendar = DateUtils.getCalendarForNow();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        DateUtils.setTimeToBeginingOfTheDay(calendar);

        return calendar.getTime();
    }

    /**
     * Get end of date range based on current date and {@link DateRange}.
     * 
     * @param dateRange
     *            date range parameter.
     * @param toDate
     *            used in case of {@link DateRange#DEFAULT}
     * @return {@link Date}
     */
    public static Date getToDate(DateRange dateRange, String toDate) {
        switch (dateRange) {
        case YEAR:
            return DateUtils.getEndDateOfYear();
        case MONTH:
            return DateUtils.getEndDateOfMonth();
        case WEEK:
            return DateUtils.getEndDateOfWeek();
        case TODAY:
            return DateUtils.getEndDateOfToday();
        case QUARTER:
            return DateUtils.getEndDateOfQuarter();
        case DEFAULT:
            return DateUtils.getDateTo(DateUtils.prepareQueryValue(toDate));
        default:
            throw new IllegalArgumentException("Unexpected '" + dateRange + "' time range");
        }
    }

    /**
     * Parses date using {@link DateUtils#DATE_FORMAT} pattern.
     * 
     * @param date
     *            string to be parsed - must be in {@link DateUtils#DATE_FORMAT} format.
     * @return {@link Date} object.
     */
    public static Date parseDate(String date) {
        return DateUtils.parseDate(date, DATE_FORMAT);
    }

    /**
     * Parses date using provided pattern.
     * 
     * @param date
     *            string to be parsed
     * @param pattern
     *            used to parse date
     * @return {@link Date} object.
     */
    public static Date parseDate(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            LOGGER.error("error parsing date " + date, e);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get parsed date accordingly to specified format .
     * 
     * @param date
     *            to be formatted - must be in {@link DateUtils#DATE_FORMAT_WITHOUT_TIMEZONE} format.
     * @return {@link Date} object.
     */
    public static Date parseDateWithoutTimeZone(String date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_WITHOUT_TIMEZONE, Locale.getDefault());
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            LOGGER.error("error parsing date " + date, e);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Parses time using {@link DateUtils#TIME_FORMAT} pattern.
     * 
     * @param time
     *            string to be parsed - must be in {@link DateUtils#TIME_FORMAT} format.
     * @return {@link Date} object.
     */
    public static Date parseTime(String time) {
        return DateUtils.parseDate(time, TIME_FORMAT);
    }

    public static void setCalendarProvider(CalendarProvider pCalendarProvider) {
        calendarProvider = pCalendarProvider == null ? DEFAULT_PROVIDER : pCalendarProvider;
    }

    private static Calendar getCalendarForNow() {
        return calendarProvider.getNow();
    }

    /**
     * Get beginning of the Date from date string.
     *
     * @param dateString date string
     * @return date beginning
     */
    public static Date getDateFrom(String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        }
        Date date = DateUtils.parseDateWithoutTimeZone(dateString);
        Calendar calendar = DateUtils.getCalendarForNow();
        calendar.setTime(date);
        DateUtils.setTimeToBeginingOfTheDay(calendar);
        return calendar.getTime();
    }

    /**
     * Get end of the Date from date string.
     *
     * @param dateString date string
     * @return date end
     */
    public static Date getDateTo(String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        }
        Date date = DateUtils.parseDateWithoutTimeZone(dateString);
        Calendar calendar = DateUtils.getCalendarForNow();
        calendar.setTime(date);
        DateUtils.setTimeToEndOfDate(calendar);
        return calendar.getTime();
    }

    private static Date getEndDateOfMonth() {
        return DateUtils.getEndDateOfMonth(null);
    }

    private static Date getEndDateOfQuarter() {
        Calendar calendar = DateUtils.getCalendarForNow();
        int quarter = calendar.get(Calendar.MONTH) / MONTH_IN_QUARTER + 1;

        switch (quarter) {
        case FIRST_QUARTER:
            calendar.set(Calendar.MONTH, Calendar.MARCH);
            break;
        case SECCOND_QUARTER:
            calendar.set(Calendar.MONTH, Calendar.JUNE);
            break;
        case THIRD_QUARTER:
            calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
            break;
        case FOURTH_QUARTER:
            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
            break;
        default:
            break;
        }

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        DateUtils.setTimeToEndOfDate(calendar);
        return calendar.getTime();
    }

    private static Date getEndDateOfToday() {
        return DateUtils.getEndDateOfToday(null);
    }

    private static Date getEndDateOfWeek() {
        return DateUtils.getEndDateOfWeek(null);
    }

    private static Date getEndDateOfYear() {
        Calendar calendar = DateUtils.getCalendarForNow();
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        DateUtils.setTimeToEndOfDate(calendar);

        return calendar.getTime();
    }

    private static Date getStartDateOfMonth() {
        return DateUtils.getStartDateOfMonth(null);
    }

    private static Date getStartDateOfQuarter() {
        Calendar calendar = DateUtils.getCalendarForNow();
        int quarter = calendar.get(Calendar.MONTH) / MONTH_IN_QUARTER + 1;

        switch (quarter) {
        case FIRST_QUARTER:
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            break;
        case SECCOND_QUARTER:
            calendar.set(Calendar.MONTH, Calendar.APRIL);
            break;
        case THIRD_QUARTER:
            calendar.set(Calendar.MONTH, Calendar.JULY);
            break;
        case FOURTH_QUARTER:
            calendar.set(Calendar.MONTH, Calendar.OCTOBER);
            break;
        default:
            break;
        }

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        DateUtils.setTimeToBeginingOfTheDay(calendar);
        return calendar.getTime();
    }

    private static Date getStartDateOfToday() {
        return DateUtils.getStartDateOfToday(null);
    }

    private static Date getStartDateOfWeek() {
        return DateUtils.getStartDateOfWeek(null);
    }

    private static Date getStartDateOfYear() {
        Calendar calendar = DateUtils.getCalendarForNow();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        DateUtils.setTimeToBeginingOfTheDay(calendar);

        return calendar.getTime();
    }

    private static String prepareQueryValue(String value) {
        if (value != null) {
            String queryValue = value;
            if (StringUtils.containsIgnoreCase(queryValue, NULL_PARAM)) {
                queryValue = StringUtils.remove(queryValue, NULL_PARAM);
            }

            return StringUtils.defaultIfBlank(queryValue, null);
        }
        return value;
    }

    private static void setTimeToBeginingOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndOfDate(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
    }

    private DateUtils() {
    }
}
