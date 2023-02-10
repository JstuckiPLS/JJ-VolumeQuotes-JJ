package com.pls.shipment.domain.sterling.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Select Carrier By.
 *
 * @author Jasmin Dhamelia 4/9/2015
 */

@XmlType
@XmlEnum(String.class)
public enum SelectCarrierBy {

    @XmlEnumValue("LC")
    LC,

    @XmlEnumValue("LT")
    LT;
}
