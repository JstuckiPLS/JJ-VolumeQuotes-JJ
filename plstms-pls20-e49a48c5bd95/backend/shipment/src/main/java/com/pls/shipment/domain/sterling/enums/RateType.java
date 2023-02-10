package com.pls.shipment.domain.sterling.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Rate type enumeration.
 *
 * @author Jasmin Dhamelia 4/9/2015
 */
@XmlType
@XmlEnum(String.class)
public enum RateType {

    @XmlEnumValue("S")
    SHIPPER,

    @XmlEnumValue("C")
    CARRIER,

    @XmlEnumValue("B")
    BENCHMARK;

}
