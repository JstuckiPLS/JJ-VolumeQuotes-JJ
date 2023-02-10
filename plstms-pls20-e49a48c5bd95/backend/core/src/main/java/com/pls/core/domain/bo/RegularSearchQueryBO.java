package com.pls.core.domain.bo;

import java.util.Date;

/**
 * Holds all regular search query parameters.
 * 
 * @author Dmitriy Nefedchenko
 */
public class RegularSearchQueryBO {
    private Date fromDate;
    private Date toDate;
    private String bol;
    private String pro;
    private String originZip;
    private String destinationZip;
    private Long carrier;
    private Long loadId;
    private String dateSearchField;
    private String job;
    private String invoiceNumber;
    private String customerInvoiceNumber;
    private Long customer;
    private Long accountExecutive;
    private String po;

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

    public String getBol() {
        return bol;
    }

    public void setBol(String bol) {
        this.bol = bol;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getOriginZip() {
        return originZip;
    }

    public void setOriginZip(String originZip) {
        this.originZip = originZip;
    }

    public String getDestinationZip() {
        return destinationZip;
    }

    public void setDestinationZip(String destinationZip) {
        this.destinationZip = destinationZip;
    }

    public Long getCarrier() {
        return carrier;
    }

    public void setCarrier(Long carrier) {
        this.carrier = carrier;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getDateSearchField() {
        return dateSearchField;
    }

    public void setDateSearchField(String dateSearchField) {
        this.dateSearchField = dateSearchField;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerInvoiceNumber() {
        return customerInvoiceNumber;
    }

    public void setCustomerInvoiceNumber(String customerInvoiceNumber) {
        this.customerInvoiceNumber = customerInvoiceNumber;
    }

    public Long getCustomer() {
        return customer;
    }

    public void setCustomer(Long customer) {
        this.customer = customer;
    }

    public Long getAccountExecutive() {
        return accountExecutive;
    }

    public void setAccountExecutive(Long accountExecutive) {
        this.accountExecutive = accountExecutive;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }
}
