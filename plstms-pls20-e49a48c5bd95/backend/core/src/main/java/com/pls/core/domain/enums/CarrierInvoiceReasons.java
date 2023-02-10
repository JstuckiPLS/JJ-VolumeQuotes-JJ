package com.pls.core.domain.enums;

/**
 * Enum class for Vendor Bill Reasons.
 * 
 * @author Alexander Nalapko
 */
public enum CarrierInvoiceReasons {

    DUPLICATE("DP"), NOT_OURS_TO_PAY("NP"), SHORT_PAY("SP"), ALREADY_INVOICED("AI"), LOAD_1_0("L1"), OTHER("OR");

    private String code;

    CarrierInvoiceReasons(String reasonCode) {
        this.code = reasonCode;
    }

    /**
     * Method returns {@link CarrierInvoiceReasons} by code.
     * 
     * @param code
     *            - string
     * @return {@link CarrierInvoiceReasons} or <code>null</code>
     */
    public static CarrierInvoiceReasons getReasonByCode(String code) {
        for (CarrierInvoiceReasons reason : CarrierInvoiceReasons.values()) {
            if (reason.code.equals(code)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unexpected '" + code + "' value for reasons");
    }

    public String getReasonCode() {
        return code;
    }

}
