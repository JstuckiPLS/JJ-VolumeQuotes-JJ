package com.pls.shipment.domain.sterling.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Address Type.
 *
 * @author Jasmin Dhamelia 4/8/2015
 */
@XmlType
@XmlEnum(String.class)
public enum AddressType {

    @XmlEnumValue("BILL_TO")
    BILL_TO,

    @XmlEnumValue("ORIGIN")
    ORIGIN,

    @XmlEnumValue("DEST")
    DESTINATION,

    @XmlEnumValue("FRT_CHARGE_TO")
    FREIGHT_CHARGE_TO;

}
