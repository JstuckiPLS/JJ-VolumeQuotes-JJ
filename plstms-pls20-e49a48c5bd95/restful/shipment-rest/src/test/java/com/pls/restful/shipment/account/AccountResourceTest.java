package com.pls.restful.shipment.account;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.dto.account.BasedOnDTO;
import com.pls.dto.enums.CalendarShipmentDetailsDateRange;
import com.pls.shipment.service.AccountService;

/**
 * Test for {@link AccountResource} class.
 * 
 * @author Dmitriy Nefedchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountResourceTest {
    @InjectMocks
    private AccountResource sut;

    @Mock
    private AccountService accountService;
    @Mock
    private UserPermissionsService permissionService;
    @Mock
    HttpServletRequest request;

    private Long customerId;
    private Long userId;
    private String date;

    @Before
    public void setUp() {
        customerId = 1L;
        userId = 1L;
        date = "10-01-2013";
        SecurityTestUtils.login("PLS", 1L);
    }

    @After
    public void after() {
        SecurityTestUtils.logout();
    }

    @Test
    public void testCalendarDetails() {
        sut.getCalendarDetails(customerId, userId, date, BasedOnDTO.BOOKED_DATE.getLabel(),
                CalendarShipmentDetailsDateRange.SINGLE_DAY);

        verify(permissionService, times(1)).checkCapabilityAndOrganization(customerId,
                Capabilities.ACCOUNT_HISTORY_CALENDAR_VIEW.name());
        verify(accountService, times(1)).getUserGroupShipmentForCalendarActivity(eq(customerId),
                notNull(DateRangeQueryBO.class), eq(ShipmentStatus.BOOKED));
    }

    @Test
    public void testGetCalendar() {
        sut.getCalendar(customerId, userId, date, BasedOnDTO.BOOKED_DATE.getLabel());

        verify(permissionService, times(1)).checkCapabilityAndOrganization(customerId,
                Capabilities.ACCOUNT_HISTORY_CALENDAR_VIEW.name());
        verify(accountService).getCalendar(eq(customerId), notNull(Date.class), eq(ShipmentStatus.BOOKED));
    }

    @Test
    public void testGetHistory() throws ApplicationException {
        sut.getHistory(customerId, request);

        verify(permissionService, times(1)).checkCapabilityAndOrganization(customerId,
                Capabilities.ACCOUNT_HISTORY_PAGE_VIEW.name());
        verify(accountService, times(1)).findShipmentHistory(notNull(RegularSearchQueryBO.class),
                eq(1L));
    }
}
