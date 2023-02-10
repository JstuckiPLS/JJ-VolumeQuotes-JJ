package com.pls.core.domain.enums;

import com.pls.core.domain.user.Capabilities;

/**
 * Type of history email.
 * 
 * @author Sergii Belodon
 */
public enum EmailType {
    DOCUMENT(Capabilities.VIEW_DOCUMENTS_EMAIL_HISTORY, "Document"),
    PEN_PAY(Capabilities.VIEW_PEN_PAY_EMAIL_HISTORY, "Pending Payment"),
    INVOICE(Capabilities.VIEW_INVOICES_EMAIL_HISTORY, "Invoice"),
    NOTIFICATION(Capabilities.VIEW_NOTIFICATIONS_EMAIL_HISTORY, "Notification"),
    CONFIRMATION(Capabilities.VIEW_CONFIRMATION_EMAIL_HISTORY, "Confirmation"),
    EDI(null, null),
    NOT_AUDITABLE(null, null),
    REASON(null, null);

    private Capabilities capability;
    private String name;

    EmailType(Capabilities capability, String name) {
        this.capability = capability;
        this.name = name;
    }
    public Capabilities getCapability() {
        return capability;
    }
    public String getName() {
        return name;
    }
}
