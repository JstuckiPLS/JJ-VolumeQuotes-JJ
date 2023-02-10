package com.pls.invoice.domain.bo;

import java.util.List;

/**
 * BO for reprocess history.
 * 
 * @author Dmitry Nikolaenko
 */
public class ReprocessHistoryBO {
    private Long invoiceId;
    private Boolean financial;
    private Boolean customerEmail;
    private String emails;
    private String subject;
    private String comments;
    private Boolean customerEDI;
    private List<Long> loadIds;
    private List<Long> adjustmentIds;
    private String invoiceNumber;

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Boolean getFinancial() {
        return financial;
    }

    public void setFinancial(Boolean financial) {
        this.financial = financial;
    }

    public Boolean getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(Boolean customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getCustomerEDI() {
        return customerEDI;
    }

    public void setCustomerEDI(Boolean customerEDI) {
        this.customerEDI = customerEDI;
    }

    public List<Long> getLoadIds() {
        return loadIds;
    }

    public void setLoadIds(List<Long> loadIds) {
        this.loadIds = loadIds;
    }

    public List<Long> getAdjustmentIds() {
        return adjustmentIds;
    }

    public void setAdjustmentIds(List<Long> adjustmentIds) {
        this.adjustmentIds = adjustmentIds;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
