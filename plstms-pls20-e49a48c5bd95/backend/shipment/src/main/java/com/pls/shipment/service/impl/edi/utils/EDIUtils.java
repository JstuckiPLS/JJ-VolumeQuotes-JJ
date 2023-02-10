package com.pls.shipment.service.impl.edi.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * EDI utilities.
 *
 * @author Mikhail Boldinov, 28/08/13
 */
public final class EDIUtils {

    public static final String[] PHONE_FORBIDDEN_SYMBOLS = {" ", "+", "-", "(", ")"};

    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final String SHORT_DATE_PATTERN = "yyMMdd";
    private static final String TIME_PATTERN = "HHmm";
    private static final String DATETIME_PATTERN = "yyyyMMddHHmmss";

    private EDIUtils() {
    }

    /**
     * Converts element value to {@link Date}.
     *
     * @param element element value
     * @return Date object
     */
    public static Date toDate(String element) {
        try {
            return StringUtils.isNotBlank(element) ? DateUtils.parseDate(element, DATE_PATTERN) : null;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Converts element value to {@link Date} using date-time pattern.
     * <p/>
     * Given element value is parsed using 'yyyyMMddHHmmss' therefore it's expected that element value contains exactly 14 digits.
     * If there are less than 14 digits, assume that some parts of time is omitted and therefore populate them with '0'.
     * If there are more than 14 digits, only first 14 are taken into account.
     * <p/>
     * E.g.
     * <ul>
     *     <li>"20140611" - 11 Jun 2014 00:00:00</li>
     *     <li>"2014061116" - 11 Jun 2014 16:00:00</li>
     *     <li>"201406111614" - 11 Jun 2014 16:14:00</li>
     *     <li>"20140611161435" - 11 Jun 2014 16:14:35</li>
     *     <li>"2014061116143528" - 11 Jun 2014 16:14:35</li>
     * </ul>
     *
     * @param element element value
     * @return Date object
     */
    public static Date toDateTime(String element) {
        try {
            return StringUtils.isNotBlank(element)
                    ? DateUtils.parseDate(StringUtils.left(StringUtils.rightPad(element, 14, '0'), 14), DATETIME_PATTERN) : null;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Converts element value to {@link Integer}.
     *
     * @param element element value
     * @return Integer object
     */
    public static Integer toInteger(String element) {
        try {
            return Integer.parseInt(element);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Converts element value to {@link BigDecimal}.
     *
     * @param element element value
     * @return BigDecimal object
     */
    public static BigDecimal toBigDecimal(String element) {
        if (StringUtils.isNotBlank(element)) {
            return new BigDecimal(element);
        }
        return null;
    }

    /**
     * Converts element value to currency value.
     *
     * @param element element value
     * @param cents indicates whether the value has cents and should be converted to dollars or not.
     * @return BigDecimal currency
     */
    public static BigDecimal toCurrency(String element, boolean cents) {
        BigDecimal amount = toBigDecimal(element);
        if (amount != null) {
            if (cents) {
                amount = amount.divide(new BigDecimal(100));
            }
            amount = amount.setScale(2, RoundingMode.HALF_UP);
        }
        return amount;
    }

    /**
     * String value of provided number.
     *
     * @param number number to convert to string
     * @return string representation of number
     */
    public static String toStr(Number number) {
        return number == null ? "" : String.valueOf(number);
    }

    /**
     * String value of provided currency value.
     * <p/>
     * If cents is <code>true</code> the currency value will be represented in cents.
     * E.g. 720.42 will be a "72042", 123.40 will be a "12340", 567 will be a "56700"
     *
     * @param value currency value to convert to string
     * @param cents flag which indicates whether the currency should be converted into cents representation or not
     * @return string representation of currency value
     */
    public static String toCurrencyStr(BigDecimal value, boolean cents) {
        String result = "";
        if (value != null) {
            result = cents ? value.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).toString()
                    : value.setScale(2, RoundingMode.HALF_UP).toString();
        }
        return result;
    }

    /**
     * Formats date as yyyyMMdd.
     *
     * @param date date to be formatted
     * @return formatted date
     */
    public static String toDateStr(Date date) {
        return formatDate(date, DATE_PATTERN);
    }

    /**
     * Formats time as HHmm.
     *
     * @param date time to be formatted
     * @return formatted time
     */
    public static String toTimeStr(Date date) {
        return formatDate(date, TIME_PATTERN);
    }

    /**
     * Formats date and time as yyyyMMddHHmmss.
     *
     * @param date date to be formatted
     * @return formatted date and
     */
    public static String toDateTimeStr(Date date) {
        return formatDate(date, DATETIME_PATTERN);
    }

    /**
     * Returns current date in format yyyyMMdd.
     *
     * @return current date's string representation
     */
    public static String getCurrentDateStr() {
        return formatDate(new Date(), DATE_PATTERN);
    }

    /**
     * Returns current date in format yyMMdd.
     *
     * @return current date's string representation
     */
    public static String getCurrentDateShortStr() {
        return formatDate(new Date(), SHORT_DATE_PATTERN);
    }

    /**
     * Returns current time in format HHmm.
     *
     * @return current time's string representation
     */
    public static String getCurrentTimeStr() {
        return formatDate(new Date(), TIME_PATTERN);
    }

    private static String formatDate(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
            return format.format(date);
        }
        return null;
    }

    /**
     * Removes all unnecessary characters from provided string.
     *
     * @param value string to refine
     * @param chars characters to be replaced
     * @return refined string
     */
    public static String refineString(String value, String... chars) {
        String result = value;
        if (result != null) {
            for (String character : chars) {
                result = result.replace(character, "");
            }
        }
        return result;
    }

    /**
     * Replaces all LF characters (char(10), '\n') with a whitespace.
     *
     * @param value string to clear the linefeed
     * @return replaced string
     */
    public static String replaceLineFeed(String value) {
        String result = value;
        if (result != null) {
            result = result.replace("\n", " ");
        }
        return result;
    }

    /**
     * Truncates provided value in case it exceeds max allowed length.
     *
     * @param value     string to truncate
     * @param maxLength max allowed length
     * @return truncated string if its length exceeds max allowed length, otherwise initial string
     */
    public static String adjustLength(String value, int maxLength) {
        try {
            return value != null && value.length() > maxLength ? value.substring(0, maxLength) : value;
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Creates a string which contains specified number of whitespaces.
     *
     * @param length string length
     * @return fixed length string
     */
    public static String element(int length) {
        return element(null, length);
    }

    /**
     * Creates a string which contains specified number of fillWith characters.
     *
     * @param length string length
     * @param fillWith a character to populate string with
     * @return fixed length string
     */
    public static String element(int length, String fillWith) {
        return element(null, length, fillWith);
    }

    /**
     * Joins initial value with whitespace characters until the string has specified length.
     *
     * @param value  initial value
     * @param length string length
     * @return fixed length string
     */
    public static String element(String value, int length) {
        return element(value, length, length);
    }

    /**
     * Joins initial value with whitespace characters until the string has specified length.
     *
     * @param value  initial value
     * @param length string length
     * @param leading   indicates whether the fillWith character is leading
     * @return fixed length string
     */
    public static String element(String value, int length, boolean leading) {
        return element(value, length, length, leading);
    }

    /**
     * Joins initial value with fillWith characters until the string has specified length.
     *
     * @param value    initial value
     * @param length   string length
     * @param fillWith a character to populate string with
     * @return fixed length string
     */
    public static String element(String value, int length, String fillWith) {
        return element(value, length, length, fillWith, false);
    }

    /**
     * Joins initial value with fillWith characters until the string has specified length.
     *
     * @param value    initial value
     * @param length   string length
     * @param fillWith a character to populate string with
     * @param leading   indicates whether the fillWith character is leading
     * @return fixed length string
     */
    public static String element(String value, int length, String fillWith, boolean leading) {
        return element(value, length, length, fillWith, leading);
    }

    /**
     * Returns initial value if its length is between provided Min and Max lengths.
     * If initial value length is less than allowed Min length, joins initial value with whitespace characters until the string has specified length.
     *
     * @param value     initial value
     * @param minLength string min length
     * @param maxLength string max length
     * @return fixed length string
     */

    public static String element(String value, int minLength, int maxLength) {
        return element(value, minLength, maxLength, " ", false);
    }

    /**
     * Returns initial value if its length is between provided Min and Max lengths.
     * If initial value length is less than allowed Min length, joins initial value with whitespace characters until the string has specified length.
     *
     * @param value     initial value
     * @param minLength string min length
     * @param maxLength string max length
     * @param leading   indicates whether the fillWith character is leading
     * @return fixed length string
     */

    public static String element(String value, int minLength, int maxLength, boolean leading) {
        return element(value, minLength, maxLength, " ", leading);
    }


    /**
     * Returns initial value if its length is between provided Min and Max lengths.
     * If initial value length is less than allowed Min length, joins initial value with fillWith characters until the string has specified length.
     *
     * @param value     initial value
     * @param minLength string min length
     * @param maxLength string max length
     * @param fillWith  a character to populate string with
     * @param leading   indicates whether the fillWith character is leading
     * @return fixed length string
     */
    public static String element(String value, int minLength, int maxLength, String fillWith, boolean leading) {
        if (minLength < 0 || maxLength < 0) {
            throw new IllegalArgumentException(String.format("Min length '%s' and max length '%s' cannot be less than 0.", minLength, maxLength));
        }
        if (minLength > maxLength) {
            throw new IllegalArgumentException(String.format("Min length '%s' cannot be greater than max length '%s'.", minLength, maxLength));
        }
        StringBuilder result = value == null ? new StringBuilder() : new StringBuilder(value);
        if (result.length() > maxLength) {
            result = new StringBuilder(result.substring(0, maxLength));
        }
        while (result.length() < minLength) {
            if (leading) {
                result.insert(0, fillWith);
            } else {
                result.append(fillWith);
            }
        }
        return result.toString();
    }

    /**
     * Removes leading and trailing whitespace characters in file.
     * 
     * @param file
     *            to trim
     * @return {@link InputStream} where all leading and trailing whitespace characters are removed
     * @throws IOException
     *             in case of any issue with file
     */
    public static InputStream trimFile(File file) throws IOException {
        StringBuilder strBuffer = new StringBuilder();
        char[] cbuf = new char[1024];
        int length = -1;

        Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        do {
            length = reader.read(cbuf);
            if (length != -1) {
                strBuffer.append(cbuf, 0, length);
            }
        } while (length != -1);
        reader.close();

        return new ByteArrayInputStream(strBuffer.toString().trim().getBytes());
    }
}
