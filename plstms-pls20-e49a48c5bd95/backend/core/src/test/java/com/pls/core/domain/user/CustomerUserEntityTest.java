package com.pls.core.domain.user;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.pls.core.domain.enums.PhoneType;
import com.pls.core.shared.Status;

/**
 * Test cases for {@link CustomerUserEntity} class.
 * 
 * @author Maxim Medvedev
 */
public class CustomerUserEntityTest {

    @Test
    public void testFindFirstActivePhoneWithEmptyRecords() {
        CustomerUserEntity sut = new CustomerUserEntity();

        Assert.assertNull(sut.findFirstActivePhone(PhoneType.VOICE));
    }

    @Test
    public void testFindFirstActivePhoneWithInactiveRecords() {
        CustomerUserEntity sut = new CustomerUserEntity();
        sut.getPhones().add(preparePhone(PhoneType.VOICE, false));
        sut.getPhones().add(preparePhone(PhoneType.VOICE, false));

        Assert.assertNull(sut.findFirstActivePhone(PhoneType.VOICE));
    }

    @Test
    public void testFindFirstActivePhoneWithMultipleRecords() {
        CustomerUserEntity sut = new CustomerUserEntity();
        sut.getPhones().add(preparePhone(PhoneType.VOICE, true));
        sut.getPhones().add(preparePhone(PhoneType.VOICE, true));

        Assert.assertNotNull(sut.findFirstActivePhone(PhoneType.VOICE));
        Assert.assertSame(sut.findFirstActivePhone(PhoneType.VOICE), sut.getPhones().get(0));
    }

    @Test
    public void testFindFirstActivePhoneWithNormalCase() {
        CustomerUserEntity sut = new CustomerUserEntity();
        sut.getPhones().add(preparePhone(PhoneType.VOICE, true));

        Assert.assertNotNull(sut.findFirstActivePhone(PhoneType.VOICE));
    }

    @Test
    public void testGetActiveNotificationsWithNormalCase() {
        CustomerUserEntity sut = new CustomerUserEntity();
        sut.getNotifications().add(prepareNotification("Test1", true));
        sut.getNotifications().add(prepareNotification("Test2", false));

        List<String> result = sut.getActiveNotifications();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("Test1", result.get(0));
    }

    private UserNotificationsEntity prepareNotification(String type, boolean status) {
        UserNotificationsEntity result = new UserNotificationsEntity();
        result.setNotificationType(type);
        result.setStatus(status ? Status.ACTIVE : Status.INACTIVE);
        return result;
    }

    private UserPhoneEntity preparePhone(PhoneType type, boolean status) {
        UserPhoneEntity result = new UserPhoneEntity();
        result.setType(type);
        result.setStatus(status ? Status.ACTIVE : Status.INACTIVE);
        result.setNumber(prepareString());
        return result;
    }

    private String prepareString() {
        return RandomStringUtils.randomAlphabetic(10);
    }
}
