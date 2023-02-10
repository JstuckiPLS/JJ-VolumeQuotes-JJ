package com.pls.shipment.domain.enums;

/**
 * Element errors enumeration.
 *
 * @author Alexander Nalapko
 */
public enum EDIElementErrorCode {

    MANDATORY_MISSING(1, "Mandatory data element missing"), INVALID_CHARACTER(6, "Invalid character in data element");
    /**
     * 723 - Data Element Syntax Error Code
     * 
     * 1 Mandatory data element missing. 2 Conditional required data element missing. 3 Too many data
     * elements. More data elements existed than defined for the segment 4 Data element too short. 5 Data
     * element too long. 6 Invalid character in data element. 7 Invalid code value. 8 Invalid Date 9 Invalid
     * Time 10 Exclusion Condition Violated 12 Too Many Repetitions More repetitions existed than defined for
     * the segment 13 Too Many Components More components existed than defined for the element 16 Composite
     * Data Structure Contains Excess Trailing Delimiters
     */

    private int id;

    private String message;

    /**
     * Constructor.
     *
     * @param id
     *            error id
     * @param message
     *            error message
     */
    EDIElementErrorCode(int id, String message) {
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
