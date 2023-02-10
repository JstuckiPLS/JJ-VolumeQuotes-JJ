package com.pls.dtobuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.fest.assertions.api.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.dto.enums.CalendarShipmentDetailsDateRange;
import com.pls.dto.query.DateRangeQueryDTO;
import com.pls.dtobuilder.util.DateUtils;
import com.pls.dtobuilder.util.DateUtils.CalendarProvider;

/**
 * Test cases for {@link DateRangeQueryDTOBuilder} class.
 * 
 * @author Artem Arapov
 */
public class DateRangeQueryDTOBuilderTest {

    private final class TestCalendarProvider implements CalendarProvider {
        @Override
        public Calendar getNow() {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.set(Calendar.YEAR, 2012);
            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 12);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.getTime();
            return calendar;
        }
    }

    private static final String DATE_FORMAT = "MM.dd.yyyy HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "MM.dd.yyyy HH:mm:ss.SSS";
    private static final String DEFAULT_FROM_DATE = "12.01.2012 00:00:00.000 -0500";
    private static final String DEFAULT_QUERY_STRING = "DEFAULT,2012-12-01 -0500,2012-12-10 -0500";
    private static final String DEFAULT_TO_DATE = "12.10.2012 23:59:59.999 -0500";
    private static final String MONTH_QUERY_STRING = "MONTH";

    private static final String QUARTER_QUERY_STRING = "QUARTER";
    private static final String TODAY_QUERY_STRING = "TODAY";
    private static final String WEEK_QUERY_STRING = "WEEK";
    private static final String YEAR_QUERY_STRING = "YEAR";

    private DateRangeQueryDTOBuilder builder;

    private DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    @Before
    public void setUp() throws Exception {
        builder = new DateRangeQueryDTOBuilder();

        DateUtils.setCalendarProvider(new TestCalendarProvider());
    }

    @Test
    public void shouldCreateBOForCalendarDateRangeMonth() throws Exception {
        Date fromDate = dateFormat.parse("10.01.2012 00:00:00 -0500");
        Date toDate = dateFormat.parse("10.31.2012 23:59:59 -0500");

        DateRangeQueryBO bo = builder.buildBO(fromDate, CalendarShipmentDetailsDateRange.MONTH);

        Assert.assertNotNull(bo);

        compareActualAndExactDates(fromDate, toDate, bo);
    }

    @Test
    public void shouldCreateBOForCalendarDateRangeSingleDay() throws Exception {
        Date fromDate = dateFormat.parse("10.01.2012 00:00:00 -0500");
        Date toDate = dateFormat.parse("10.01.2012 23:59:59 -0500");

        DateRangeQueryBO bo = builder.buildBO(fromDate, CalendarShipmentDetailsDateRange.SINGLE_DAY);

        Assert.assertNotNull(bo);

        compareActualAndExactDates(fromDate, toDate, bo);
    }

    @Test
    public void shouldCreateBOForCalendarDateRangeWeek() throws Exception {
        Date fromDate = dateFormat.parse("12.09.2012 00:00:00 -0500");
        Date toDate = dateFormat.parse("12.15.2012 23:59:59 -0500");

        DateRangeQueryBO bo = builder.buildBO(fromDate, CalendarShipmentDetailsDateRange.WEEK);

        Assert.assertNotNull(bo);

        compareActualAndExactDates(fromDate, toDate, bo);
    }

    @Test
    public void shouldCreateBOWithDefault() throws Exception {
        SimpleDateFormat simpleDateFormatForDefault = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date fromDate = simpleDateFormatForDefault.parse(DEFAULT_FROM_DATE);
        Date toDate = simpleDateFormatForDefault.parse(DEFAULT_TO_DATE);

        DateRangeQueryDTO dto = new DateRangeQueryDTO(DEFAULT_QUERY_STRING);
        DateRangeQueryBO actual = builder.buildEntity(dto);

        Assert.assertNotNull(actual);
        Assert.assertEquals(fromDate, actual.getFromDate());
        Assert.assertEquals(toDate, actual.getToDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCreateBOWithException() throws Exception {
        DateRangeQueryBO actual = builder.buildEntity(null);
        Assert.assertNotNull(actual);
    }

    @Test
    public void shouldCreateBOWithMonth() throws Exception {
        Date fromDate = dateFormat.parse("12.01.2012 00:00:00 -0500");
        Date toDate = dateFormat.parse("12.31.2012 23:59:59 -0500");

        DateRangeQueryDTO dto = new DateRangeQueryDTO(MONTH_QUERY_STRING);
        DateRangeQueryBO bo = builder.buildEntity(dto);

        Assert.assertNotNull(bo);
        compareActualAndExactDates(fromDate, toDate, bo);
    }

    @Test(expected = AssertionError.class)
    public void shouldCreateBOWithMonthWithIncorrectData() throws Exception {
        Date invalidDate = dateFormat.parse("01.01.1900 23:01:05 -0500");

        DateRangeQueryDTO dto = new DateRangeQueryDTO(MONTH_QUERY_STRING);
        DateRangeQueryBO bo = builder.buildEntity(dto);

        Assert.assertNotNull(bo);
        Assertions.assertThat(bo.getFromDate()).isInSameYearAs(invalidDate).isInSameMonthAs(invalidDate)
                .isInSameDayAs(invalidDate).isInSameHourAs(invalidDate).isInSameMinuteAs(invalidDate)
                .isInSameSecondAs(invalidDate);
    }

    @Test
    public void shouldCreateBOWithQuarter() throws Exception {
        Date fromDate = dateFormat.parse("10.01.2012 00:00:00 -0500");
        Date toDate = dateFormat.parse("12.31.2012 23:59:59 -0500");

        DateRangeQueryDTO dto = new DateRangeQueryDTO(QUARTER_QUERY_STRING);
        DateRangeQueryBO bo = builder.buildEntity(dto);

        Assert.assertNotNull(bo);

        compareActualAndExactDates(fromDate, toDate, bo);
    }

    @Test
    public void shouldCreateBOWithToday() throws Exception {
        Date fromDate = dateFormat.parse("12.12.2012 00:00:00 -0500");
        Date toDate = dateFormat.parse("12.12.2012 23:59:59 -0500");

        DateRangeQueryDTO dto = new DateRangeQueryDTO(TODAY_QUERY_STRING);
        DateRangeQueryBO bo = builder.buildEntity(dto);

        Assert.assertNotNull(bo);

        compareActualAndExactDates(fromDate, toDate, bo);
    }

    @Test
    public void shouldCreateBOWithWeek() throws Exception {
        Date fromDate = dateFormat.parse("12.09.2012 00:00:00 -0500");
        Date toDate = dateFormat.parse("12.15.2012 23:59:59 -0500");

        DateRangeQueryDTO dto = new DateRangeQueryDTO(WEEK_QUERY_STRING);
        DateRangeQueryBO bo = builder.buildEntity(dto);

        Assert.assertNotNull(bo);

        compareActualAndExactDates(fromDate, toDate, bo);
    }

    @Test
    public void shouldCreateBOWithYeart() throws Exception {
        Date fromDate = dateFormat.parse("01.01.2012 00:00:00 -0500");
        Date toDate = dateFormat.parse("12.31.2012 23:59:59 -0500");

        DateRangeQueryDTO dto = new DateRangeQueryDTO(YEAR_QUERY_STRING);
        DateRangeQueryBO bo = builder.buildEntity(dto);

        Assert.assertNotNull(bo);
        compareActualAndExactDates(fromDate, toDate, bo);
    }

    @Test(expected = AssertionError.class)
    public void shouldCreateBOWithYeartWithIncorrectData() throws Exception {
        Date invalidDate = dateFormat.parse("01.01.1900 23:01:05 -0500");

        DateRangeQueryDTO dto = new DateRangeQueryDTO(YEAR_QUERY_STRING);
        DateRangeQueryBO bo = builder.buildEntity(dto);

        Assert.assertNotNull(bo);
        Assertions.assertThat(bo.getFromDate()).isInSameYearAs(invalidDate).isInSameMonthAs(invalidDate)
                .isInSameDayAs(invalidDate).isInSameHourAs(invalidDate).isInSameMinuteAs(invalidDate)
                .isInSameSecondAs(invalidDate);
    }

    @After
    public void tearDown() {
        //To avoid side effects for other tests
        DateUtils.setCalendarProvider(DateUtils.DEFAULT_PROVIDER);
    }

    private void compareActualAndExactDates(Date fromDate, Date toDate, DateRangeQueryBO bo) {
        Assertions.assertThat(bo.getFromDate()).isInSameYearAs(fromDate).isInSameMonthAs(fromDate)
                .isInSameDayAs(fromDate).isInSameHourAs(fromDate).isInSameMinuteAs(fromDate)
                .isInSameSecondAs(fromDate);
        Assertions.assertThat(bo.getToDate()).isInSameYearAs(toDate).isInSameMonthAs(toDate)
                .isInSameDayAs(toDate).isInSameHourAs(toDate).isInSameMinuteAs(toDate)
                .isInSameSecondAs(toDate);
    }
}
