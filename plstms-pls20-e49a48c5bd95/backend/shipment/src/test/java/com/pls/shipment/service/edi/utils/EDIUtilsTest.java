package com.pls.shipment.service.edi.utils;

import com.pls.shipment.service.impl.edi.utils.EDIUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Test cases for {@link EDIUtils} class.
 *
 * @author Mikhail Boldinov, 11/06/14
 */
public class EDIUtilsTest {

    @Test
    public void testToDate() {
        Assert.assertEquals(getDate(2014, Calendar.JUNE, 11), EDIUtils.toDate("20140611"));
        Assert.assertEquals(getDate(2010, Calendar.FEBRUARY, 2), EDIUtils.toDate("20100202"));
        Assert.assertNull(EDIUtils.toDate("sd20011302a"));
        Assert.assertNull(EDIUtils.toDate(""));
        Assert.assertNull(EDIUtils.toDate(null));
    }

    @Test
    public void testToDateTime() {
        Assert.assertEquals(getDate(2014, Calendar.JUNE, 11, 16, 14, 35), EDIUtils.toDateTime("2014061116143528"));
        Assert.assertEquals(getDate(2014, Calendar.JUNE, 11, 16, 14, 35), EDIUtils.toDateTime("20140611161435"));
        Assert.assertEquals(getDate(2014, Calendar.JUNE, 11, 16, 14, 0), EDIUtils.toDateTime("201406111614"));
        Assert.assertEquals(getDate(2014, Calendar.JUNE, 11, 16, 0, 0), EDIUtils.toDateTime("2014061116"));
        Assert.assertEquals(getDate(2014, Calendar.JUNE, 11), EDIUtils.toDateTime("20140611"));
        Assert.assertNull(EDIUtils.toDateTime(""));
        Assert.assertNull(EDIUtils.toDateTime(null));
    }

    @Test
    public void testToInteger() {
        Assert.assertEquals(new Integer(1), EDIUtils.toInteger("1"));
        Assert.assertEquals(new Integer(325), EDIUtils.toInteger("325"));
        Assert.assertEquals(null, EDIUtils.toInteger("146.45"));
        Assert.assertEquals(null, EDIUtils.toInteger("hello"));
        Assert.assertEquals(null, EDIUtils.toInteger(null));
    }

    @Test
    public void testToBigDecimal() {
        Assert.assertEquals(BigDecimal.ONE, EDIUtils.toBigDecimal("1"));
        Assert.assertEquals(new BigDecimal("126.13"), EDIUtils.toBigDecimal("126.13"));
        Assert.assertEquals(new BigDecimal("-235.547"), EDIUtils.toBigDecimal("-235.547"));
        Assert.assertEquals(null, EDIUtils.toBigDecimal(""));
        Assert.assertEquals(null, EDIUtils.toBigDecimal(null));
    }

    @Test
    public void testToCurrency() {
        Assert.assertEquals(new BigDecimal("0.01").setScale(2), EDIUtils.toCurrency("1", true));
        Assert.assertEquals(new BigDecimal("1").setScale(2), EDIUtils.toCurrency("1", false));
        Assert.assertEquals(new BigDecimal("1.23").setScale(2), EDIUtils.toCurrency("123", true));
        Assert.assertEquals(new BigDecimal("123").setScale(2), EDIUtils.toCurrency("123", false));
        Assert.assertEquals(new BigDecimal("4.87").setScale(2), EDIUtils.toCurrency("487.23", true));
        Assert.assertEquals(new BigDecimal("487.23").setScale(2), EDIUtils.toCurrency("487.23", false));
        Assert.assertEquals(new BigDecimal("700").setScale(2), EDIUtils.toCurrency("70000", true));
        Assert.assertEquals(new BigDecimal("70000").setScale(2), EDIUtils.toCurrency("70000", false));
        Assert.assertNull(EDIUtils.toCurrency("", true));
        Assert.assertNull(EDIUtils.toCurrency("", false));
        Assert.assertNull(EDIUtils.toCurrency(null, true));
        Assert.assertNull(EDIUtils.toCurrency(null, false));
    }

