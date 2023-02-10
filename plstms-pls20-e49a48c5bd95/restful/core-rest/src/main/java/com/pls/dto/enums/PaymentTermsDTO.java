package com.pls.dto.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Payment Terms enumeration for DTO.
 *
 * @author Alexander Kirichenko
 */
public enum PaymentTermsDTO {
    COLLECT, PREPAID, THIRD_PARTY_COLLECT, THIRD_PARTY_PREPAID;

    /**
     * Returns list of all payment terms.
     *
     * @return list of {@link PaymentTermsDTO}
     */
    public static List<PaymentTermsDTO> getList() {
        return Arrays.asList(PaymentTermsDTO.values());
    }
}
