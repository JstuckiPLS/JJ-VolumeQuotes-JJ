package com.pls.restful;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.NotificationTypeEntity;
import com.pls.core.service.DictionaryTypesService;
import com.pls.dto.ValueLabelDTO;

/**
 * Test cases for {@link DictionaryResource}.
 * 
 * @author Maxim Medvedev
 */
@RunWith(MockitoJUnitRunner.class)
public class DictionaryResourceTest {

    @Mock
    private DictionaryTypesService notificationTypesService;

    @InjectMocks
    private DictionaryResource sut;

    @Test
    public void testGetNotificationTypes() {
        List<NotificationTypeEntity> notifications = new ArrayList<NotificationTypeEntity>();
        notifications.add(new NotificationTypeEntity());
        notifications.add(new NotificationTypeEntity());
        Mockito.when(notificationTypesService.getNotificationTypes()).thenReturn(notifications);

        List<ValueLabelDTO> result = sut.getNotificationTypes();

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
    }

}
