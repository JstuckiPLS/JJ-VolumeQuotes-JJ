package com.pls.ltlrating.domain.enums;

import java.util.stream.Stream;

/**
 * Allowed statuses of Freight Analysis jobs.
 *
 * @author Aleksandr Leshchenko
 */
public enum FinancialAnalysisStatus {
    Processing('P'),
    Stopped('S'),
    Completed('C'),
    Deleted('D');

    private final char code;

    FinancialAnalysisStatus(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }

    /**
     * Method returns {@link FinancialAnalysisStatus} by code or throws {@link IllegalArgumentException} if
     * not found.
     *
     * @param code
     *            {@link FinancialAnalysisStatus} code
     * @return {@link FinancialAnalysisStatus}
     */
    public static FinancialAnalysisStatus getByCode(final char code) {
        return Stream.of(FinancialAnalysisStatus.values()).filter(value -> value.code == code).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can not get Shipment Direction by code: " + code));
    }
}
