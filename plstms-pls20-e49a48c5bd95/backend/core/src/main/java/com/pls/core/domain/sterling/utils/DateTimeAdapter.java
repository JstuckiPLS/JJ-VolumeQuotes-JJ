package com.pls.core.domain.sterling.utils;

import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Date and Time adapter for conversion from and to xml.
 *
 * @author Yasaman Palumbo
 */

public class DateTimeAdapter extends XmlAdapter<String, Date> {
    private static final String DATE_PATTERN_DEF = "yyyy/MM/dd HH:mm:ss";
    private static final String DATE_PATTERN_RECV = "MM/dd/yy HH:mm:ss";

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}");

    @Override
    public Date unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }
        if (DATE_PATTERN.matcher(v).matches()) {
            return DateUtils.parseDate(v, DATE_PATTERN_RECV);
        } else {
            return DateUtils.parseDate(v, DATE_PATTERN_DEF);
        }
    }

    @Override
    public String marshal(Date v) throws Exception {
        if (v == null) {
            return null;
        }
        return DateFormatUtils.format(v, DATE_PATTERN_DEF, Locale.US);
    }
}