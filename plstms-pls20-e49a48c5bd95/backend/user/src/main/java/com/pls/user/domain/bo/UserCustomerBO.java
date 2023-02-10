package com.pls.user.domain.bo;

import java.util.Date;

/**
 * BO for assigned customers search results.
 * 
 * @author Aleksandr Leshchenko
 */
public class UserCustomerBO {
    private Long customerId;
    private String customerName;
    private String accountExecutive;
    private Boolean multipleAE;
    private Date unassignmentDate;
    private Long locationId;
    private Long locationsCount;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(String accountExecutive) {
        this.accountExecutive = accountExecutive;
    }

    public Boolean getMultipleAE() {
        return multipleAE;
    }

    public void setMultipleAE(Boolean multipleAE) {
        this.multipleAE = multipleAE;
    }

    public Date getUnassignmentDate() {
        return unassignmentDate;
    }

    public void setUnassignmentDate(Date unassignmentDate) {
        this.unassignmentDate = unassignmentDate;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getLocationsCount() {
        return locationsCount;
    }

    public void setLocationsCount(Long locationsCount) {
        this.locationsCount = locationsCount;
    }
}
