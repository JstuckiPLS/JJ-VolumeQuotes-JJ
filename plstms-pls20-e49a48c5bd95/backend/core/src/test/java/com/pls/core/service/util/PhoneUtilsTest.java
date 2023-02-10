package com.pls.core.service.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.bo.PhoneBO;

/**
 * Test for {@link PhoneUtils}.
 * 
 * @author Aleksandr Leshchenko
 */
public class PhoneUtilsTest {
    private static final Map<String, String> VALID_PHONES = new HashMap<String, String>();
    static {
        VALID_PHONES.put("+1 (123) 1234567", "+1(123)123 4567");
        VALID_PHONES.put("+1 (123) 1234567   Ext.:  1234", "+1(123)123 4567  Ext.: 1234");
        VALID_PHONES.put("+1     (123)    1234567", "+1(123)123 4567");
        VALID_PHONES.put("+1     (123)    1234567      Ext.:     1234", "+1(123)123 4567  Ext.: 1234");
        VALID_PHONES.put("+1(123)1234567", "+1(123)123 4567");
        VALID_PHONES.put("+1(123)1234567Ext.:123", "+1(123)123 4567  Ext.: 123");
        VALID_PHONES.put("+1(123)123 4567", "+1(123)123 4567");
        VALID_PHONES.put("+123(1)1234567", "+123(1)123 4567");
        VALID_PHONES.put("+123(1)1234567Ext.:12345", "+123(1)123 4567  Ext.: 12345");
        VALID_PHONES.put("1(123)123 4567", "+1(123)123 4567");
        VALID_PHONES.put("123(123)1234567", "+123(123)123 4567");
        VALID_PHONES.put("+1(123) 123-4567", "+1(123)123 4567");
        VALID_PHONES.put(" + 1 ( 1 2 3 ) 1 2 3   4 5 6 7 ", "+1(123)123 4567");
        VALID_PHONES.put(" (123) 1234567 ", "+1(123)123 4567");
        VALID_PHONES.put("123-123-4567", "+1(123)123 4567");
        VALID_PHONES.put("123-123-4567 Ext.:123456", "+1(123)123 4567  Ext.: 123456");
        VALID_PHONES.put("12-123-4567", "+1(12)123 4567");
        VALID_PHONES.put("1-123-4567", "+1(1)123 4567");
        VALID_PHONES.put("11234567", "+1(1)123 4567");
        VALID_PHONES.put("1-234-567-8901", "+1(234)567 8901");
        VALID_PHONES.put("234-567-8901", "+1(234)567 8901");
        VALID_PHONES.put("123.456.7890", "+1(123)456 7890");
        VALID_PHONES.put("123.456.7890 Ext.:12", "+1(123)456 7890  Ext.: 12");
        VALID_PHONES.put("123 456 7890", "+1(123)456 7890");
        VALID_PHONES.put("1234567890", "+1(123)456 7890");
        VALID_PHONES.put("1.123.456.7890", "+1(123)456 7890");
        VALID_PHONES.put("1-123-456-7890", "+1(123)456 7890");
        VALID_PHONES.put("1 123 456 7890", "+1(123)456 7890");
        VALID_PHONES.put("11234567890", "+1(123)456 7890");
        VALID_PHONES.put("(1) (234) 567-8901", "+1(234)567 8901");
        VALID_PHONES.put("(1) (234) 567-8901 Ext.:1", "+1(234)567 8901  Ext.: 1");
        VALID_PHONES.put("262.785.3518", "+1(262)785 3518");
        VALID_PHONES.put("800-555-5555", "+1(800)555 5555");
        VALID_PHONES.put("330 395 3490", "+1(330)395 3490");
        VALID_PHONES.put("(440)439-4000", "+1(440)439 4000");
        VALID_PHONES.put("(440)439-4000 Ext.:123", "+1(440)439 4000  Ext.: 123");
        VALID_PHONES.put("(800) 264-7014", "+1(800)264 7014");
        VALID_PHONES.put("*8179462525", "+1(817)946 2525");
        VALID_PHONES.put("*8179462525Ext.:12345", "+1(817)946 2525  Ext.: 12345");
        VALID_PHONES.put("*12814490777", "+1(281)449 0777");
        VALID_PHONES.put("*12814490777Ext.:123456", "+1(281)449 0777  Ext.: 123456");
    }
    private static final String[] INVALID_PHONES = new String[]{
            "+1(1)12345678",        // 8 digits in phone number section
            "+1 (123) 123/4567",    // invalid character
            "1234567",              // just phone number
            "",                      // empty phone
            "+1(1)1234567Ext.:1234567", // 7 digits in phone extension section
            "+1(1)1234567 123456" // extension does not precede with "Ext.:"
    };

    @Test
    public void shouldFormatValidPhones() {
        for (Entry<String, String> phone : VALID_PHONES.entrySet()) {
            Assert.assertEquals(phone.getValue(), PhoneUtils.formatPhoneNumber(phone.getKey()));
        }
    }

    @Test
    public void shouldParsePhoneAndSetSpecifiedCountryCode() {
        PhoneBO parse = PhoneUtils.parse("123-124-3567", "010");
        Assert.assertEquals("010", parse.getCountryCode());
        Assert.assertEquals("123", parse.getAreaCode());
        Assert.assertEquals("1243567", parse.getNumber());
    }

    @Test
    public void shouldLeaveInvalidPhonesAsTheyAre() {
        for (String phone : INVALID_PHONES) {
            Assert.assertEquals(phone, PhoneUtils.formatPhoneNumber(phone));
        }
    }

    @Test
    public void shouldNotParseEmptyPhones() {
        Assert.assertEquals("", PhoneUtils.formatPhoneNumber("  "));
        Assert.assertEquals("", PhoneUtils.formatPhoneNumber(null));
    }
}
