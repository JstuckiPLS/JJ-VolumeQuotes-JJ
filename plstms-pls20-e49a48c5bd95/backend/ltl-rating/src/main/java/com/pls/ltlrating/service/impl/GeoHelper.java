package com.pls.ltlrating.service.impl;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Helper class to determine the geo service type of a geoValue.<br>
 * <ul>
 * <li>If the geo value is a country code, then the geo service type is 7.</li>
 * <li>if the geo value is a state code, then the geo service type is 6.</li>
 * <li>If the geo value is a range with 3 digit zip codes (for eg., 343-364), then the geo service type is 4.</li>
 * <li>If the geo value is a range with 5 digit zip codes (for eg., 34334-36456), then the geo service type is 3.</li>
 * <li>If the geo value is 3 digits of the zip code, then the geo service type is 2.</li>
 * <li>If the geo value is the 5 digit zip code, the geo service type is 1.</li>
 * <li>Otherwise the geo service type is 5.</li>
 * </ul>
 *
 * @author Pavani Challa
 */
public final class GeoHelper {
    public static final String FIVE_DIGITS_RANGE = "^(\\d{5})-(\\d{5})$";

    private GeoHelper() {
    }

    /**
     * Calculates the geo service type for the geo value passed.
     *
     * @param geoValue
     *            country code/state code/zip code/zip range
     * @return the geo service type for the geo value.
     */
    public static Pair<Integer, String> getGeoServType(String geoValue) {
        String geoDetail = geoValue.replace(" ", "");
        int geoServiceTypeCode = getGeoServiceTypeCode(geoDetail);
        if (geoServiceTypeCode < 5 && isNotNumeric(geoDetail)) {
            geoDetail = getGeoCodeSearchable(geoDetail);
        }
        return Pair.of(geoServiceTypeCode, geoDetail);
    }

    private static int getGeoServiceTypeCode(String geoDetail) {
        if (isCountryCode(geoDetail)) {
            return 7;
        } else if (isStateCode(geoDetail)) {
            return 6;
        } else if (isShortZipRange(geoDetail)) {
            return 4;
        } else if (isZipRange(geoDetail)) {
            return 3;
        } else if (isShortZipCode(geoDetail)) {
            return 2;
        } else if (isZipCode(geoDetail)) {
            return 1;
        }
        return 5;
    }

    private static boolean isZipCode(String geoDetail) {
        return isFiveDigitZipCode(geoDetail) || isSixSymbolsZipCode(geoDetail);
    }

    private static boolean isShortZipCode(String geoDetail) {
        return isThreeDigitZipCode(geoDetail) || isThreeSymbolsZipCode(geoDetail);
    }

    private static boolean isZipRange(String geoDetail) {
        return isFiveDigitZipRange(geoDetail) || isSixSymbolsZipRange(geoDetail);
    }

    private static boolean isShortZipRange(String geoDetail) {
        return isThreeDigitZipRange(geoDetail) || isThreeSymbolsZipRange(geoDetail);
    }

    private static boolean isSixSymbolsZipCode(String geoDetail) {
        return geoDetail.length() == 6 && geoDetail.matches("^[a-zA-Z&&[^DFIOQUWZdfioquwz]]\\d([a-zA-Z&&[^DFIOQUdfioqu]]\\d){2}$");
    }

    /**
     * Check if specified geoDetail is a five digit zip code, like "12345".
     * 
     * @param geoDetail
     *            geo detail to check
     * @return <code>true</code> if it is. <code>false</code> otherwise.
     */
    public static boolean isFiveDigitZipCode(String geoDetail) {
        return geoDetail.length() == 5 && geoDetail.matches("^\\d{5}$");
    }

    private static boolean isThreeSymbolsZipCode(String geoDetail) {
        return geoDetail.length() == 3 && geoDetail.matches("^[a-zA-Z&&[^DFIOQUWZdfioquwz]]\\d[a-zA-Z&&[^DFIOQUdfioqu]]$");
    }

    private static boolean isThreeDigitZipCode(String geoDetail) {
        return geoDetail.length() == 3 && geoDetail.matches("^\\d{3}$");
    }

    private static boolean isSixSymbolsZipRange(String geoDetail) {
        return geoDetail.length() == 13 && geoDetail.matches("^[a-zA-Z&&[^DFIOQUWZdfioquwz]]\\d([a-zA-Z&&[^DFIOQUdfioqu]]\\d){2}"
                + "-[a-zA-Z&&[^DFIOQUWZdfioquwz]]\\d([a-zA-Z&&[^DFIOQUdfioqu]]\\d){2}$");
    }

    /**
     * Check if specified geoDetail is a five digit zip range, like "12345-12358".
     * 
     * @param geoDetail
     *            geo detail to check
     * @return <code>true</code> if it is. <code>false</code> otherwise.
     */
    public static boolean isFiveDigitZipRange(String geoDetail) {
        return geoDetail.length() == 11 && geoDetail.matches(FIVE_DIGITS_RANGE);
    }

    private static boolean isThreeSymbolsZipRange(String geoDetail) {
        return geoDetail.length() == 7 && geoDetail.matches("^[a-zA-Z&&[^DFIOQUWZdfioquwz]]\\d[a-zA-Z&&[^DFIOQUdfioqu]]"
                + "-[a-zA-Z&&[^DFIOQUWZdfioquwz]]\\d[a-zA-Z&&[^DFIOQUdfioqu]]$");
    }

    private static boolean isThreeDigitZipRange(String geoDetail) {
        return geoDetail.length() == 7 && geoDetail.matches("^\\d{3}-\\d{3}$");
    }

    private static boolean isStateCode(String geoDetail) {
        return geoDetail.length() == 2 && geoDetail.matches("^[a-zA-Z]{2}$");
    }

    private static boolean isCountryCode(String geoDetail) {
        return "USA".equalsIgnoreCase(geoDetail) || "MEX".equalsIgnoreCase(geoDetail) || "CAN".equalsIgnoreCase(geoDetail);
    }

    private static boolean isNotNumeric(String geoDetail) {
        return geoDetail.matches(".*[a-zA-Z].*");
    }

    /**
     * Converts the given geo value into its equivalent ASCII value.
     * @param geoDetail - geo value with empty space removed.
     * @return geoCodeSearchable - string containing the ASCII equivalent of the geo code.
     */
    private static String getGeoCodeSearchable(String geoDetail) {
        StringBuilder geoCodeSearchable = new StringBuilder();
        char[] letters = geoDetail.toCharArray();
        for (char ch : letters) {
            if (ch == '-') {
                geoCodeSearchable.append(ch);
                continue;
            }
            geoCodeSearchable.append((int) ch);
        }
        return geoCodeSearchable.toString();
    }
}
