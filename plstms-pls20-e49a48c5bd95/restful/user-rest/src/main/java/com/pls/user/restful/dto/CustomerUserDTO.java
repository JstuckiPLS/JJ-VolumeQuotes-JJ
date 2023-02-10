package com.pls.user.restful.dto;

import java.util.Date;
import java.util.List;

import com.pls.core.domain.user.CustomerUserEntity;

/**
 * DTO for {@link CustomerUserEntity}.
 * 
 * @author Aleksandr Leshchenko
 */
public class CustomerUserDTO {
    private Long customerId;
    private String customerName;
    private String accountExecutive;
    private Boolean multipleAE;
    private Date assignmentDate;
    private Long locationsCount;
    private List<CustomerLocationUserDTO> locations;

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

    public Date getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(Date assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Long getLocationsCount() {
        return locationsCount;
    }

    public void setLocationsCount(Long locationsCount) {
        this.locationsCount = locationsCount;
    }

    public List<CustomerLocationUserDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<CustomerLocationUserDTO> locations) {
        this.locations = locations;
    }
}
