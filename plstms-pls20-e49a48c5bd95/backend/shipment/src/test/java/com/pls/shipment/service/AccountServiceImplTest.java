package com.pls.shipment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pls.core.domain.bo.CalendarDayBO;
import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.service.impl.security.util.SecurityTestUtils;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.service.impl.AccountServiceImpl;

/**
 * Test for {@link com.pls.shipment.service.impl.AccountServiceImpl}.
 * 
 * @author Aleksandr Leshchenko
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss.SSS";
    private static final String STATUSES_NOT_ORDERED = "Statuses are not ordered!";

    private static final List<ShipmentStatus> STATUSES = Arrays.asList(ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED, ShipmentStatus.IN_TRANSIT,
            ShipmentStatus.DELIVERED, ShipmentStatus.OUT_FOR_DELIVERY);
    private static final ShipmentStatus BASED_ON = ShipmentStatus.IN_TRANSIT;
    private static final Long USER_ID = (long) (Math.random() * 100);
    private static final Long ORGANIZATION_ID = (long) (Math.random() * 100);

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private Map<Date, CalendarDayBO> calendarMap;
    @Mock
    private LtlShipmentDao ltlShipmentDao;

    private final List<ShipmentListItemBO> shipments = new ArrayList<ShipmentListItemBO>();

    @Before
    public void init() {
        SecurityTestUtils.login("Test", USER_ID, ORGANIZATION_ID);

        shipments.clear();
        shipments.add(create(ShipmentStatus.DELIVERED));
        shipments.add(create(ShipmentStatus.OUT_FOR_DELIVERY));
        shipments.add(create(ShipmentStatus.IN_TRANSIT));
        shipments.add(create(ShipmentStatus.BOOKED));
        shipments.add(create(ShipmentStatus.BOOKED));
        shipments.add(create(ShipmentStatus.IN_TRANSIT));
        shipments.add(create(ShipmentStatus.BOOKED));
        shipments.add(create(ShipmentStatus.DISPATCHED));
    }

    private ShipmentListItemBO create(ShipmentStatus status) {
        ShipmentListItemBO result = new ShipmentListItemBO();
        result.setStatus(status);
        return result;
    }

    @Test
    public void shouldGetCalendarForFourWeeks() throws ParseException {
        DateMatcher fromDateMatcher = new DateMatcher("01.02.2015 00:00:00.000");
        DateMatcher toDateMatcher = new DateMatcher("14.03.2015 23:59:59.999");
        Calendar dateOfMonth = Calendar.getInstance(Locale.US);
        dateOfMonth.setTime(new SimpleDateFormat(DATE_FORMAT).parse("01.02.2015 11:25:33.123"));

        for (int i = 0; i < 28; i++) {
            List<CalendarDayBO> result = accountService.getCalendar(ORGANIZATION_ID, dateOfMonth.getTime(), BASED_ON);
            verify(ltlShipmentDao, times(i + 1)).getOrganizationCalendarActivity(eq(ORGANIZATION_ID), argThat(fromDateMatcher),
                    argThat(toDateMatcher), eq(STATUSES), eq(BASED_ON));
            assertEquals(42, result.size());
            dateOfMonth.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Test
    public void shouldGetCalendarForFiveWeeks() throws ParseException {
        DateMatcher fromDateMatcher = new DateMatcher("01.01.2010 00:00:00.000");
        DateMatcher toDateMatcher = new DateMatcher("13.03.2010 23:59:59.999");
        Calendar dateOfMonth = Calendar.getInstance(Locale.US);
        dateOfMonth.setTime(new SimpleDateFormat(DATE_FORMAT).parse("01.02.2010 18:44:27.321"));

        for (int i = 0; i < 28; i++) {
            List<CalendarDayBO> result = accountService.getCalendar(ORGANIZATION_ID, dateOfMonth.getTime(), BASED_ON);
            verify(ltlShipmentDao, times(i + 1)).getOrganizationCalendarActivity(eq(ORGANIZATION_ID), argThat(fromDateMatcher),
                    argThat(toDateMatcher), eq(STATUSES), eq(BASED_ON));
            assertEquals(42, result.size());
            dateOfMonth.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Test
    public void shouldGetCalendarForSixWeeks() throws ParseException {
        DateMatcher fromDateMatcher = new DateMatcher("01.11.2012 00:00:00.000");
        DateMatcher toDateMatcher = new DateMatcher("05.01.2013 23:59:59.999");
        Calendar dateOfMonth = Calendar.getInstance(Locale.US);
        dateOfMonth.setTime(new SimpleDateFormat(DATE_FORMAT).parse("01.12.2012 00:00:00.000"));

        for (int i = 0; i < 31; i++) {
            List<CalendarDayBO> result = accountService.getCalendar(ORGANIZATION_ID, dateOfMonth.getTime(), BASED_ON);
            verify(ltlShipmentDao, times(i + 1)).getOrganizationCalendarActivity(eq(ORGANIZATION_ID), argThat(fromDateMatcher),
                    argThat(toDateMatcher), eq(STATUSES), eq(BASED_ON));
            assertEquals(42, result.size());
            dateOfMonth.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Test
    public void shouldGetCalendarWithTotals() throws ParseException {
        DateMatcher fromDateMatcher = new DateMatcher("01.11.2012 00:00:00.000");
        DateMatcher toDateMatcher = new DateMatcher("05.01.2013 23:59:59.999");
        Calendar dateOfMonth = Calendar.getInstance(Locale.US);
        dateOfMonth.setTime(new SimpleDateFormat(DATE_FORMAT).parse("11.12.2012 00:00:00.000"));

        when(ltlShipmentDao.getOrganizationCalendarActivity(eq(ORGANIZATION_ID), argThat(fromDateMatcher),
                argThat(toDateMatcher), eq(STATUSES), eq(BASED_ON))).thenReturn(getTestCalendarData());

        List<CalendarDayBO> result = accountService.getCalendar(ORGANIZATION_ID, dateOfMonth.getTime(), BASED_ON);
        verify(ltlShipmentDao).getOrganizationCalendarActivity(eq(ORGANIZATION_ID), argThat(fromDateMatcher),
                argThat(toDateMatcher), eq(STATUSES), eq(BASED_ON));
        assertEquals(42, result.size());

        for (int i = 1; i <= 42; i++) {
            if (i % 7 == 0) {
                assertNotNull("" + i, result.get(i - 1).getWeeklyTotal());
                assertEquals("" + i, BigDecimal.valueOf(70), result.get(i - 1).getWeeklyTotal().getTotalCost());
                assertEquals("" + i, Long.valueOf(70), result.get(i - 1).getWeeklyTotal().getTotalCount());
                assertNull("" + i, result.get(i - 1).getWeeklyTotal().getWeeklyTotal());
                assertNull("" + i, result.get(i - 1).getWeeklyTotal().getMonthlyTotal());
            } else {
                assertNull("" + i, result.get(i - 1).getWeeklyTotal());
            }
            if (i == 6) {
                assertNotNull("" + i, result.get(i - 1).getMonthlyTotal());
                assertEquals("" + i, BigDecimal.valueOf(300), result.get(i - 1).getMonthlyTotal().getTotalCost());
                assertEquals("" + i, Long.valueOf(300), result.get(i - 1).getMonthlyTotal().getTotalCount());
                assertNull("" + i, result.get(i - 1).getMonthlyTotal().getWeeklyTotal());
                assertNull("" + i, result.get(i - 1).getMonthlyTotal().getMonthlyTotal());
            } else if (i == 37) {
                assertNotNull("" + i, result.get(i - 1).getMonthlyTotal());
                assertEquals("" + i, BigDecimal.valueOf(310), result.get(i - 1).getMonthlyTotal().getTotalCost());
                assertEquals("" + i, Long.valueOf(310), result.get(i - 1).getMonthlyTotal().getTotalCount());
                assertNull("" + i, result.get(i - 1).getMonthlyTotal().getWeeklyTotal());
                assertNull("" + i, result.get(i - 1).getMonthlyTotal().getMonthlyTotal());
            } else {
                assertNull("" + i, result.get(i - 1).getMonthlyTotal());
            }
        }
    }

    private List<CalendarDayBO> getTestCalendarData() throws ParseException {
        Calendar dateOfMonth = Calendar.getInstance(Locale.US);
        dateOfMonth.setTime(new SimpleDateFormat(DATE_FORMAT).parse("01.11.2012 13:15:22.456"));
        ArrayList<CalendarDayBO> calendar = new ArrayList<CalendarDayBO>(66);
        for (int i = 0; i < 66; i++) {
            calendar.add(new CalendarDayBO(dateOfMonth.getTime(), 10L, BigDecimal.TEN));
            dateOfMonth.add(Calendar.DAY_OF_YEAR, 1);
        }
        return calendar;
    }

    private static class DateMatcher extends ArgumentMatcher<Date> {
        private final Calendar expectedDate;

        DateMatcher(String expectedDate) throws ParseException {
            this.expectedDate = Calendar.getInstance(Locale.US);
            this.expectedDate.setTime(new SimpleDateFormat(DATE_FORMAT).parse(expectedDate));
        }

        @Override
        public boolean matches(Object arg0) {
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.setTime((Date) arg0);
            return calendar.get(Calendar.YEAR) == expectedDate.get(Calendar.YEAR)
                    && calendar.get(Calendar.DAY_OF_YEAR) == expectedDate.get(Calendar.DAY_OF_YEAR)
                    && calendar.get(Calendar.HOUR_OF_DAY) == expectedDate.get(Calendar.HOUR_OF_DAY)
                    && calendar.get(Calendar.MINUTE) == expectedDate.get(Calendar.MINUTE)
                    && calendar.get(Calendar.SECOND) == expectedDate.get(Calendar.SECOND)
                    && calendar.get(Calendar.MILLISECOND) == expectedDate.get(Calendar.MILLISECOND);
        }
    }

    @Test
    public void shouldFindShipmentsHistory() {
        when(ltlShipmentDao.findShipmentsInfo(ORGANIZATION_ID, ShipmentStatus.DELIVERED, new DateRangeQueryBO(),
                ShipmentStatus.BOOKED)).thenReturn(shipments);

        List<ShipmentListItemBO> result = accountService.findShipmentHistory(new RegularSearchQueryBO(), 1L);

        assertNotNull(result);
    }

    @Test
    public void testFindShipmentHistory() {
        when(ltlShipmentDao.findShipmentsInfo(ORGANIZATION_ID, ShipmentStatus.DELIVERED, new DateRangeQueryBO(), null,
                ShipmentStatus.BOOKED)).thenReturn(shipments);

        List<ShipmentListItemBO> result = accountService.findShipmentHistory(new RegularSearchQueryBO(), 1L);

        // check booked first. It hopes that in setup statuses only for this test.
        Iterator<ShipmentListItemBO> it = result.iterator();
        boolean wasAnotherStatus = false;
        while (it.hasNext()) {
            ShipmentListItemBO e = it.next();
            if (hasStatus(e, ShipmentStatus.BOOKED) && wasAnotherStatus) {
                Assert.fail(STATUSES_NOT_ORDERED);
            } else if (!hasStatus(e, ShipmentStatus.BOOKED)) {
                wasAnotherStatus = true;
            }
            it.remove();
        }

        // check dispatched - should be second
        it = result.iterator();
        wasAnotherStatus = false;
        while (it.hasNext()) {
            ShipmentListItemBO e = it.next();
            if (hasStatus(e, ShipmentStatus.DISPATCHED) && wasAnotherStatus) {
                Assert.fail(STATUSES_NOT_ORDERED);
            } else if (!hasStatus(e, ShipmentStatus.DISPATCHED)) {
                wasAnotherStatus = true;
            }
            it.remove();
        }

        // check in transit - should be third
        it = result.iterator();
        wasAnotherStatus = false;
        while (it.hasNext()) {
            ShipmentListItemBO e = it.next();
            if ((hasStatus(e, ShipmentStatus.IN_TRANSIT)) || hasStatus(e, ShipmentStatus.OUT_FOR_DELIVERY)
                    || hasStatus(e, ShipmentStatus.DISPATCHED) && wasAnotherStatus) {
                Assert.fail(STATUSES_NOT_ORDERED);
            } else if (!hasStatus(e, ShipmentStatus.IN_TRANSIT) || !hasStatus(e, ShipmentStatus.OUT_FOR_DELIVERY)
                    || !hasStatus(e, ShipmentStatus.DISPATCHED)) {
                wasAnotherStatus = true;
            }
            it.remove();
        }

        // check delivered - should be fourth
        it = result.iterator();
        wasAnotherStatus = false;
        while (it.hasNext()) {
            ShipmentListItemBO e = it.next();
            if (hasStatus(e, ShipmentStatus.DELIVERED) && wasAnotherStatus) {
                Assert.fail(STATUSES_NOT_ORDERED);
            } else if (!hasStatus(e, ShipmentStatus.DELIVERED)) {
                wasAnotherStatus = true;
            }
            it.remove();
        }
    }

    private boolean hasStatus(ShipmentListItemBO shipment, ShipmentStatus status) {
        return status == shipment.getStatus();
    }

    @Test
    public void shouldGetUserGroupShipmentForCalendarActivity() throws Exception {
        List<ShipmentListItemBO> expected = Collections.unmodifiableList(new ArrayList<ShipmentListItemBO>());
        ShipmentStatus[] statuses = AccountServiceImpl.STATUSES;
        DateRangeQueryBO dateRange = new DateRangeQueryBO(toDate("01.01.2012 00:00:00.000"),
                toDate("07.01.2012 00:00:00.000"));

        Mockito.when(ltlShipmentDao.findShipmentsInfo(ORGANIZATION_ID, BASED_ON, dateRange, statuses))
                .thenReturn(expected);

        List<ShipmentListItemBO> result = accountService.getUserGroupShipmentForCalendarActivity(
                ORGANIZATION_ID, dateRange, BASED_ON);

        verify(ltlShipmentDao).findShipmentsInfo(ORGANIZATION_ID, BASED_ON, dateRange, statuses);

        assertSame(result, expected);
    }

    private Date toDate(String source2) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT).parse(source2);
    }
}