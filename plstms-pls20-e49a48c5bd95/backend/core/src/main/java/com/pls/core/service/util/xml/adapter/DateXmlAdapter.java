package com.pls.core.service.util.xml.adapter;

import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Xml adapter for date type.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class DateXmlAdapter extends XmlAdapter<String, Date> {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Override
    public Date unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }

        return DateUtils.parseDate(v, DATE_PATTERN);
    }

    @Override
    public String marshal(Date v) throws Exception {
        if (v == null) {
            return null;
        }

        return DateFormatUtils.format(v, DATE_PATTERN, Locale.US);
    }
}
