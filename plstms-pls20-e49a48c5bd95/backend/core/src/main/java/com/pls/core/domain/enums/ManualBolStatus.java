package com.pls.core.domain.enums;



/**
 * Statuses for manual BOL.
 * 
 * @author Alexander Nalapko
 */
public enum ManualBolStatus implements Status {

    /**
     * "Customer Truck" status.
     */
    CUSTOMER_TRUCK("CT"),

    /**
     * "Canceled" status.
     */
    CANCELLED("C");

    private final String code;

    ManualBolStatus(String code) {
        this.code = code;
    }

    public String getStatusCode() {
        return this.code;
    }

    /**
     * Get {@link ManualBolStatus} by specified status code.
     * 
     * @param code - code of status.
     * @return {@link ManualBolStatus} or <code>null</code>.
     */
    public static ManualBolStatus getByCode(String code) {
        if (code != null) {
            for (ManualBolStatus status : ManualBolStatus.values()) {
                if (code.equalsIgnoreCase(status.code)) {
                    return status;
                }
            }
        }

        return null;
    }
}
