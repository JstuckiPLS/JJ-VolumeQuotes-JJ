package com.pls.shipment.domain.enums;

/**
 * Segment errors enumeration.
 *
 * @author Mikhail Boldinov, 28/07/14
 */
public enum EDISegmentErrorCode {
    UNRECOGNIZED_ID(1, "Unrecognized segment ID"),
    UNEXPECTED(2, "Unexpected segment"),
    MANDATORY_MISSING(3, "Mandatory segment missing"),
    LOOP_OCCURS_OVER_MAX_TIMES(4, "Loop Occurs Over Maximum Times"),
    EXCEEDS_MAX_USE(5, "Segment Exceeds Maximum Use"),
    NOT_DEFINED_TRANSACTION_SET(6, "Segment Not in Defined Transaction Set"),
    NOT_PROPER_SEQUENCE(7, "Segment Not in Proper Sequence"),
    DATA_ELEMENT_ERRORS(8, "Segment Has Data Element Errors");

    private int id;

    private String message;

    /**
     * Constructor.
     *
     * @param id error id
     * @param message error message
     */
    EDISegmentErrorCode(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
