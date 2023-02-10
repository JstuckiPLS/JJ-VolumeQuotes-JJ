package com.pls.shipment.dao;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pls.core.dao.AbstractDaoTest;
import com.pls.core.domain.bo.CalendarDayBO;
import com.pls.core.domain.enums.ShipmentStatus;

/**
 * Test cases for {@link com.pls.shipment.dao.impl.LtlShipmentDaoImpl} class.
 * 
 * @author Oleksandr Brychak
 */
public class LtlShipmentCalendarlIT extends AbstractDaoTest {

    @Autowired
    private LtlShipmentDao ltlShipmentDao;

    @Test
    public void shouldGetCalendarActivityForOneDay() {

        updateCreateDateToLoad("111", "'27.02.2014 01:30:00'");
        updateCreateDateToLoad("112", "'27.02.2014 04:30:00'");

        Calendar calendar = Calendar.getInstance(Locale.US);

        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date fromDate = calendar.getTime();
        calendar.add(Calendar.MILLISECOND, 24 * 60 * 60 * 1000 - 1);
        Date toDate = calendar.getTime();

        List<ShipmentStatus> statuses = Arrays.asList(ShipmentStatus.BOOKED, ShipmentStatus.DISPATCHED,
                ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED, ShipmentStatus.OUT_FOR_DELIVERY);

        List<CalendarDayBO> userCalendarActivity = ltlShipmentDao.getOrganizationCalendarActivity(1L, fromDate, toDate,
                statuses, ShipmentStatus.BOOKED);
        Assert.assertNotNull(userCalendarActivity);
        Assert.assertEquals(1, userCalendarActivity.size());
        Assert.assertEquals(new Long(2), userCalendarActivity.get(0).getTotalCount());
        Assert.assertEquals(new BigDecimal("644.10"), userCalendarActivity.get(0).getTotalCost());
        Assert.assertNull(userCalendarActivity.get(0).getMonthlyTotal());
        Assert.assertNull(userCalendarActivity.get(0).getWeeklyTotal());
        Assert.assertEquals(fromDate, userCalendarActivity.get(0).getExactDate());
    }

    private void updateCreateDateToLoad(String loadId, String dateCreated) {

        StringBuilder sql = new StringBuilder();
        sql.append("Update LOADS set DATE_CREATED =  ");
        sql.append("TO_DATE(" + dateCreated + ",'DD.MM.YYYY HH24:MI:SS') ");
        sql.append("where LOAD_ID = ");
        sql.append(loadId);
        getSession().createSQLQuery(sql.toString()).executeUpdate();

    }

}
