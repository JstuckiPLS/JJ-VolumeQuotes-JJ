package com.pls.shipment.service.impl.edi.parser.enums;

import static com.pls.shipment.service.impl.edi.utils.EDIUtils.element;
import static com.pls.shipment.service.impl.edi.utils.EDIUtils.toStr;

import com.pls.shipment.service.edi.parser.EDIParseableElement;
import com.pls.shipment.service.impl.edi.parser.EDI214Parser;

/**
 * EDI 214 elements enumeration.
 * 
 * @author Aleksandr Leshchenko
 */
public enum EDI214Element implements EDIParseableElement {
    PRO_NUM("127", "B10", EDI214Parser.IDENTIFIERS, 1, "PRO #", false),
    BOL("145", "B10", EDI214Parser.IDENTIFIERS, 2, "BOL #", true),
    SCAC("140", "B10", EDI214Parser.IDENTIFIERS, 3, "SCAC", true),
    LINE_NUM("554", "LX", EDI214Parser.LINE_NUMBER, 1, "Line number", false),
    STATUS("1650", "AT7", EDI214Parser.TRACKING_DETAILS, 1, "Status code", false),
    STATUS_REASON("1651", "AT7", EDI214Parser.TRACKING_DETAILS, 2, "Status reason code", false),
    TRACK_DATE("373", "AT7", EDI214Parser.TRACKING_DETAILS, 5, "Tracking Date", false),
    TRACK_TIME("337", "AT7", EDI214Parser.TRACKING_DETAILS, 6, "Tracking Time", false),
    TRACK_TIMEZONE("623", "AT7", EDI214Parser.TRACKING_DETAILS, 7, "Tracking Timezone", false),
    TRACK_CITY("19", "MS1", EDI214Parser.TRACKING_POINT, 1, "Tracking Point City", false),
    TRACK_STATE("156", "MS1", EDI214Parser.TRACKING_POINT, 2, "Tracking Point State", false),
    TRACK_COUNTRY("26", "MS1", EDI214Parser.TRACKING_POINT, 3, "Tracking Point Country", false),
    EDI_ACCOUNT("127", "L11", EDI214Parser.REFERENCE_NUMBER, 1, "Customer alpha numeric", false);

    private String id;
    private String segment;
    private String configName;
    private int index;
    private String plsValue;
    private boolean mandatory;

    EDI214Element(String id, String segment, String configName, int index, String plsValue, boolean mandatory) {
        this.id = id;
        this.configName = configName;
        this.plsValue = plsValue;
        this.segment = segment;
        this.index = index;
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