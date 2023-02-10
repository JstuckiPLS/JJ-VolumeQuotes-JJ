package com.pls.invoice.domain.bo;

import java.io.Serializable;
import java.util.List;

/**
 * BO with data for invoice processing.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class InvoiceProcessingBO implements Serializable {

    private static final long serialVersionUID = -1833998638471319762L;

    private Long billToId;
    private String email;
    private String comments;
    private String subject;
    private List<Long> loadIds;
    private List<Long> adjustmentIds;
    private String consolidatedInvoiceNumber;

    public Long getBillToId() {
        return billToId;
    }
    public void setBillToId(Long billToId) {
        this.billToId = billToId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String emails) {
        this.email = emails;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
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
    public String getConsolidatedInvoiceNumber() {
        return consolidatedInvoiceNumber;
    }
    public void setConsolidatedInvoiceNumber(String consolidatedInvoiceNumber) {
        this.consolidatedInvoiceNumber = consolidatedInvoiceNumber;
    }
}
