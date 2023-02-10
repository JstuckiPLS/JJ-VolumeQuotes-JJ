package com.pls.core.domain.sterling.utils;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * Tests the marshaller and unmarshaller functionality of Date Time adapter.
 * 
 * @author Yasaman Honarvar
 *
 */
public class DateTimeAdapterTest {

    private static final String VALUE = "2012/12/20 21:34:54";
    private static final String VALUE_RECV = "12/20/12 21:34:54";

    @Test
    public void testUnmarshaller() throws Exception {
        DateTimeAdapter adapter = new DateTimeAdapter();
        Date date = adapter.unmarshal(VALUE);
        assertEquals(getDate(2012, 11, 20, 21, 34, 54), date);
        date = adapter.unmarshal(VALUE_RECV);
        assertEquals(getDate(2012, 11, 20, 21, 34, 54), date);
        date = adapter.unmarshal("05/12/17 13:08:40");
        assertEquals(getDate(2017, 4, 12, 13, 8, 40), date);
    }

    @Test
    public void testMarshaller() throws Exception {
        DateTimeAdapter adapter = new DateTimeAdapter();
        Date date = getDate(2012, 11, 20, 21, 34, 54);
        String value = adapter.marshal(date);
        assertEquals(value, VALUE);
    }

    private Date getDate(int year, int month, int date, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hourOfDay, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
