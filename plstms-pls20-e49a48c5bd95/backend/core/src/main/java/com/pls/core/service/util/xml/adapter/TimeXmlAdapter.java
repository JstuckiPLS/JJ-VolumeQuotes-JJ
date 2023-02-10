package com.pls.core.service.util.xml.adapter;

import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Xml adapter for time type.
 * 
 * @author Dmitry Nikolaenko
 */
public class TimeXmlAdapter extends XmlAdapter<String, Date> {

    private static final String TIME_PATTERN = "HH:mm:ss";

    @Override
    public Date unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }

        return DateUtils.parseDate(v, TIME_PATTERN);
    }

    @Override
    public String marshal(Date v) throws Exception {
        if (v == null) {
            return null;
        }

        return DateFormatUtils.format(v, TIME_PATTERN, Locale.US);
    }

}
