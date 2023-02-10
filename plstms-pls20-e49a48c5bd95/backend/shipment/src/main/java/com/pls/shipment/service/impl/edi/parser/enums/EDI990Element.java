package com.pls.shipment.service.impl.edi.parser.enums;

import static com.pls.shipment.service.impl.edi.utils.EDIUtils.element;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toStr;

import com.pls.shipment.service.edi.parser.EDIParseableElement;
import com.pls.shipment.service.impl.edi.parser.EDI990Parser;

/**
 * EDI 990 elements enumeration.
 * 
 * @author Aleksandr Leshchenko
 */
public enum EDI990Element implements EDIParseableElement {
    SCAC("140", "B1", EDI990Parser.IDENTIFIERS, 1, "SCAC", true),
    BOL("145", "B1", EDI990Parser.IDENTIFIERS, 2, "BOL #", true),
    STATE("558", "B1", EDI990Parser.IDENTIFIERS, 4, "STATE", true),
    PRO_NUMBER("127", "N9", EDI990Parser.REFERENCE_IDENTIFIERS, 2, "PRO Number", false),
    NUMBER("206", "N7", EDI990Parser.EQUIPMENT, 1, "Equipment Number", false),
    K1_MESSAGE_CODE("61", "K1", EDI990Parser.REMARKS, 1, "Free-Form Message Type", false),
    K1_MESSAGE("61", "K1", EDI990Parser.REMARKS, 2, "Free-Form Message", false),
    V9_EVENT_CODE("304", "V9", EDI990Parser.EVENT_DETAIL, 1, "Event Code", false),
    V9_STATUS_REASON_CODE("641", "V9", EDI990Parser.EVENT_DETAIL, 8, "Status Reason Code", false),
    PICKUP_CONFIRMATION("127", "N9", EDI990Parser.REFERENCE_IDENTIFIERS, 2, "Carrier Pickup Confirmation", false);

    private String segment;
    private String configName;
    private int index;
    private String plsValue;
    private String id;
    private boolean mandatory;

    EDI990Element(String id, String segment, String configName, int index, String plsValue, boolean mandatory) {
        this.id = id;
        this.segment = segment;
        this.index = index;
        this.configName = configName;
        this.plsValue = plsValue;
        this.mandatory = mandatory;
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
    public String getPlsValue() {
        return plsValue;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public String getElementId() {
        return id;
    }

    @Override
    public String toString() {
        return segment + element(toStr(index), 2, "0", true);
    }
}