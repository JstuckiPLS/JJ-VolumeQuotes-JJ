/**
 *
 */
package com.pls.core.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date util class.
 *
 * @author Hima Bindu Challa
 */
public final class DateUtility {


    private static final int SEVEN_DAYS_PER_WEEK = 7;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtility.class);

    private DateUtility() {

    }

    /**
     * Represents a hyphen-delimted date, of the form "MM-dd-yyyy".
     * @see java.text.SimpleDateFormat
     **/
    public static final String HYPENATED_DATE = "MM-dd-yyyy";

    /**
     * Represents a slash-delimited date, of the form "MM/dd/yyyy".
     * @see java.text.SimpleDateFormat
     **/
    public static final String SLASHED_DATE = "MM/dd/yyyy";

    /**
     * Represents a positional date, of the form ddMMyyyy".
     * @see java.text.SimpleDateFormat
     **/
    public static final String POSITIONAL_DATE = "ddMMyyyy";

    public static final String REVERSE_POSITIONAL_DATE = "yyyyMMdd";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat();

    static {
        SIMPLE_DATE_FORMAT.setLenient(false);
    }


    /**
     * For the given date, returns a date object with the same month/day/year, but
     * sets the time to be the hour and minute provided, IE 08:30.
     * @param date day to be adjusted
     * @param hour the absolute hour of the day (24 hour), IE 08 or 17
     * @param minute the abosulte min of the hour, IE 15, 30 or 45
     * @return date with the specified time set
     */
    public static Date rollToTimeOfDay(Date date, int hour, int minute) {

        Date requestedDate = null;

        if (date != null) {

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);

            requestedDate = cal.getTime();
        }

        return requestedDate;
    }

    public static Calendar getCalenderInstance() {
        return Calendar.getInstance();
    }


    /**
     * Convert a <code>String</code> into a <code>Date</code>.
     * This method does not allow for lenient Date conversions. The following
     * example would throw an exception:
     * <p><code>
     *  DateUtility.stringToDate( "32012006", "ddMMyyyy");
     * </code>
     * @param date A string representing a date.
     * @param format A string containing a simple date format, as defined
     * in {@link java.text.SimpleDateFormat}.
     * @return A Date object corresponding to the date argument.
     * @exception ParseException when a Date can't be extracted
     * from the date parameter using the given format.
     **/
    public static Date stringToDate(final String date, final String format) throws ParseException {

        synchronized (date) {
            return DateUtils.parseDateStrictly(date, format);
        }

    }


    /**
     * Convert a date to specified format.
     *
     * @param date to be formatted.
     * @param format format string, non null, Specifications
     * can be found in {@link java.text.SimpleDateFormat}.
     * @return a <code>Date</code> value

     */
    public static String dateToString(final Date date, final String format) {
        if (date != null) {
            synchronized (date) {
                SIMPLE_DATE_FORMAT.applyPattern(format);
                return SIMPLE_DATE_FORMAT.format(date);
            }
        } else {
            return "";
        }
    }

    /**
     * Return the int representing Calendar.DAY_OF_WEEK for the given date.
     * @param date - date
     * @return - day of week
     */
    public static int getDayOfWeek(Date date) {
        if (date == null) {
            return -1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Find the instance of the parameter day that occurs some number of weeks in the future or past. Does not account for business days.
     * @param today - basis for defining future or past
     * @param calendarWeekday such as Calendar.Monday
     * @param numberOfWeeks in future or past, zero for this week
     * @return instance of the requested day, occurring next week
     */
    public static Date getSomeDayInSomeWeek(Date today, int calendarWeekday, int numberOfWeeks) {

        Date requestedDate = null;

        if (today != null) {

            Calendar cal = Calendar.getInstance();
            cal.setTime(today);

            //be careful here and convert to the enumerated values
            if (Calendar.SUNDAY == calendarWeekday) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

            } else if (Calendar.MONDAY == calendarWeekday) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            } else if (Calendar.TUESDAY == calendarWeekday) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

            } else if (Calendar.WEDNESDAY == calendarWeekday) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);

            } else if (Calendar.THURSDAY == calendarWeekday) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);

            } else if (Calendar.FRIDAY == calendarWeekday) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

            } else if (Calendar.SATURDAY == calendarWeekday) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

            } else {
                return null;  //garbage in garbage out
            }

            //calculate the offset
            int numberOfDays = SEVEN_DAYS_PER_WEEK * numberOfWeeks;

            cal.add(Calendar.DAY_OF_MONTH, numberOfDays);


            requestedDate = cal.getTime();
        }

        return requestedDate;
    }


    /**
     * Return whether the specified date is a week day, M-F.
     * @param date - date
     * @return true if the date is M-F, false if it is Saturday or Sunday.
     */
    public static boolean isWeekDay(Date date) {
        int dayOfWeek = getDayOfWeek(date);
        if (dayOfWeek == -1) {
            // It's null, so it's not a weekday..
            return false;
        }
        return dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY;
    }


    /**
     * Calculates the X occurrence of a particular day with a month.  This
     * is useful for finding holiday dates that are derived from an
     * X day of a month.  Eg, thanksgiving is the 4th Thursday in November.
     *
     * @param dayOfWeek - day of week
     * @param occurrence - occurence
     * @param month - month
     * @param year - year
     * @return date
     */
    public static Date xDayOfMonth(int dayOfWeek, int occurrence, int month, int year) {
        int calculatedDay = dayOfWeek;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (calculatedDay < currentDayOfWeek) {
            calculatedDay += SEVEN_DAYS_PER_WEEK;
        }
        cal.add(Calendar.DAY_OF_MONTH, calculatedDay - currentDayOfWeek + ((occurrence - 1) * SEVEN_DAYS_PER_WEEK));
        return cal.getTime();
    }

    /**
     * Calculates the X occurrence of a particular day with a month working backwards.
     * This is useful for finding holiday dates that are derived from an
     * X day of a month.  Eg, memorial is the last Monday in May.
     *
     * @param dayOfWeek - day of week
     * @param occurrence - occurrence
     * @param month - month
     * @param year - year
     * @return date
     */
    public static Date xDayOfMonthBackwards(int dayOfWeek, int occurrence, int month, int year) {
        int calculatedDay = dayOfWeek;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month + 1);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (calculatedDay > currentDayOfWeek) {
            calculatedDay -= SEVEN_DAYS_PER_WEEK;
        }
        cal.add(Calendar.DAY_OF_MONTH, calculatedDay - currentDayOfWeek - ((occurrence - 1) * SEVEN_DAYS_PER_WEEK));
        return cal.getTime();
    }

    /**
     * Add the specified number of days to the specified date.
     * If negative days are entered, it will roll the
     * dates in the past.  If 0 days are entered, it will return the same
     * date.
     * @param date date to add
     * @param days days to add
     * @return a new Date object
     * @throws IllegalArgumentException if date is null.
     */
    public static Date addDays(Date date, int days) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }
    
    /**
     * Converts the specified date to UTC timezone
     * @param date date to convert
     * @return a new Date object
     * @throws IllegalArgumentException if date is null.
     */
    public static Date timeToUTC(Date date) throws IllegalArgumentException {
        if (date == null) {
        	Exception e = new IllegalArgumentException("Date cannot be null");
        	LOGGER.error("Date cannot be null", e);
            throw new IllegalArgumentException("Date cannot be null");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        TimeZone tz = cal.getTimeZone();
        cal.add(Calendar.HOUR_OF_DAY, tz.getOffset(date.getTime()) / (60 * 60 * 1000));
        cal.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
        LOGGER.warn("Time zone converted to UTC time");

        return cal.getTime();
    }

    /**
     * Method truncates milliseconds from date.
     * @param date - date to clear.
     * @return cleared date.
     */
    public static Date truncateMilliseconds(Date date) {
        // Hibernate injects java.sql.Timestamp into date fields. This is why we should to convert all custom
        // dated to provide valid behavior of equals() method.
        if (date != null) {
            return new Timestamp(DateUtils.truncate(date, Calendar.SECOND).getTime());
        } else {
            return null;
        }
    }

    /**
     * Get date for start of day based on received date.
     * 
     * @param date
     *            date to base on
     * @return start of day date
     */
    public static Date getStartDateOfToday(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(new Date());

        if (date != null) {
            calendar.setTime(date);
        }
        DateUtility.setTimeToBeginingOfTheDay(calendar);

        return calendar.getTime();
    }

    private static void setTimeToBeginingOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
    *Return whether the specified date is a weekend, Saturday or Sunday, or a
    *holiday.
    * 
    *@param date - the date that needs to be checked.
    *@return true if the date is Saturday, Sunday or a holiday.
    *
    */
    public static boolean isWeekendOrHoliday(Date date) {
        if (date == null) {
            // It's null, so it's not a weekend or holiday..
            return false;
        }
        if (!isWeekDay(date)) {
            return true;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        List<Date> holidays = getHolidays(year);
        for (Date holiday : holidays) {
            if (DateUtils.isSameDay(holiday, date)) {
                return true;
            }
        }

        return false;
    }

    /**
    * Returns List<Date> of holidays for a given year.
    * @param year - the year to get list of holidays.
    * @return List of holidays.
    */
    public static List<Date> getHolidays(int year) {
        List<Date> holidays = new ArrayList<Date>();
        holidays.add(getNewYearsDay(year));
        holidays.add(getMemorialDay(year));
        holidays.add(getIndependenceDay(year));
        holidays.add(getLaborDay(year));
        holidays.add(getThanksgivingDay(year));
        holidays.add(getThanksgivingNextDay(year));
        holidays.add(getChristmasDay(year));
        return holidays;
    }

    /**
    * Get new year day.
    * @param year - the year to get new year.
    * @return Date - new year date.
    */
    public static Date getNewYearsDay(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * Get memorial day date.
     * @param year - the year to get memorial day.
     * @return Date - memorial day date.
     */
    public static Date getMemorialDay(int year) {
        return xDayOfMonthBackwards(Calendar.MONDAY, 1, Calendar.MAY, year);
    }

    /**
     * Get Independence day date.
     * @param year - the year to get independence day.
     * @return Date - independence day date.
     */
    public static Date getIndependenceDay(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.JULY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 4);
        return cal.getTime();
    }

    /**
     * Get labor day date.
     * @param year - the year to get labor day.
     * @return Date - labor day date.
     */
    public static Date getLaborDay(int year) {
        return xDayOfMonth(Calendar.MONDAY, 1, Calendar.SEPTEMBER, year);
    }

    /**
     * Get thanks giving day date.
     * @param year - the year to get thanks giving day.
     * @return Date - thanks giving day date.
     */
    public static Date getThanksgivingDay(int year) {
        return xDayOfMonth(Calendar.THURSDAY, 4, Calendar.NOVEMBER, year);
    }

    /**
     * Get thanks giving  next day date.
     * @param year - the year to get thanks giving next day.
     * @return Date - thanks giving next day date.
     */
    public static Date getThanksgivingNextDay(int year) {
        Date thanksGivingDay = xDayOfMonth(Calendar.THURSDAY, 4, Calendar.NOVEMBER, year);
        return DateUtils.addDays(thanksGivingDay, 1);
    }

    /**
     * Get christmas day date.
     * @param year - the year to get christmas day.
     * @return Date - christmas day date.
     */
    public static Date getChristmasDay(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 25);
        return cal.getTime();
    }

    /**
     * For the given date, returns a date object with the same month/day/year, but
     * sets the time to the beginning of the day, 00:00.
     * @param date - the date that needs to be truncated.
     * @return truncated date.
     */
    public static Date rollToBeginningOfDay(Date date) {
        return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }
}
