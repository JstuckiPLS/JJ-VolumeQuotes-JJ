package com.pls.core.domain.bo;

import java.util.Date;

/**
 * Business Object for Customer Locations.
 * 
 * @author Artem Arapov
 *
 */
public class CustomerLocationListItemBO {

    private Long id;

    private String location;

    private String accountExecutive;

    private Date startDate;

    private Date endDate;

    private String billTo;

    private Boolean defaultNode;

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getAccountExecutive() {
        return accountExecutive;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAccountExecutive(String accountExecutive) {
        this.accountExecutive = accountExecutive;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public Boolean getDefaultNode() {
        return defaultNode;
    }

    public void setDefaultNode(Boolean defaultNode) {
        this.defaultNode = defaultNode;
    }
}
