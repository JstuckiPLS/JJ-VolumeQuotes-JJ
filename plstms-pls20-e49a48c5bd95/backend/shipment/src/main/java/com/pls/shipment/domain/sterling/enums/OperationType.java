package com.pls.shipment.domain.sterling.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Operation type enumeration.
 *
 * @author Jasmin Dhamelia 4/8/2015
 */

@XmlType
@XmlEnum(String.class)
public enum OperationType {

    @XmlEnumValue("CREATE")
    TENDER,

    @XmlEnumValue("UPDATE")
    UPDATE,

    @XmlEnumValue("CANCEL")
    CANCEL;

}
