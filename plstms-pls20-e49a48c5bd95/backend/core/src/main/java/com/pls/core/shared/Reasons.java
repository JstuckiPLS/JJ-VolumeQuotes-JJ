package com.pls.core.shared;

import org.apache.commons.lang3.StringUtils;

/**
 * Enum class for automatic Reasons.
 * 
 * @author Brichak Aleksandr
 */
public enum Reasons {

    DISPUTE_FINANCE("DF"),
    DISPUTE_ACCOUNT_EXEC("DA"),
    DISPUTE_RESOLVED("DR"),
    REQUEST_PAPERWORK("RP"),
    COST_REVIEW_COMPLETE("RC"),

    LOW_MARGIN("LM"),
    COST_DIFF("CD"),
    MISSING_DOCUMENT("MD"),
    MARGIN_REVIEW("MR"),
    HIGH_REVENUE_REVIEW("HR"),
    ACCESSORIAL_REVIEW("AR"),
    HIGH_COST_REVIEW("HC"),
    MISSING_PAYMENTS_TERM("MP"),
    INVOICE_AUDIT("IA"),

    MISSING_BOL("MB"),
    MISSING_GL("MG"),
    MISSING_PO("MO"),
    MISSING_PU("MU"),
    MISSING_SO("MZ"),
    MISSING_SHIPPER_REF("MH"),
    MISSING_TRAILER_NUMBER("MT"),

    INCORRECT_BOL_NUMBER("BL"),
    INCORRECT_GL_NUMBER("GL"),
    INCORRECT_PO_NUMBER("PO"),
    INCORRECT_PU_NUMBER("UP"),
    INCORRECT_SO_NUMBER("SO"),
    INCORRECT_PRO_NUMBER("PR"),

    INCORRECT_SHIPPER_REF_NUMBER("SF"),
    INCORRECT_TRAILER_NUMBER("TL"),
    INCORRECT_JOB_NUMBER("JB"),
    INCORRECT_CARGO_VALUE("CV"),
    INCORRECT_REQUESTED_BY("RB"),

    READY_FOR_CONSOLIDATED("CR");

    private String reasonCode;

    public static final String INVOICE_FAILED = "IF";
    public static final String INVOICE_FAILED_DESCRIPTION = "Invoice Failed";

    Reasons(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * Method returns {@link Reasons} by reasonCode.
     * 
     * @param reasonCode
     *            - string
     * @return {@link Reasons} or <code>null</code>
     */
    public static Reasons getReasonByCode(String reasonCode) {
        for (Reasons type : Reasons.values()) {
            if (StringUtils.equals(type.reasonCode, reasonCode)) {
                return type;
            }
        }
        return null;
    }

    public String getReasonCode() {
        return reasonCode;
    }
}
