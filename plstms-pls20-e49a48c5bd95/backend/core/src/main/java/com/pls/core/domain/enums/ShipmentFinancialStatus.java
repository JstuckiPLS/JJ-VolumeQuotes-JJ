package com.pls.core.domain.enums;

/**
 * Financial Statuses for shipment.
 * 
 * @see FLATBED#LOADS_FINAN_STATUS_TYPES
 * 
 * @author Aleksandr Leshchenko
 */
public enum ShipmentFinancialStatus {
    FIELD_BILLING("FB"),
    FIELD_BILLING_HOLD("FBH"),
    ACCOUNTING_BILLING("AB"),                                   // Transactional / CBI
    ACCOUNTING_BILLING_RELEASE("FP"),                           // Invoice History / Invoice Errors
    NONE("NF"),                                                 // Tracking Board
    READY_FOR_FIELD_BILLING("RB"),
    ACCOUNTING_BILLING_HOLD("ABH"),                             // Invoice Audit
    PRICING_AUDIT_HOLD("PAH"),                                  // Billing Hold
    FIELD_BILLING_ADJUSTMENT("FBAD"),
    FIELD_BILLING_ACCESSORIAL("FBAC"),
    FIELD_BILLING_HOLD_ADJUSTMENT("FBHAD"),                     // Invoice Errors
    FIELD_BILLING_HOLD_ACCESSORIAL("FBHAC"),
    ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL("ABAA"),          // Transactional / CBI
    ACCOUNTING_BILLING_HOLD_ADJUSTMENT_ACCESSORIAL("ABHAA"),    // Invoice Audit
    FINANCE_HOLD("FH");                                         // Tracking Board (Hold)


    private final String statusCode;

    ShipmentFinancialStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Get {@link ShipmentFinancialStatus} by Status Code.
     * 
     * @param code
     *            status code
     * @return {@link ShipmentFinancialStatus} or <code>null</code>
     */
    public static ShipmentFinancialStatus getByCode(String code) {
        if (code != null) {
            for (ShipmentFinancialStatus status : values()) {
                if (status.statusCode.equals(code)) {
                    return status;
                }
            }
        }
        return null;
    }
}
