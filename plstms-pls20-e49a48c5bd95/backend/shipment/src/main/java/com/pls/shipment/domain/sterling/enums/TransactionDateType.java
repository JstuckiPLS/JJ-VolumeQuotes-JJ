package com.pls.shipment.domain.sterling.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Enum class representing type of the transaction date on a load like confirm pickup date, confirm delivery date etc.
 * 
 * @author Pavani Challa
 *
 */
@XmlType
@XmlEnum(String.class)
public enum TransactionDateType {

    @XmlEnumValue("GATE_ARR")
    GATE_ARRIVAL,

    @XmlEnumValue("GATE_ADM")
    GATE_ADMIT,

    @XmlEnumValue("CONF_PU")
    CONFIRM_PICKUP,

    @XmlEnumValue("GATE_DEP")
    GATE_DEPARTURE,

    @XmlEnumValue("LOADED")
    LOADED,

    @XmlEnumValue("CONF_DEL")
    CONFIRM_DELIVERY,

    @XmlEnumValue("EST_DEL")
    ESTIMATED_DELIVERY,

    @XmlEnumValue("EST_PU")
    ESTIMATED_PICKUP
}
