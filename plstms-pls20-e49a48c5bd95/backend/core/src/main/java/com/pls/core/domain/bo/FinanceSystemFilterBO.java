package com.pls.core.domain.bo;

import java.util.Date;

/**
 * Finance system filter BO.
 * 
 * @author Dmitry Nikolaenko
 */
public class FinanceSystemFilterBO {

    private String loadId;
    private String financialStatus;
    private String personId;
    private Date glDate;
    private String holdRelease;
    private String adjAccType;
    private String adjList;

    public String getLoadId() {
        return loadId;
    }
    public void setLoadId(String loadId) {
        this.loadId = loadId;
    }
    public String getFinancialStatus() {
        return financialStatus;
    }
    public void setFinancialStatus(String financialStatus) {
        this.financialStatus = financialStatus;
    }
    public String getPersonId() {
        return personId;
    }
    public void setPersonId(String personId) {
        this.personId = personId;
    }
    public Date getGlDate() {
        return glDate;
    }
    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }
    public String getHoldRelease() {
        return holdRelease;
    }
    public void setHoldRelease(String holdRelease) {
        this.holdRelease = holdRelease;
    }
    public String getAdjAccType() {
        return adjAccType;
    }
    public void setAdjAccType(String adjAccType) {
        this.adjAccType = adjAccType;
    }
    public String getAdjList() {
        return adjList;
    }
    public void setAdjList(String adjList) {
        this.adjList = adjList;
    }
}