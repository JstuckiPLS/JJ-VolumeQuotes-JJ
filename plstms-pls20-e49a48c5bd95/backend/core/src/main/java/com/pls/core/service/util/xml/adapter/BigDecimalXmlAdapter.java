package com.pls.core.service.util.xml.adapter;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * BigDecimal xml adapter.
 *
 * @author Denis Zhupinsky (Team International)
 */
public class BigDecimalXmlAdapter extends XmlAdapter<String, BigDecimal> {
    @Override
    public String marshal(BigDecimal value) throws Exception {
        if (value != null) {
            return String.format("%.2f", value);
        }
        return null;
    }

    @Override
    public BigDecimal unmarshal(String s) throws Exception {
        return new BigDecimal(s);
    }
}
