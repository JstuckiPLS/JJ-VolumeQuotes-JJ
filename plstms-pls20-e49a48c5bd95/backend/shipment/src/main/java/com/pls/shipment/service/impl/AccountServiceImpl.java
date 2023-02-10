package com.pls.shipment.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pls.core.domain.bo.CalendarDayBO;
import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.shipment.dao.LtlShipmentDao;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.service.AccountService;

/**
 * Account service.
 * 
 * @author Aleksandr Leshchenko
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private static final int WEEK_AMOUNT_SHOWN_IN_CALENDAR = 6;
    private static final int LAST_DAY_OF_WEEK = Calendar.SATURDAY;
    private static final int FIRST_DAY_OF_WEEK = Calendar.SUNDAY;
    public static final ShipmentStatus[] STATUSES = new ShipmentStatus[] { ShipmentStatus.BOOKED,
            ShipmentStatus.DISPATCHED, ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED,
            ShipmentStatus.OUT_FOR_DELIVERY };
    public static final ShipmentStatus[] HISTORY_STATUSES = new ShipmentStatus[] { ShipmentStatus.BOOKED,
            ShipmentStatus.DISPATCHED, ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED,
            ShipmentStatus.OUT_FOR_DELIVERY, ShipmentStatus.CANCELLED };

    private LtlShipmentDao ltlShipmentDao;

    @Autowired
    public void setLtlShipmentDao(LtlShipmentDao ltlShipmentDao) {
        this.ltlShipmentDao = ltlShipmentDao;
    }

    @Override
    public List<CalendarDayBO> getCalendar(Long customerId, Date dateOfMonth, ShipmentStatus basedOn) {
        Calendar calendar = getFirstDayOfMonth(dateOfMonth);
        int currentMonth = calendar.get(Calendar.MONTH);
        int previousMonth = -1;
        int nextMonth = -1;

        // calculate From Date
        boolean needToLoadPreviousMonth = calendar.get(Calendar.DAY_OF_WEEK) != FIRST_DAY_OF_WEEK;
        if (needToLoadPreviousMonth) {
            calendar.add(Calendar.MONTH, -1);
            previousMonth = calendar.get(Calendar.MONTH);
        }
        Date fromDate = calendar.getTime();

        // calculate To Date
        calendar.add(Calendar.MONTH, needToLoadPreviousMonth ? 2 : 1);
        calendar.add(Calendar.MILLISECOND, -1);
        calendar.setMinimalDaysInFirstWeek(1);
        if (calendar.get(Calendar.WEEK_OF_MONTH) < WEEK_AMOUNT_SHOWN_IN_CALENDAR) { // UI calendar always shows 6 weeks
            calendar.add(Calendar.WEEK_OF_YEAR, WEEK_AMOUNT_SHOWN_IN_CALENDAR - calendar.get(Calendar.WEEK_OF_MONTH));
        }
        calendar.set(Calendar.DAY_OF_WEEK, LAST_DAY_OF_WEEK);
        if (calendar.get(Calendar.MONTH) != currentMonth) {
            nextMonth = calendar.get(Calendar.MONTH);
        }
        Date toDate = calendar.getTime();

        List<CalendarDayBO> calendarActivity = ltlShipmentDao.getOrganizationCalendarActivity(customerId,
                fromDate, toDate, Arrays.asList(STATUSES), basedOn);

        // find and prepare items with monthly and weekly totals
        List<CalendarDayBO> monthlyTotals = findMonthlyTotals(calendarActivity, dateOfMonth, previousMonth);
        List<CalendarDayBO> weeklyTotals = findWeeklyTotals(calendarActivity, toDate, currentMonth, nextMonth);

        calculateMonthlyAndWeeklyTotals(monthlyTotals, weeklyTotals, calendarActivity);
        calendar = getFirstDayOfMonth(dateOfMonth);
        calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, FIRST_DAY_OF_WEEK);
        fromDate = calendar.getTime();
        removeUnnecessaryDays(calendarActivity, calendar, previousMonth);
        fillEmptyDays(calendarActivity, fromDate, toDate);

        return calendarActivity;
    }

    @Override
    public List<ShipmentListItemBO> findShipmentHistory(RegularSearchQueryBO search, Long userId) {
        List<ShipmentListItemBO> result = ltlShipmentDao.findShipmentInfo(search, userId, HISTORY_STATUSES);
        Collections.sort(result, STATUS_COMPARATOR);
        return result;
    }

    private static final Comparator<ShipmentListItemBO> STATUS_COMPARATOR = new Comparator<ShipmentListItemBO>() {
        private int getStatusCost(ShipmentStatus status) {
            int cost = 0;
            if (status != null) {
                switch (status) {
                case BOOKED:
                    cost = 4;
                    break;
                case DISPATCHED:
                    cost = 3;
                    break;
                case IN_TRANSIT:
                case OUT_FOR_DELIVERY:
                    cost = 2;
                    break;
                case DELIVERED:
                    cost = 1;
                    break;
                default:
                    break;
                }
            }
            return cost;
        }

        @Override
        public int compare(ShipmentListItemBO o1, ShipmentListItemBO o2) {
            return getStatusCost((ShipmentStatus) o2.getStatus()) - getStatusCost((ShipmentStatus) o1.getStatus());
        }
    };

    @Override
    public List<ShipmentListItemBO> getUserGroupShipmentForCalendarActivity(Long customerId,
            DateRangeQueryBO dateRange, ShipmentStatus basedOn) {
        return ltlShipmentDao.findShipmentsInfo(customerId, basedOn, dateRange, STATUSES);
    }

    private void removeUnnecessaryDays(List<CalendarDayBO> calendarActivity, Calendar fromDate,
            int previousMonth) {
        if (previousMonth == -1) {
            return;
        }
        Iterator<CalendarDayBO> it = calendarActivity.iterator();
        while (it.hasNext()) {
            CalendarDayBO day = it.next();
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.setTime(day.getExactDate());
            if (calendar.get(Calendar.MONTH) == previousMonth
                    && calendar.get(Calendar.DAY_OF_MONTH) < fromDate.get(Calendar.DAY_OF_MONTH)) {
                it.remove();
            }
        }
    }

    private void fillEmptyDays(List<CalendarDayBO> calendarActivity, Date fromDate, Date toDate) {
        Calendar fromDateCalendar = Calendar.getInstance(Locale.US);
        fromDateCalendar.setTime(fromDate);
        Calendar toDateCalendar = Calendar.getInstance(Locale.US);
        toDateCalendar.setTime(toDate);

        do {
            getCalendarDayBOByDate(calendarActivity, fromDateCalendar);
            fromDateCalendar.add(Calendar.DAY_OF_YEAR, 1);
        } while (toDateCalendar.get(Calendar.DAY_OF_YEAR) != fromDateCalendar.get(Calendar.DAY_OF_YEAR));
    }

    private List<CalendarDayBO> findWeeklyTotals(List<CalendarDayBO> calendarActivity, Date toDate,
            int currentMonth, int nextMonth) {
        List<CalendarDayBO> weeklyTotals = new ArrayList<CalendarDayBO>(WEEK_AMOUNT_SHOWN_IN_CALENDAR);
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(toDate);
        while (calendar.get(Calendar.MONTH) == currentMonth || calendar.get(Calendar.MONTH) == nextMonth) {
            CalendarDayBO bo = getCalendarDayBOByDate(calendarActivity, calendar);
            bo.setWeeklyTotal(new CalendarDayBO(bo.getExactDate(), 0L, BigDecimal.ZERO));
            weeklyTotals.add(bo);
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }
        return weeklyTotals;
    }

    private CalendarDayBO getCalendarDayBOByDate(List<CalendarDayBO> calendarActivity, Calendar calendar) {
        for (CalendarDayBO day : calendarActivity) {
            if (isSameDay(day.getExactDate(), calendar)) {
                return day;
            }
        }
        CalendarDayBO day = new CalendarDayBO(calendar.getTime(), 0L, BigDecimal.ZERO);
        calendarActivity.add(day);
        return day;
    }

    private boolean isSameDay(Date exactDate, Calendar calendar) {
        Calendar exactDateCalendar = Calendar.getInstance(Locale.US);
        exactDateCalendar.setTime(exactDate);
        return exactDateCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
    }

    private List<CalendarDayBO> findMonthlyTotals(List<CalendarDayBO> calendarActivity, Date dateOfMonth,
            int previousMonth) {
        Calendar calendar = getFirstDayOfMonth(dateOfMonth);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        List<CalendarDayBO> monthlyTotals = new ArrayList<CalendarDayBO>(2);

        CalendarDayBO bo = getCalendarDayBOByDate(calendarActivity, calendar);
        bo.setMonthlyTotal(new CalendarDayBO(bo.getExactDate(), 0L, BigDecimal.ZERO));
        monthlyTotals.add(bo); // last day of current month

        if (previousMonth >= 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -calendar.get(Calendar.DAY_OF_MONTH));

            bo = getCalendarDayBOByDate(calendarActivity, calendar);
            bo.setMonthlyTotal(new CalendarDayBO(bo.getExactDate(), 0L, BigDecimal.ZERO));
            monthlyTotals.add(bo); // last day of previous month
        }
        return monthlyTotals;
    }

    private void calculateMonthlyAndWeeklyTotals(List<CalendarDayBO> monthlyTotals,
            List<CalendarDayBO> weeklyTotals, List<CalendarDayBO> calendarActivity) {
        for (CalendarDayBO day : calendarActivity) {
            for (CalendarDayBO lastDayOfWeek : weeklyTotals) {
                if (isSameWeek(lastDayOfWeek.getExactDate(), day.getExactDate())) {
                    lastDayOfWeek.getWeeklyTotal().setTotalCount(
                            lastDayOfWeek.getWeeklyTotal().getTotalCount() + day.getTotalCount());
                    lastDayOfWeek.getWeeklyTotal().setTotalCost(
                            lastDayOfWeek.getWeeklyTotal().getTotalCost().add(day.getTotalCost()));
                    break;
                }
            }
            for (CalendarDayBO lastDayOfMonth : monthlyTotals) {
                if (isSameMonth(lastDayOfMonth.getExactDate(), day.getExactDate())) {
                    lastDayOfMonth.getMonthlyTotal().setTotalCount(
                            lastDayOfMonth.getMonthlyTotal().getTotalCount() + day.getTotalCount());
                    lastDayOfMonth.getMonthlyTotal().setTotalCost(
                            lastDayOfMonth.getMonthlyTotal().getTotalCost().add(day.getTotalCost()));
                    break;
                }
            }
        }
    }

    private boolean isSameMonth(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance(Locale.US);
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance(Locale.US);
        calendar2.setTime(date2);
        return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
    }

    private boolean isSameWeek(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance(Locale.US);
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance(Locale.US);
        calendar2.setTime(date2);
        return calendar1.get(Calendar.WEEK_OF_YEAR) == calendar2.get(Calendar.WEEK_OF_YEAR);
    }

    private Calendar getFirstDayOfMonth(Date dateOfMonth) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(dateOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }
}