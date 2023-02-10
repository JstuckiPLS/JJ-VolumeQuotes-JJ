package com.pls.dto;

import java.util.List;

import com.pls.ltlrating.integration.ltllifecycle.dto.p44.P44AccountDTO;

public class P44ConfigDTO {
    
    private String customerName;
    private String accountGroupCode;
    private List<P44AccountDTO> accounts;
    
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getAccountGroupCode() {
        return accountGroupCode;
    }
    public void setAccountGroupCode(String accountGroup) {
        this.accountGroupCode = accountGroup;
    }
    public List<P44AccountDTO> getAccounts() {
        return accounts;
    }
    public void setAccounts(List<P44AccountDTO> accounts) {
        this.accounts = accounts;
    }

}
