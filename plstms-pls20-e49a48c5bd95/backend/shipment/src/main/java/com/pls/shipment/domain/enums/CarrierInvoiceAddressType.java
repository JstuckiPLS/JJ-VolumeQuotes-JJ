package com.pls.shipment.domain.enums;

/**
 * Carrier Invoice Address types enumeration.
 *
 * @author Mikhail Boldinov, 02/10/13
 */
public enum CarrierInvoiceAddressType {
    ORIGIN("SH"), DESTINATION("CN"), BILL_TO("BT");

    private String code;

    CarrierInvoiceAddressType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get {@link CarrierInvoiceAddressType} enum instance by id.
     *
     * @param code address type DB code
     * @return instance of this enum
     */
    public static CarrierInvoiceAddressType getByCode(String code) {
        for (CarrierInvoiceAddressType carrierInvoiceAddressType : CarrierInvoiceAddressType.values()) {
            if (carrierInvoiceAddressType.code.equals(code)) {
                return carrierInvoiceAddressType;
            }
        }
        throw new IllegalArgumentException("Can not get CarrierInvoiceAddressType by code: " + code);
    }
}
