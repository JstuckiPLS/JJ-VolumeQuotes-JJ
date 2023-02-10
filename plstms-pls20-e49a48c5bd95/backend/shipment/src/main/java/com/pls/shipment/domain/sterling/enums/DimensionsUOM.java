package com.pls.shipment.domain.sterling.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Dimension UOM Type.
 *
 * @author Jasmin Dhamelia 4/8/2015
 */
@XmlType
@XmlEnum(String.class)
public enum DimensionsUOM {

    @XmlEnumValue("INCH")
    INCH,

    @XmlEnumValue("CM")
    CM;

}
