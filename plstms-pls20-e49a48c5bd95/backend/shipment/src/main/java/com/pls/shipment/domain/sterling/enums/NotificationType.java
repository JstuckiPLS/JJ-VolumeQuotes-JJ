package com.pls.shipment.domain.sterling.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Enum class representing the different tracking notifications user can opt to.
 * 
 * @author Pavani Challa
 */

@XmlType
@XmlEnum(String.class)
public enum NotificationType {

    @XmlEnumValue("DISP")
    DISPATCHED,

    @XmlEnumValue("PICK_UP")
    PICK_UP,

    @XmlEnumValue("OUT_FOR_DEL")
    OUT_FOR_DELIVERY,

    @XmlEnumValue("DEL")
    DELIVERED,

    @XmlEnumValue("TRACK_UPD")
    DETAILS;
}
