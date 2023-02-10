package com.pls.search.domain.co;

import java.util.Date;

import com.pls.core.shared.Status;

/**
 * Criteria Object for Searching Customers.
 * 
 * @author Artem Arapov
 * 
 */
public class GetCustomerCO {

    private Date fromDate;

    private Date toDate;

    private Status status;

    private Long personId;

    private Date fromLoadDate;

    private Date toLoadDate;

    private String name;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Date getFromLoadDate() {
        return fromLoadDate;
    }

    public void setFromLoadDate(Date fromLoadDate) {
        this.fromLoadDate = fromLoadDate;
    }

    public Date getToLoadDate() {
        return toLoadDate;
    }

    public void setToLoadDate(Date toLoadDate) {
        this.toLoadDate = toLoadDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
