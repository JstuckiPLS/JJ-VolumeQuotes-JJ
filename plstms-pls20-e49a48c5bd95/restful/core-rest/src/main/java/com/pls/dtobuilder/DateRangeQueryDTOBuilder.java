package com.pls.dtobuilder;

import com.pls.core.domain.bo.DateRangeQueryBO;
import com.pls.dto.enums.CalendarShipmentDetailsDateRange;
import com.pls.dto.enums.DateRange;
import com.pls.dto.query.DateRangeQueryDTO;
import com.pls.dtobuilder.util.DateUtils;

import java.util.Date;

/**
 * Builder to convert {@link DateRangeQueryDTO} into the {@link DateRangeQueryBO}.
 *
 * @author Artem Arapov
 */
public class DateRangeQueryDTOBuilder extends AbstractDTOBuilder<DateRangeQueryBO, DateRangeQueryDTO> {
    /**
     * This method throws {@link UnsupportedOperationException}.
     *
     * @param bo
     *            {@link DateRangeQueryBO}
     * @return nothing
     * @throws UnsupportedOperationException
     */
    @Override
    public DateRangeQueryDTO buildDTO(DateRangeQueryBO bo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DateRangeQueryBO buildEntity(DateRangeQueryDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Argument 'dto' can't be null");
        }

        DateRange dateRange = DateRange.valueOf(dto.getDateRange());

        Date fromDate = DateUtils.getFromDate(dateRange, dto.getFromDate());
        Date toDate = DateUtils.getToDate(dateRange, dto.getToDate());

        return new DateRangeQueryBO(fromDate, toDate);
    }

    /**
     * Build BO object accordingly to received date and period of time.
     *
     * @param date date to based on
     * @param range specified period of time {@link CalendarShipmentDetailsDateRange}
     * @return result BO
     */
    public DateRangeQueryBO buildBO(Date date, CalendarShipmentDetailsDateRange range) {
        Date fromDate;
        Date toDate;
        switch (range) {
            case SINGLE_DAY:
                fromDate = DateUtils.getStartDateOfToday(date);
                toDate = DateUtils.getEndDateOfToday(date);
                break;
            case WEEK:
                fromDate = DateUtils.getStartDateOfWeek(date);
                toDate = DateUtils.getEndDateOfWeek(date);
                break;
            case MONTH:
                fromDate = DateUtils.getStartDateOfMonth(date);
                toDate = DateUtils.getEndDateOfMonth(date);
                break;
            default:
                throw new IllegalArgumentException(String.format("CalendarShipmentDetailsDateRange: %s does not supported", range));
        }

        return new DateRangeQueryBO(fromDate, toDate);
    }
}
