package com.pls.shipment.service.impl.edi.parser.enums;

import static com.pls.shipment.service.impl.edi.utils.EDIUtils.element;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toStr;

import com.pls.shipment.service.edi.parser.EDIParseableElement;
import com.pls.shipment.service.impl.edi.parser.EDI210Parser;

/**
 * EDI 210 elements enumeration.
 * 
 * @author Aleksandr Leshchenko
 */
public enum EDI210Element implements EDIParseableElement {
    INVOICE_NUMBER("76", "B3", EDI210Parser.INVOICE_DETAILS, 2, "Invoice Number", true),
    INVOICE_DATE("373", "B3", EDI210Parser.INVOICE_DETAILS, 6, "Invoice Date", true),
    REF_NUMBER("145", "B3", EDI210Parser.INVOICE_DETAILS, 3, "Reference Number", false),
    PAY_TERMS("146", "B3", EDI210Parser.INVOICE_DETAILS, 4, "Payment Terms", false),
    NET_AMOUNT("193", "B3", EDI210Parser.INVOICE_DETAILS, 7, "Net Amount", true),
    DELIVERY_DATE("32", "B3", EDI210Parser.INVOICE_DETAILS, 9, "Delivery Date", true),
    BOL_NUMBER("127", "N9", EDI210Parser.BOL_NUMBER, 2, "BOL Number", false),
    PO_NUMBER("127", "N9", EDI210Parser.PO_NUMBER, 2, "PO Number", false),
    SHIPPER_REF_NUMBER("127", "N9", EDI210Parser.SHIPPER_REF_NUMBER, 2, "Shipper Reference Number", false),
    PRO_NUMBER("127", "N9", EDI210Parser.PRO_NUMBER, 2, "PRO Number", false),
    PU_NUMBER("127", "N9", EDI210Parser.PU_NUMBER, 2, "PU Number", false),
    EDI_NUMBER("127", "N9", EDI210Parser.EDI_ACCOUNT, 2, "Customer EDI Account Number", false),
    ACTUAL_PICKUP_DATE("373", "G62", EDI210Parser.ACTUAL_PICKUP_DATE, 2, "Actual Pickup Date", true),
    WEIGHT("81", "L0", EDI210Parser.LINE_ITEMS_L0, 4, "Weight", false),
    QUANTITY("80", "L0", EDI210Parser.LINE_ITEMS_L0, 8, "Quantity", false),
    PACKAGING_CODE("211", "L0", EDI210Parser.LINE_ITEMS_L0, 9, "Packaging Code", false),
    CHARGE("58", "L1", EDI210Parser.LINE_ITEMS_L1, 4, "Charge", false),
    SPECIAL_CHARGE_CODE("150", "L1", EDI210Parser.LINE_ITEMS_L1, 8, "Special Charge Code", false),
    ORDER_NUM("213", "L5", EDI210Parser.LINE_ITEMS_L5, 1, "Order Number", false),
    DESCRIPTION("79", "L5", EDI210Parser.LINE_ITEMS_L5, 2, "Description", false),
    COMMODITY_CODE("22", "L5", EDI210Parser.LINE_ITEMS_L5, 3, "NMFC", false),
    COMMODITY_CLASS_CODE("59", "L7", EDI210Parser.LINE_ITEMS_L7, 7, "Commodity Class Code", false),
    TOTAL_WEIGHT("81", "L3", EDI210Parser.TOTAL, 1, "Total Weight", true),
    TOTAL_CHARGES("58", "L3", EDI210Parser.TOTAL, 5, "Total Charges", true),
    TOTAL_QUANTITY("80", "L3", EDI210Parser.TOTAL, 11, "Total Quantity", true),
    ORIGIN_ADDRESS("93", "N1", EDI210Parser.ORIGIN_ADDRESS, 2, "Origin Address Name", true),
    DESTINATION_ADDRESS("93", "N1", EDI210Parser.DESTINATION_ADDRESS, 2, "Destination Address Name", true),
    BILL_TO_ADDRESS("93", "N1", EDI210Parser.BILL_TO_ADDRESS, 2, "Bill To Address Name", true),
    ADDRESS1("166", "N3", EDI210Parser.ADDRESS, 1, "Address 1", true),
    ADDRESS2("166", "N3", EDI210Parser.ADDRESS, 2, "Address 2", false),
    CITY("19", "N4", EDI210Parser.CITY_ST_ZIP, 1, "City", true),
    STATE("156", "N4", EDI210Parser.CITY_ST_ZIP, 2, "State", true),
    ZIP("116", "N4", EDI210Parser.CITY_ST_ZIP, 3, "Zip", true),
    COUNTRY("26", "N4", EDI210Parser.CITY_ST_ZIP, 4, "Country", false);

    private String id;
    private String segment;
    private String configName;
    private int index;
    private String plsValue;
    private boolean mandatory;

    EDI210Element(String id, String segment, String configName, int index, String plsValue, boolean mandatory) {
        this.id = id;
        this.index = index;
        this.segment = segment;
        this.configName = configName;
        this.mandatory = mandatory;
        this.plsValue = plsValue;
    }

    @Override
    public String getSegment() {
        return segment;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getPlsValue() {
        return plsValue;
    }

    @Override
    public String getElementId() {
        return id;
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public String toString() {
        return segment + element(toStr(index), 2, "0", true);
    }
}