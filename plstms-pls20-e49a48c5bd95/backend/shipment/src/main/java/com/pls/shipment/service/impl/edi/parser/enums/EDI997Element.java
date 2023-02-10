package com.pls.shipment.service.impl.edi.parser.enums;

import static com.pls.shipment.service.impl.edi.utils.EDIUtils.element;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toStr;

import com.pls.shipment.service.edi.parser.EDIParseableElement;
import com.pls.shipment.service.impl.edi.parser.EDI997Parser;

/**
 * EDI 990 elements enumeration.
 * 
 * @author Alexander Nalapko
 */
public enum EDI997Element implements EDIParseableElement {
    ID("28", "AK1", EDI997Parser.IDENTIFIERS, 2, "id", true),
    STATUS("715", "AK9", EDI997Parser.TRAILER, 1, "Status", true);


    private String segment;
    private String configName;
    private int index;
    private boolean mandatory;
    private String id;
    private String plsValue;

    EDI997Element(String id, String segment, String configName, int index, String plsValue, boolean mandatory) {
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
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public int getIndex() {
        return index;
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