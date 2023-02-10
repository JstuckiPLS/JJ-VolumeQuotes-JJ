package com.pls.invoice.domain.bo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Object that contains Invoice data for customers.
 *
 * @author Alexander Kirichenko
 */
public class CustomerInvoiceBO {
    private Long loadId;
    private Long adjustmentId;
    private String bol;
    private String pro;
    private String ref;
    private String origin;
    private String destination;
    private String invoiceNumber;
    private Date invoiceDate;
    private Date dueDate;
    private Integer overdue;
    private BigDecimal invoiced;
    private BigDecimal paid;
    private BigDecimal due;

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public Long getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(Long adjustmentId) {
        this.adjustmentId = adjustmentId;
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

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getOverdue() {
        return overdue;
    }

    public void setOverdue(Integer overdue) {
        this.overdue = overdue;
    }

    public BigDecimal getInvoiced() {
        return invoiced;
    }

    public void setInvoiced(BigDecimal invoiced) {
        this.invoiced = invoiced;
    }

    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    public BigDecimal getDue() {
        return due;
    }

    public void setDue(BigDecimal due) {
        this.due = due;
    }
}
