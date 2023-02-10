package com.pls.core.service.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.pls.core.domain.PhoneNumber;
import com.pls.core.domain.bo.PhoneBO;

/**
 * Utility for processing phones.
 *
 * @author Mikhail Boldinov, 03/06/13
 */
public final class PhoneUtils {
    private static final Map<Pattern, int[]> PATTERNS = new HashMap<Pattern, int[]>();
    static {
        PATTERNS.put(Pattern.compile("\\+?(\\d{1,3})(\\((\\d{1,3})\\))(\\d{7})"), new int[] { 1, 3, 4, -1}); // usual format
        PATTERNS.put(Pattern.compile("(\\((\\d{1,3})\\))(\\d{7})"), new int[] { -1, 2, 3, -1 }); // no country code format
        PATTERNS.put(Pattern.compile("(\\d{1,3})(\\d{7})"), new int[] { -1, 1, 2, -1 }); // phone in format used for terminal contact
        PATTERNS.put(Pattern.compile("\\+?(\\d{1,3})(\\((\\d{1,3})\\))(\\d{7})(Ext:\\d{1,6})"),
                new int[] { 1, 3, 4, 5 }); // usual format with extension
        PATTERNS.put(Pattern.compile("(\\((\\d{1,3})\\))(\\d{7})(Ext:\\d{1,6})"),
                new int[] { -1, 2, 3, 4 }); // no country code format with extension
        PATTERNS.put(Pattern.compile("(\\d{1,3})(\\d{7})(Ext:\\d{1,6})"),
                new int[] { -1, 1, 2, 3 }); // phone in format used for terminal contact with extension
        PATTERNS.put(Pattern.compile("(\\d{1,3}|\\(\\d{1,3}\\)).*(\\d{3}).*(\\d{7})"), new int[] { 1, 2, 3, -1 });
        PATTERNS.put(Pattern.compile("(\\d{1,3}|\\(\\d{1,3}\\)).*(\\d{3}).*(\\d{7})(Ext:\\d{1,6})"),
                new int[] { 1, 2, 3, 4});
    }

    private PhoneUtils() {
    }

    /**
     * Formats phone entity into string representation using the following pattern.
     * <br/>
     * {@code +ccc(aaa)nnn nnnn  Ext: eeeeee}, where
     * <li> c - Country Code character</li>
     * <li> a - Area Code character</li>
     * <li> n - Phone number character</li>
     * <li> e - Extension number character</li>
     * <br/>
     * NOTE: all leading zeros in country code are removed
     *
     * @param phone {@link PhoneNumber}
     * @return formatted string
     */
    public static String format(PhoneNumber phone) {
        StringBuilder result = new StringBuilder();
        if (phone != null) {
            String countryCode;
            try {
                countryCode = String.valueOf(Integer.valueOf(phone.getCountryCode()));
            } catch (NumberFormatException e) {
                countryCode = phone.getCountryCode();
            }
            String areaCode = phone.getAreaCode();
            String phoneNumber = phone.getNumber();
            if (StringUtils.isNotEmpty(countryCode)) {
                result.append('+').append(countryCode);
            }
            if (StringUtils.isNotEmpty(areaCode)) {
                result.append(" (").append(areaCode).append(") ");
            }
            if (StringUtils.isNotEmpty(phoneNumber)) {
                if (StringUtils.isNotEmpty(areaCode)) {
                    result.append(StringUtils.substring(phoneNumber, 0, 3)).append('-').append(StringUtils.substring(phoneNumber, 3));
                } else {
                    result.append(phoneNumber);
                }
            }
            if (StringUtils.isNotEmpty(phone.getExtension())) {
                result.append("  Ext.: ").append(phone.getExtension());
            }
        }
        return result.toString();
    }

    /**
     * Static method parses string and returns new instance of {@link PhoneBO} where country code equals to
     * countryCode parameter value.
     * 
     * @param phoneString
     *            string to parse
     * @param countryCode
     *            country code to be set at response
     * @return new instance of {@link PhoneBO}
     */
    public static PhoneBO parse(String phoneString, String countryCode) {
        PhoneBO phone = parse(phoneString);
        phone.setCountryCode(countryCode);
        return phone;
    }

    /**
     * Static method parses string and returns new instance of {@link PhoneBO}.
     * 
     * @param phoneString
     *            string to parse
     * @return new instance of {@link PhoneBO}
     */
    public static PhoneBO parse(String phoneString) {
        PhoneBO phoneDTO = new PhoneBO();
        if (StringUtils.isNotBlank(phoneString)) {
            String phone = phoneString.replaceAll("[\\s-.*]", "");
            for (Entry<Pattern, int[]> pattern : PATTERNS.entrySet()) {
                Matcher matcher = pattern.getKey().matcher(phone);
                if (matcher.matches()) {
                    if (pattern.getValue()[0] != -1) {
                        /*
                         * If the phoneString is formatted like so: (1) (234) 567-8901, the extracted country code
                         * will be surrounded by parenthesis. Calling extractDigits just keeps the digits of
                         * the country code and discards anything else.
                         */
                        phoneDTO.setCountryCode(extractDigits(matcher.group(pattern.getValue()[0])));
                    } else {
                        phoneDTO.setCountryCode("1");
                    }
                    if (pattern.getValue()[1] != -1) {
                        phoneDTO.setAreaCode(matcher.group(pattern.getValue()[1]));
                    }
                    if (pattern.getValue()[2] != -1) {
                        phoneDTO.setNumber(matcher.group(pattern.getValue()[2]));
                    }
                    if (pattern.getValue()[3] != -1) {
                        phoneDTO.setExtension(matcher.group(pattern.getValue()[3]).replace("Ext:", ""));
                    }
                    return phoneDTO;
                }
            }
            phoneDTO.setNumber(phoneString);
        }
        return phoneDTO;
    }

    /**
     * Formats phone number to "+ccc(aaa)nnn nnnn" format.
     * 
     * @param contactPhone
     *            phone number
     * @return phone number in "+ccc(aaa)nnn nnnn" format if parameter can be parsed,
     *         else returns original parameter value.
     */
    public static String formatPhoneNumber(String contactPhone) {
        return format(parse(contactPhone));
    }


    private static String extractDigits(String input) {
        return input.replaceAll("[^0-9]", "");
    }

}
