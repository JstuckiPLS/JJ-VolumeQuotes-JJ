package com.pls.dto.invoice.enums;

/**
 * History Invoice Status enumeration.
 *
 * @author Sergey Kirichenko
 */
public enum HistoryInvoiceStatusDTO {
    UNPAID("Unpaid"), SHORT_PAID("Short Paid"), PAID("Paid");

    private String label;

    public String getLabel() {
        return label;
    }

    HistoryInvoiceStatusDTO(String label) {
        this.label = label;
    }
}
