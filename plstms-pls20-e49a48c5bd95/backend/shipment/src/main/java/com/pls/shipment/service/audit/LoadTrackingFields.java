package com.pls.shipment.service.audit;

import org.apache.commons.lang3.StringUtils;

import com.pls.shipment.domain.LoadEntity;

/**
 * Fields of {@link LoadEntity} what should be logged by system.
 * 
 * @author Artem Arapov
 *
 */
public enum LoadTrackingFields {

    ORGANIZATION("organization", "Customer"),
    CARRIER("carrier", "Carrier"),
    BILL_TO("billTo", "Bill-To"),
    WEIGHT("weight", "Weight"),
    COMMODITY("commodity", "Commodity class"),
    PRO("proNumber", "numbers", "Carrier Reference Number", "Pro"),
    REF("refNumber", "numbers", "Shipment Number", "Shipper Ref"),
    PO("poNumber", "numbers", "Purchase Order #", "PO"),
    BOL("bolNumber", "numbers", "Bill of Lading Number", "BOL"),
    GL("glNumber", "numbers", "GL Number", "GL"),
    SO("soNumber", "numbers", "SO Number", "SO"),
    PU("puNumber", "numbers", "PU Number", "PU"),
    NUMBERS("numbers", "Numbers"),
    TRAILER("trailerNumber", "numbers", "Trailer #", "Trailer"),
    SHIPMENT_DIRECTION("shipmentDirection", "Inbound Outbound"),
    PAYMENT_TERMS("paymentTerms", "Pay Terms"),
    SOURCE("sourceInd", "Source of shipment"),
    FINALIZATION_STATUS("finalizationStatus", "Load invoiced to customer"),
    FRT_RECEIVE_FLAG("frtBillRecvFlag", "vendorBillDetails", "Vendor Bill Received"),
    VENDOR_BILL_DETAILS("vendorBillDetails", "Vendor Bill"),
    AWARD_DATE("awardDate", "Load awarder to Carrier"),
    STATUS("status", "Status");

    private String name;

    private String description;

    private String embeddedClassName;

    private String abbreviation;

    LoadTrackingFields(String name, String description) {
        this.name = name;
        this.description = description;
    }

    LoadTrackingFields(String name, String embeddedClassName, String description) {
        this(name, description);
        this.embeddedClassName = embeddedClassName;
    }

    LoadTrackingFields(String name, String embeddedClassName, String description, String abbreviation) {
        this(name, embeddedClassName, description);
        this.abbreviation = abbreviation;
    }

    /**
     * Returns field name.
     * 
     * @return name of selected field.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns field description.
     * 
     * @return Description of selected field.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns embedded class containing field.
     * 
     * @return embedded class with watched field.
     */
    public String getEmbeddedClassName() {
        return this.embeddedClassName;
    }

    /**
     * Returns abbreviation of watched field.
     * 
     * @return abbreviation of watched field.
     */
    public String getAbbreviation() {
        return this.abbreviation;
    }

    /**
     * Checks that specified field name exists.
     * 
     * @param name - name of field to be checked
     * @return <code>TRUE</code> if specified field was found, otherwise returns <code>FALSE</code>
     */
    public static boolean containsField(String name) {
        return getByFieldName(name) != null;
    }

    /**
     * Find {@link LoadTrackingFields} by specified field name.
     * 
     * @param name - field name
     * @return {@link LoadTrackingFields} if specified field name was found, otherwise returns <code>null</code>.
     */
    public static LoadTrackingFields getByFieldName(String name) {
        for (LoadTrackingFields field : LoadTrackingFields.values()) {
            if (StringUtils.equalsIgnoreCase(field.getName(), name)) {
                return field;
            }
        }
        return null;
    }
}
