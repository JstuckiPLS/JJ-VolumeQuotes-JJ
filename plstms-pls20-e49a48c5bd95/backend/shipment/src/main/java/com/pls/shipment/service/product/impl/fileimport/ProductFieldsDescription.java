package com.pls.shipment.service.product.impl.fileimport;

import org.apache.commons.lang3.StringUtils;

import com.pls.shipment.domain.LtlProductEntity;
import com.pls.shipment.domain.LtlProductHazmatInfo;

/**
 * Represents fields of {@link LtlProductEntity} entity. Also holds column header information for Excel file.
 * 
 * @author Artem Arapov
 *
 */
public enum ProductFieldsDescription {

    /**
     * Data for {@link LtlProductEntity#getDescription()} field.
     */
    DESCRIPTION("Product Description", true, LtlProductEntity.DESCRIPTION_LENGTH),

    /**
     * Data for {@link LtlProductEntity#getNmfcNum()} field.
     */
    NMFC_NUM("NMFC #", false, LtlProductEntity.NMFC_NUM_LENGHT),

    /**
     * Data for {@link LtlProductEntity#getCommodityClass()} field.
     */
    COMMODITY_CLASS("Commodity Class", true, 8),

    /**
     * Data for {@link LtlProductEntity#isHazmat()} field.
     */
    HAZMAT_FLAG("Hazmat (yes or no)", true, null),

    /**
     * Data for {@link LtlProductHazmatInfo#getUnNumber()} field.
     */
    HAZMAT_UN("UN# (if Hazmat is Yes)", true, 32),

    /**
     * Data for {@link LtlProductHazmatInfo#getPackingGroup()} field.
     */
    HAZMAT_PACKING_CODE("Packaging Group (if Hazmat is Yes)", true, 32),

    /**
     * Data for {@link LtlProductHazmatInfo#getHazmatClass()} field.
     */
    HAZMAT_CLASS("Hazmat Class (if Hazmat is Yes)", true, LtlProductHazmatInfo.HAZMAT_CLASS_LENGTH),

    /**
     * Data for {@link LtlProductHazmatInfo#getEmergencyCompany()} field.
     */
    HAZMAT_EMERGENCY_COMPANY("Emergency Response Company(if Hazmat is Yes)", false, LtlProductHazmatInfo.EMERGENCY_COMPANY_LENGTH),

    /**
     * Data for {@link LtlProductHazmatInfo#getEmergencyPhone()} field.
     */
    HAZMAT_EMERGENCY_PHONE("Emergency Response Phone(if Hazmat is Yes)", false, 13),

    /**
     * Data for {@link LtlProductHazmatInfo#getEmergencyPhone().getExtension()} field.
     */
    EXTENSION("Extension(if Hazmat is Yes)", false, 13),

    /**
     * Data for {@link LtlProductHazmatInfo#getEmergencyContract()} field.
     */
    HAZMAT_EMERGENCY_CONTRACT_NUM("Emergency Response Contract # (if Hazmat is Yes)", false, 32),

    /**
     * Data for {@link LtlProductHazmatInfo#getInstructions()} field.
     */
    HAZMAT_INSTRUCTIONS("Emergency Response Instructions(if Hazmat is Yes)", false, LtlProductHazmatInfo.HAZMAT_INSTRUCTIONS_LENGTH),

    /**
     * Data for {@link LtlProductEntity#getProductCode()} field.
     */
    SKU("SKU/ Product  Code", false, LtlProductEntity.PRODUCT_CODE_LENGHT),

    /**
     * Data for {@link LtlProductEntity#isShared()} field.
     */
    SHARED("Shared ( yes or no)", true, null);

    /**
     * Get appropriated {@link ProductFieldsDescription} using column header text.
     * 
     * @param header
     *            Header string.
     * 
     * @return {@link ProductFieldsDescription} if this column was recognized. Otherwise returns <code>null</code>.
     */
    public static ProductFieldsDescription getFromHeaderText(String header) {
        ProductFieldsDescription result = null;

        String normalizedHeader = StringUtils.trimToNull(header);

        for (ProductFieldsDescription field : ProductFieldsDescription.values()) {
            if (StringUtils.equalsIgnoreCase(field.getHeader(), normalizedHeader)) {
                result = field;
                break;
            }
        }

        return result;
    }

    private String header;

    private boolean required;

    private Integer maxLength;

    ProductFieldsDescription(String header, boolean required, Integer maxLength) {
        this.header = StringUtils.trimToEmpty(header);
        this.required = required;
        this.maxLength = maxLength;
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

    /**
     * Get max length value.
     * 
     * @return max length.
     */
    public Integer getMaxLength() {
        return maxLength;
    }

}
