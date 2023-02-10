package com.pls.ltlrating.service.impl.file;

import org.apache.commons.lang3.StringUtils;

import com.pls.ltlrating.domain.LtlFuelSurchargeEntity;

/**
 * Field description for importing {@link LtlFuelSurchargeEntity} from excel.
 *
 * @author Stas Norochevskiy
 *
 */
public enum LtlFuelSurchargeFieldsDescription {

    LOW_RANGE("Min. Rate", true), HIGH_RANGE("Max. Rate", true), SURCHARGE("Surcharge", true);

    /**
     * Get appropriated {@link LtlFuelSurchargeFieldsDescription} using column header text.
     *
     * @param header
     *            Header string.
     *
     * @return {@link LtlFuelSurchargeFieldsDescription} if this column was recognized. Otherwise returns
     *         <code>null</code>.
     */
    public static LtlFuelSurchargeFieldsDescription getFromHeaderText(String header) {
        LtlFuelSurchargeFieldsDescription result = null;

        String normalizedHeader = StringUtils.trimToNull(header);

        for (LtlFuelSurchargeFieldsDescription field : LtlFuelSurchargeFieldsDescription.values()) {
            if (StringUtils.equalsIgnoreCase(field.getHeader(), normalizedHeader)) {
                result = field;
                break;
            }
        }

        return result;
    }

    private String header;

    private boolean required;

    LtlFuelSurchargeFieldsDescription(String header, boolean required) {
        this.header = StringUtils.trimToEmpty(header);
        this.required = required;
    }

    /**
     * Get header value.
     *
     * @return the header.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Get required value.
     *
     * @return the required.
     */
    public boolean isRequired() {
        return required;
    }
}
