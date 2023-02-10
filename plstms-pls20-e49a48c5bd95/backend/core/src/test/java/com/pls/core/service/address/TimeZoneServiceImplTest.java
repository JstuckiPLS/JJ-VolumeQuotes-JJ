package com.pls.core.service.address;

import com.pls.core.dao.TimeZoneDao;
import com.pls.core.domain.TimeZoneEntity;
import com.pls.core.service.address.impl.TimeZoneServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Test cases for {@link TimeZoneServiceImpl}.
 *
 * @author Denis Zhupinsky (Team International)
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeZoneServiceImplTest {
    @InjectMocks
    private TimeZoneServiceImpl sut;

    @Mock
    private TimeZoneDao timeZoneDao;

    @Test
    public void testFindAll() {
        List<TimeZoneEntity> expectedList = new ArrayList<TimeZoneEntity>();
        expectedList.add(new TimeZoneEntity());
        Mockito.when(timeZoneDao.getAll()).thenReturn(expectedList);
        List<TimeZoneEntity> actualList = sut.findAll();
        Mockito.verify(timeZoneDao).getAll();
        Assert.assertEquals(expectedList, actualList);
    }
}