    @Test
    public void testToStr() {
        Assert.assertEquals("0", EDIUtils.toStr(0));
        Assert.assertEquals("1", EDIUtils.toStr(1));
        Assert.assertEquals("1.25", EDIUtils.toStr(1.25));
        Assert.assertEquals("24.36", EDIUtils.toStr(24.36f));
        Assert.assertEquals("12.78", EDIUtils.toStr(12.78d));
        Assert.assertEquals("175.1675", EDIUtils.toStr(175.1675));
        Assert.assertEquals("-234.08", EDIUtils.toStr(-234.08d));
        Assert.assertEquals("118.45", EDIUtils.toStr(new BigDecimal(118.45).setScale(2, RoundingMode.HALF_UP)));
        Assert.assertEquals("212.1000", EDIUtils.toStr(new BigDecimal(212.1).setScale(4, RoundingMode.HALF_UP)));
        Assert.assertEquals("10", EDIUtils.toStr(BigDecimal.TEN));
        Assert.assertEquals("", EDIUtils.toStr(null));
    }

    @Test
    public void testToCurrencyStr() {
        Assert.assertEquals("12.30", EDIUtils.toCurrencyStr(new BigDecimal("12.3"), false));
        Assert.assertEquals("1230", EDIUtils.toCurrencyStr(new BigDecimal("12.3"), true));
        Assert.assertEquals("34.55", EDIUtils.toCurrencyStr(new BigDecimal("34.55"), false));
        Assert.assertEquals("3455", EDIUtils.toCurrencyStr(new BigDecimal("34.55"), true));
        Assert.assertEquals("567.00", EDIUtils.toCurrencyStr(new BigDecimal("567"), false));
        Assert.assertEquals("56700", EDIUtils.toCurrencyStr(new BigDecimal("567"), true));
        Assert.assertEquals("89.95", EDIUtils.toCurrencyStr(new BigDecimal("89.953"), false));
        Assert.assertEquals("8995", EDIUtils.toCurrencyStr(new BigDecimal("89.953"), true));
        Assert.assertEquals("412.77", EDIUtils.toCurrencyStr(new BigDecimal("412.766"), false));
        Assert.assertEquals("41277", EDIUtils.toCurrencyStr(new BigDecimal("412.766"), true));
        Assert.assertEquals("", EDIUtils.toCurrencyStr(null, false));
        Assert.assertEquals("", EDIUtils.toCurrencyStr(null, true));
    }

    @Test
    public void testToDateStr() {
        Assert.assertEquals("20140611", EDIUtils.toDateStr(getDate(2014, Calendar.JUNE, 11)));
        Assert.assertEquals("20140611", EDIUtils.toDateStr(getDate(2014, Calendar.JUNE, 11, 17, 31, 26)));
        Assert.assertEquals("20001231", EDIUtils.toDateStr(getDate(2000, Calendar.DECEMBER, 31, 23, 59, 59)));
        Assert.assertEquals("20010101", EDIUtils.toDateStr(getDate(2001, Calendar.JANUARY, 1, 0, 0, 0)));
        Assert.assertNull(EDIUtils.toDateStr(null));
    }

    @Test
    public void testToTimeStr() {
        Assert.assertEquals("0000", EDIUtils.toTimeStr(getDate(2014, Calendar.JUNE, 11)));
        Assert.assertEquals("1731", EDIUtils.toTimeStr(getDate(2014, Calendar.JUNE, 11, 17, 31, 26)));
        Assert.assertEquals("2359", EDIUtils.toTimeStr(getDate(2000, Calendar.DECEMBER, 31, 23, 59, 59)));
        Assert.assertEquals("0000", EDIUtils.toTimeStr(getDate(2001, Calendar.JANUARY, 1, 0, 0, 0)));
        Assert.assertNull(EDIUtils.toTimeStr(null));
    }

    @Test
    public void testToDateTimeStr() {
        Assert.assertEquals("20140611000000", EDIUtils.toDateTimeStr(getDate(2014, Calendar.JUNE, 11)));
        Assert.assertEquals("20140611173126", EDIUtils.toDateTimeStr(getDate(2014, Calendar.JUNE, 11, 17, 31, 26)));
        Assert.assertEquals("20001231235959", EDIUtils.toDateTimeStr(getDate(2000, Calendar.DECEMBER, 31, 23, 59, 59)));
        Assert.assertEquals("20010101000000", EDIUtils.toDateTimeStr(getDate(2001, Calendar.JANUARY, 1, 0, 0, 0)));
        Assert.assertNull(EDIUtils.toDateTimeStr(null));
    }

