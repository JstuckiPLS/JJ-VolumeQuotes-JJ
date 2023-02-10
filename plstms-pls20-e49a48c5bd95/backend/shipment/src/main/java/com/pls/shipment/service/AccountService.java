package com.pls.shipment.service;

import java.util.Date;
import java.util.List;

import com.pls.core.domain.bo.CalendarDayBO;
import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.shipment.domain.bo.ShipmentListItemBO;

/**
 * Business service for account history.
 * 
 * @author Aleksandr Leshchenko
 */
public interface AccountService {

    /**
     * Get data for account history calendar.
     * 
     * @param customerId
     *            - customer identifier
     * @param dateOfMonth
     *            any date in month
     * @param basedOn
     *            status which is used to group result.
     * @return list of items with information of loads per date/week/month
     */
    List<CalendarDayBO> getCalendar(Long customerId, Date dateOfMonth, ShipmentStatus basedOn);

    /**
     * Get a <code>List</code> of {@link ShipmentListItemBO}.
     * 
     * @param search
     *            - {@link RegularSearchQueryBo}
     * @param userId
     *            id of user
     * @return <code>List</code> of {@link ShipmentListItemBO}
     */
    List<ShipmentListItemBO> findShipmentHistory(RegularSearchQueryBO search, Long userId);

    /**
     * Get <code>List</code> of {@link ShipmentListItemBO}.
     *
     * @param customerId customer identifier
     * @param dateRange from nad to dates when shipment status was set to value defined in the basedOn parameter
     * @param basedOn   status which is used to group result.
     * @return <code>List</code> of {@link ShipmentListItemBO}
     */
    List<ShipmentListItemBO> getUserGroupShipmentForCalendarActivity(Long customerId, DateRangeQueryBO dateRange,
            ShipmentStatus basedOn);
}
