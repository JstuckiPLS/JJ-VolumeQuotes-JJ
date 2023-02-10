package com.pls.core.shared;

/**
 * Enumeration for billTo required fields.
 * 
 * @author Brichak Aleksandr
 */
public enum BillToRequiredField {
    BOL("BOL", "BOL#"),
    GL("GL", "GL#"),
    PO("PO", "PO#"),
    PU("PU", "PU#"),
    SO("SO", "SO#"),
    SHIPPER_REF("SR", "Shipper Ref#"),
    TRAILER("TR", "Trailer#"),
    JOB("JOB", "Job#"),
    REQUESTED_BY("RB", "Requested By"),
    PRO("PRO", "Pro#"),
    CARGO("CARGO", "Cargo value");

    private String code;

    private String description;

    BillToRequiredField(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Method returns {@link BillToRequiredField} by code.
     *
     * @param code {@link BillToRequiredField} code
     * @return {@link BillToRequiredField}
     */
    public static BillToRequiredField getByCode(String code) {
        for (BillToRequiredField requiredField : BillToRequiredField.values()) {
            if (requiredField.code.equals(code)) {
                return requiredField;
            }
        }
        throw new IllegalArgumentException("Can not get bill to required field by code: " + code);
    }
}
