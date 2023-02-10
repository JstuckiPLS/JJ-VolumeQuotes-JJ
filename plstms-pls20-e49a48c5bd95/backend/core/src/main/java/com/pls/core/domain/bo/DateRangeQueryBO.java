package com.pls.core.domain.bo;

import java.util.Date;

/**
 * Date range information.
 * 
 * @author Artem Arapov
 */
public class DateRangeQueryBO {

    private Date fromDate;

    private Date toDate;

    /**
     * Default constructor.
     */
    public DateRangeQueryBO() {
    }

    /**
     * Constructor.
     * 
     * @param fromDate Start date.
     * @param toDate End date.
     */
    public DateRangeQueryBO(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
