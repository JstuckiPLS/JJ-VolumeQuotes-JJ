package com.pls.restful.shipment.account;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pls.core.domain.bo.CalendarDayBO;
import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.core.domain.bo.RegularSearchQueryBO;
import com.pls.core.domain.enums.ShipmentStatus;
import com.pls.core.domain.user.Capabilities;
import com.pls.core.exception.ApplicationException;
import com.pls.core.service.UserPermissionsService;
import com.pls.core.service.impl.security.util.SecurityUtils;
import com.pls.dto.account.BasedOnDTO;
import com.pls.dto.enums.CalendarShipmentDetailsDateRange;
import com.pls.dtobuilder.DateRangeQueryDTOBuilder;
import com.pls.dtobuilder.util.DateUtils;
import com.pls.dtobuilder.util.RegularSearchQueryBOBuilder;
import com.pls.shipment.domain.bo.ShipmentListItemBO;
import com.pls.shipment.service.AccountService;

/**
 * REST service for account history.
 * 
 * @author Aleksandr Leshchenko
 */
@Controller
@Transactional(readOnly = true)
@RequestMapping("/customer/{customerId}/user/{userId}/account")
public class AccountResource {

    private final DateRangeQueryDTOBuilder dateRangeBuilder = new DateRangeQueryDTOBuilder();

    @Autowired
    private UserPermissionsService permissionService;

    @Autowired
    private AccountService service;

    /**
     * Get <code>ListDTO</code> of {@link ShipmentListItemBO}.
     *
     * @param customerId customer identifier
     * @param userId     user id
     * @param basedOn    Shipment Order's date filter
     * @param startDate   Date when shipment was set to value defined in <code>baseOn</code> parameter
     * @param period     Period of time {@link CalendarShipmentDetailsDateRange} starting fromDate when shipment was
     *                   set to value defined in <code>baseOn</code> parameter
     * @return {@link List} of {@link ShipmentListItemBO}
     */
    @RequestMapping(value = "/calendar/details", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentListItemBO> getCalendarDetails(
            @PathVariable("customerId") Long customerId,
            @PathVariable("userId") Long userId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "basedOn", required = false) String basedOn,
            @RequestParam(value = "period", required = false) CalendarShipmentDetailsDateRange period) {
        permissionService.checkCapabilityAndOrganization(customerId, Capabilities.ACCOUNT_HISTORY_CALENDAR_VIEW.name());

        DateRangeQueryBO dateRange = dateRangeBuilder.buildBO(DateUtils.parseDateWithoutTimeZone(startDate), period);

        return service.getUserGroupShipmentForCalendarActivity(customerId, dateRange, toStatus(basedOn));
    }

    /**
     * Get history calendar for month.
     * 
     * @param customerId
     *            customer identifier
     * @param userId
     *            user identifier
     * @param dateOfMonth
     *            any date of month
     * @param basedOn
     *            date based on which data should be grouped
     * @return list of calendar days
     */
    @RequestMapping(value = "/calendar", method = RequestMethod.GET)
    @ResponseBody
    public List<CalendarDayBO> getCalendar(
            @PathVariable("customerId") Long customerId,
            @PathVariable("userId") Long userId,
            @RequestParam(value = "dateOfMonth", required = false) String dateOfMonth,
            @RequestParam(value = "basedOn", required = false) String basedOn) {
        permissionService.checkCapabilityAndOrganization(customerId, Capabilities.ACCOUNT_HISTORY_CALENDAR_VIEW.name());
        final Date date = DateUtils.parseDateWithoutTimeZone(dateOfMonth);
        return service.getCalendar(customerId, date, toStatus(basedOn));
    }

    /**
     * Lists all {@link ShipmentListItemBO} that was found by params.
     *
     * @param customerId customer ID
     * @param request - standard http servlet request
     * @return list of shipments
     * @throws ApplicationException
     *             if the wrong Bol Number of inappropriate wildcard pattern was entered.
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipmentListItemBO> getHistory(@PathVariable("customerId") Long customerId, HttpServletRequest request)
            throws ApplicationException {
        permissionService.checkCapabilityAndOrganization(customerId, Capabilities.ACCOUNT_HISTORY_PAGE_VIEW.name());
        RegularSearchQueryBO search = new RegularSearchQueryBOBuilder(request).build();
        search.setCustomer(customerId);
        return service.findShipmentHistory(search, SecurityUtils.getCurrentPersonId());
    }

    private ShipmentStatus toStatus(String basedOnValue) {
        BasedOnDTO value = BasedOnDTO.getValue(basedOnValue);
        switch (value) {
            case BOOKED_DATE:
                return ShipmentStatus.BOOKED;
            case DELIVERY_DATE:
                return ShipmentStatus.DELIVERED;
            case PICKUP_DATE:
                return ShipmentStatus.DISPATCHED;
            default:
                throw new IllegalArgumentException();
        }
    }
}
