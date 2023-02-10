package com.pls.core.domain.bo;

/**
 * Manage billing status BO.
 * 
 * @author Dmitry Nikolaenko
 * 
 */
public class ManageBillingStatusBO {

    private String loadsCount;
    private String financialId;

    public String getLoadsCount() {
        return loadsCount;
    }

    public void setLoadsCount(String loadsCount) {
        this.loadsCount = loadsCount;
    }

    public String getFinancialId() {
        return financialId;
    }

    public void setFinancialId(String financialId) {
        this.financialId = financialId;
    }
}
