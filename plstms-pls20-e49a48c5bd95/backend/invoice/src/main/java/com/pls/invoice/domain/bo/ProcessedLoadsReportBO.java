package com.pls.invoice.domain.bo;

import java.util.List;

/**
 * Bo for Processed Loads XLS Repor.
 * 
 * @author Alexander Nalapko
 *
 */
public class ProcessedLoadsReportBO {
    private String successful;
    private String failed;
    private String customer;
    private String billTo;
    private String email;
    private String invoice;
    private String revenue;
    private String cost;
    private String subject;
    private String comments;

    private List<InvoiceResultBO> loads;

    public List<InvoiceResultBO> getLoads() {
        return loads;
    }

    public void setLoads(List<InvoiceResultBO> loads) {
        this.loads = loads;
    }

    public String getSuccessful() {
        return successful;
    }

    public void setSuccessful(String successful) {
        this.successful = successful;
    }

    public String getFailed() {
        return failed;
    }

    public void setFailed(String failed) {
        this.failed = failed;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
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

}
