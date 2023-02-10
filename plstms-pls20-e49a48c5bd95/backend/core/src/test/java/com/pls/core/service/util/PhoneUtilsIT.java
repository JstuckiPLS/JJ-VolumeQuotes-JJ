package com.pls.core.service.util;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.pls.core.dao.AbstractDaoTest;

/**
 * Test for {@link PhoneUtils}.
 * 
 * @author Aleksandr Leshchenko
 */
public class PhoneUtilsIT extends AbstractDaoTest {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+\\d{1,3}\\(\\d{1,3}\\)\\d{3} \\d{4}$");

    @SuppressWarnings("unchecked")
    @Test
    public void shouldParseAllPhonesAndFaxesInTheApplication() {
        List<String> phones = getSession().createSQLQuery("select CONTACT_PHONE from LOAD_DETAILS union select CONTACT_FAX from LOAD_DETAILS")
                .list();
        for (String phone : phones) {
            if (phone != null) {
                Assert.assertTrue("Phone doesn't match pattern: " + phone, PHONE_PATTERN.matcher(PhoneUtils.formatPhoneNumber(phone)).matches());
            }
        }
        Assert.assertTrue(phones.size() > 0);
    }
}
