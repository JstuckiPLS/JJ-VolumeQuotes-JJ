package com.pls.invoice.service.impl.jms;

import java.io.Serializable;

import com.pls.invoice.domain.bo.SendToFinanceBO;

/**
 * Finance Bill To JMS message.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public class InvoiceJMSMessage implements Serializable {

    private static final long serialVersionUID = 358973216761815271L;

    private Long invoiceId;
    private Long userId;
    private SendToFinanceBO financeBO;

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SendToFinanceBO getFinanceBO() {
        return financeBO;
    }

    public void setFinanceBO(SendToFinanceBO financeBO) {
        this.financeBO = financeBO;
    }
}