    @Test
    public void testGetCurrentDateStr() {
        Pattern pattern = Pattern.compile("2\\d{3}[01]\\d[0123]\\d");
        Matcher matcher = pattern.matcher(EDIUtils.getCurrentDateStr());
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void testGetCurrentDateShortStr() {
        Pattern pattern = Pattern.compile("\\d{2}[01]\\d[0123]\\d");
        Matcher matcher = pattern.matcher(EDIUtils.getCurrentDateShortStr());
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void testGetCurrentTimeStr() {
        Pattern pattern = Pattern.compile("[012]\\d[012345]\\d");
        Matcher matcher = pattern.matcher(EDIUtils.getCurrentTimeStr());
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void testRefineStr() {
        Assert.assertEquals("HelloWorld", EDIUtils.refineString("'Hello World!!!'", " ", "'", "!"));
        Assert.assertEquals("testRefineStr", EDIUtils.refineString("t e s t    Refine____ S't'r%", " ", "_", "%", "'"));
        Assert.assertEquals("assertEquals", EDIUtils.refineString("assertEquals.", "."));
        Assert.assertEquals("Assert", EDIUtils.refineString("Assert"));
    }

    @Test
    public void testReplaceLineFeed() {
        Assert.assertEquals("London is the capital of Great Britain", EDIUtils.replaceLineFeed("London\nis the capital\nof Great\nBritain"));
    }

    @Test
    public void testAdjustLength() {
        Assert.assertEquals("Is this the real life?", EDIUtils.adjustLength("Is this the real life?", 100));
        Assert.assertEquals("Is this just fantasy", EDIUtils.adjustLength("Is this just fantasy?", 20));
        Assert.assertEquals("I'm just a poor", EDIUtils.adjustLength("I'm just a poor boy, nobody loves me", 15));
        Assert.assertEquals("Easy come,", EDIUtils.adjustLength("Easy come, easy go, will you let me go?", 10));
        Assert.assertEquals(5, EDIUtils.adjustLength("Anyone can see", 5).length());
        Assert.assertEquals("", EDIUtils.adjustLength("Nothing really matters to me", 0));
        Assert.assertNull(EDIUtils.adjustLength("Anyway the wind blows.", -42));
        Assert.assertNull(EDIUtils.adjustLength(null, 2));
    }

    @Test
    public void testElement() {
        Assert.assertEquals("", EDIUtils.element(0));
        Assert.assertEquals(" ", EDIUtils.element(1));
        Assert.assertEquals("          ", EDIUtils.element(10));
        Assert.assertEquals("", EDIUtils.element(0, " "));
        Assert.assertEquals("**", EDIUtils.element(2, "*"));
        Assert.assertEquals("=====", EDIUtils.element(5, "="));
        Assert.assertEquals("alpha     ", EDIUtils.element("alpha", 10));
        Assert.assertEquals("beta ", EDIUtils.element("beta", 5));
        Assert.assertEquals("gam", EDIUtils.element("gamma", 3));
        Assert.assertEquals("delta     ", EDIUtils.element("delta", 10, false));
        Assert.assertEquals("   epsilon", EDIUtils.element("epsilon", 10, true));
        Assert.assertEquals("ze", EDIUtils.element("zeta", 2, true));
        Assert.assertEquals("eta+++++++", EDIUtils.element("eta", 10, "+"));
        Assert.assertEquals("the", EDIUtils.element("theta", 3, "+"));
        Assert.assertEquals("iota......", EDIUtils.element("iota", 10, ".", false));
        Assert.assertEquals(",,,,,kappa", EDIUtils.element("kappa", 10, ",", true));
        Assert.assertEquals("lam", EDIUtils.element("lambda", 3, ";", true));
        Assert.assertEquals("mu", EDIUtils.element("mu", 2, 10));
        Assert.assertEquals("nu    ", EDIUtils.element("nu", 6, 10));
        Assert.assertEquals("xi ", EDIUtils.element("xi", 3, 3));
        Assert.assertEquals("omicro", EDIUtils.element("omicron", 5, 6));
        Assert.assertEquals("pi  ", EDIUtils.element("pi", 4, 8, false));
        Assert.assertEquals("  rho", EDIUtils.element("rho", 5, 6, true));
        Assert.assertEquals("sigma", EDIUtils.element("sigma", 1, 10, true));
        Assert.assertEquals("t", EDIUtils.element("tau", 1, 1, true));
        Assert.assertEquals("upsil", EDIUtils.element("upsilon", 4, 5, "'", true));
        Assert.assertEquals("~~~phi", EDIUtils.element("phi", 6, 6, "~", true));
        Assert.assertEquals("chi````", EDIUtils.element("chi", 7, 10, "`", false));
        Assert.assertEquals("ps", EDIUtils.element("psi", 1, 2, " ", true));
        Assert.assertEquals("omega", EDIUtils.element("omega", 3, 8, "-", true));
    }

    private static Date getDate(int year, int month, int day) {
        return getDate(year, month, day, 0, 0, 0);
    }

    private static Date getDate(int year, int month, int day, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hours, minutes, seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
