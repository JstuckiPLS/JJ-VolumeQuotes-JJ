package com.pls.core.common.utils;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link DateUtility}.
 * 
 * @author Artem Arapov
 *
 */
public class DateUtilityTest {

    @Test
    public void testPositionalDate() throws Exception {
        Calendar expectedCalendar = Calendar.getInstance();
        expectedCalendar.set(Calendar.YEAR, 2013);
        expectedCalendar.set(Calendar.MONTH, 8);
        expectedCalendar.set(Calendar.DATE, 16);

        Date actualDate = DateUtility.stringToDate("20130916", DateUtility.REVERSE_POSITIONAL_DATE);
        Calendar actualCalendar = Calendar.getInstance();
        actualCalendar.setTime(actualDate);

        Assert.assertNotNull(actualDate);
        Assert.assertEquals(expectedCalendar.get(Calendar.YEAR), actualCalendar.get(Calendar.YEAR));
        Assert.assertEquals(expectedCalendar.get(Calendar.MONTH), actualCalendar.get(Calendar.MONTH));
        Assert.assertEquals(expectedCalendar.get(Calendar.DATE), actualCalendar.get(Calendar.DATE));
    }
}
