package com.pls.documentmanagement.domain.enums;

/**
 * Types of existing documents for shipments.
 *
 * @author Denis Zhupinsky (Team International)
 */
public enum DocumentTypes {
    BOL("BOL"), SHIPPING_LABELS("SHIPPING_LABELS"), INVOICE("INVOICE"), VENDOR_BILL("VENDOR BILL"), MISCELLANEOUS("Miscellaneous"),
    TEMP("TEMP"), UNKNOWN("UNKNOWN"), CONSIGNEE_INVOICE("CONSIGNEE_INVOICE"), CERTIFICATE_OF_INS("CERTIFICATE_OF_INS");

    private String dbValue;

    DocumentTypes(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
