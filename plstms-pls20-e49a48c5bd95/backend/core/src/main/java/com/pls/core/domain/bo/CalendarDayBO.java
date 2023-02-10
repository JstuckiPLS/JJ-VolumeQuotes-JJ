package com.pls.core.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Business object for Customer's user's calendar activity.
 * 
 * @author Sergey Kirichenko
 * @author Aleksandr Leshchenko
 */
public class CalendarDayBO {
    private Date exactDate;
    private Long totalCount;
    private BigDecimal totalCost;

    private CalendarDayBO weeklyTotal;
    private CalendarDayBO monthlyTotal;

    /**
     * Empty default constructor.
     */
    public CalendarDayBO() {
    }

    /**
     * Constructor.
     * 
     * @param exactDate
     *            date with some amount of loads
     * @param totalCount
     *            count.
     * @param totalCost
     *            total cost.
     */
    public CalendarDayBO(Date exactDate, Long totalCount, BigDecimal totalCost) {
        this.exactDate = exactDate;
        this.totalCount = totalCount == null ? 0L : totalCount;
        this.totalCost = totalCost == null ? BigDecimal.ZERO : totalCost;
    }

    public Date getExactDate() {
        return exactDate;
    }

    public void setExactDate(Date exactDate) {
        this.exactDate = exactDate;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public CalendarDayBO getWeeklyTotal() {
        return weeklyTotal;
    }

    public void setWeeklyTotal(CalendarDayBO weeklyTotal) {
        this.weeklyTotal = weeklyTotal;
    }

    public CalendarDayBO getMonthlyTotal() {
        return monthlyTotal;
    }

    public void setMonthlyTotal(CalendarDayBO monthlyTotal) {
        this.monthlyTotal = monthlyTotal;
    }
}
