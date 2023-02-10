package com.pls.core.domain.sterling;

/**
 * The types of Sterling integration messages to send to the financial system.
 * 
 * @author Jasmin Dhamelia
 *
 */
public enum EDIMessageType {

    EDI204_STERLING_MESSAGE_TYPE("204", "LOAD_TENDER"),

    EDI214_STERLING_MESSAGE_TYPE("214", "TRACKING"),

    EDI210_STERLING_MESSAGE_TYPE("210", "INVOICE"),

    CUSTOMER_INVOICE_MESSAGE_TYPE("210", "INVOICE"),

    EDI997_STERLING_MESSAGE_TYPE("997", "ACKNOWLEDGMENT"),

    EDI990_STERLING_MESSAGE_TYPE("990", "TENDER_ACKNOWLEDGEMENT"),

    EDI211_STERLING_MESSAGE_TYPE("211", "BOL"),
    
    LOAD_UPDATE_MESSAGE_TYPE(null, "UPDATE_LOAD"),

    // below four financeMessages
    CUSTOMER(null, "CUSTOMER"),

    VENDOR(null, "VENDOR"),

    AR(null, "AR"),

    AP(null, "AP"),
    
    P44_TRACKING(null, "P44_TRACKING"),

    // for xml files that could not be validated.
    UNKNOWN(null, null);

    private final String ediTransaction;

    private final String code;

    EDIMessageType(String ediTransaction, String code) {
        this.ediTransaction = ediTransaction;
        this.code = code;
    }

    public String getEdiTransaction() {
        return ediTransaction;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get {@link EDIMessageType} by specified message type.
     *
     * @param messageType - type of the message.
     * @return {@link EDIMessageType} or <code>{@link EDIMessageType.UNKNOWN}</code>.
     */
    public static EDIMessageType getEDIMessageByType(String messageType) {
        switch (messageType) {
            case "LOAD_TENDER":
                return EDI204_STERLING_MESSAGE_TYPE;
            case "INVOICE":
                return EDI210_STERLING_MESSAGE_TYPE;
            case "TENDER_ACKNOWLEDGEMENT":
                return EDI990_STERLING_MESSAGE_TYPE;
            case "TRACKING":
                return EDI214_STERLING_MESSAGE_TYPE;
            case "ACKNOWLEDGMENT":
                return EDI997_STERLING_MESSAGE_TYPE;
            default:
                break;
        }
        return UNKNOWN;
    }
}
