package com.pls.dto.invoice;

import java.util.Date;

/**
 * DTO to pass CBI processing parameters.
 * 
 * @author Aleksandr Leshchenko
 */
public class CBIProcessingDTO {
    private String emails;
    private Date invoiceDate;

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}
