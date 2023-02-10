package com.pls.core.domain.enums;

/**
 * Payment Terms enumeration.
 *
 * @author Mikhail Boldinov, 28/08/13
 */
public enum PaymentTerms {
    COLLECT("COL", "CC", "Collect"), PREPAID("PPD", "PP", "Prepaid"), THIRD_PARTY_COLLECT("TPL", "TP", "Third Party Collect"),
    THIRD_PARTY_PREPAID("TPD", "TP", "Third Party Prepaid");

    private String code;

    private String ediCode;

    private String description;

    PaymentTerms(String code, String ediCode, String description) {
        this.code = code;
        this.ediCode = ediCode;
        this.description = description;
    }

    /**
     * Method returns {@link PaymentTerms} by code.
     *
     * @param code {@link PaymentTerms} code
     * @return {@link PaymentTerms}
     */
    public static PaymentTerms getByCode(String code) {
        for (PaymentTerms paymentTerms : PaymentTerms.values()) {
            if (paymentTerms.code.equals(code)) {
                return paymentTerms;
            }
        }

        throw new IllegalArgumentException("Can not get Payment Terms by code: " + code);
    }

    /**
     * Method returns {@link PaymentTerms} by ediCode.
     *
     * @param ediCode {@link PaymentTerms} ediCode
     * @return {@link PaymentTerms}
     */
    public static PaymentTerms getByEdiCode(String ediCode) {
        for (PaymentTerms paymentTerms : PaymentTerms.values()) {
            if (paymentTerms.ediCode.equals(ediCode)) {
                return paymentTerms;
            }
        }

        throw new IllegalArgumentException("Can not get Payment Terms by ediCode: " + ediCode);
    }

    public String getPaymentTermsCode() {
        return code;
    }

    public String getPaymentTermsEdiCode() {
        return ediCode;
    }

    public String getDescription() {
        return description;
    }
}
