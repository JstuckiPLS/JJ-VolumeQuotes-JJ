package com.pls.search.domain.vo;

import java.util.Date;

/**
 * Customer Library Value Object.
 * 
 * @author Artem Arapov
 * 
 */
public class CustomerLibraryVO {

    private Long customerId;

    private String customerName;

    private Long accountExecId;

    private String accountExecName;

    private Date createdDate;

    private Date lastLoadDate;

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

    public Long getAccountExecId() {
        return accountExecId;
    }

    public void setAccountExecId(Long accountExecId) {
        this.accountExecId = accountExecId;
    }

    public String getAccountExecName() {
        return accountExecName;
    }

    public void setAccountExecName(String accountExecName) {
        this.accountExecName = accountExecName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastLoadDate() {
        return lastLoadDate;
    }

    public void setLastLoadDate(Date lastLoadDate) {
        this.lastLoadDate = lastLoadDate;
    }
}
