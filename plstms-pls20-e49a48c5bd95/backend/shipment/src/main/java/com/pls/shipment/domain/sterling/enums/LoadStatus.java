package com.pls.shipment.domain.sterling.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Load Status type enumeration.
 *
 * @author Jasmin Dhamelia 4/8/2015
 */
@XmlType
@XmlEnum(String.class)
@XmlRootElement(name = "LoadStatus")
public enum LoadStatus {

    @XmlEnumValue("O")
    OPEN,

    @XmlEnumValue("B")
    BOOK,

    @XmlEnumValue("D")
    DISPATCH;

}
