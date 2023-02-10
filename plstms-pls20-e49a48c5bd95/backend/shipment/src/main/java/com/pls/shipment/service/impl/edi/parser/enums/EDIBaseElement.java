package com.pls.shipment.service.impl.edi.parser.enums;

import static com.pls.shipment.service.impl.edi.utils.EDIUtils.element;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toStr;

import com.pls.shipment.service.edi.parser.EDIParseableElement;
import com.pls.shipment.service.impl.edi.parser.AbstractEDIParser;

/**
 * Basic EDI elements enumeration.
 * 
 * @author Aleksandr Leshchenko
 */
public enum EDIBaseElement implements EDIParseableElement {
    ISA_SCAC("I06", "ISA", AbstractEDIParser.INTERCHANGE_CONTROL_HEADER, 6, "Carrier SCAC (ISA)", true),
    GS_SCAC("142", "GS", AbstractEDIParser.FUNCTIONAL_GROUP_HEADER, 2, "Carrier SCAC (GS)", true),
    ST_TRANSACTION_SET_ID("143", "ST", AbstractEDIParser.TRANSACTION_SET_HEADER, 1, "Transaction Set ID", true);

    private String segment;
    private String configName;
    private int index;
    private String plsValue;
    private String id;
    private boolean mandatory;

    EDIBaseElement(String id, String segment, String configName, int index, String plsValue, boolean mandatory) {
        this.id = id;
        this.index = index;
        this.segment = segment;
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