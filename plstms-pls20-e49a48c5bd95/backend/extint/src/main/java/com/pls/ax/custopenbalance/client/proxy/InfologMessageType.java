package com.pls.ax.custopenbalance.client.proxy;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for InfologMessageType.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <p>
 * 
 * @author Thomas Clancy
 *
 */
@XmlType(name = "InfologMessageType", namespace
    = "http://schemas.datacontract.org/2004/07/Microsoft.Dynamics.AX.Framework.Services")
@XmlEnum
@SuppressWarnings("PMD")
public enum InfologMessageType {

    @XmlEnumValue("Info")
    INFO("Info"),
    @XmlEnumValue("Warning")
    WARNING("Warning"),
    @XmlEnumValue("Error")
    ERROR("Error");
    private final String value;

    /**
     * Primary constructor.
     * 
     * @param v A string.
     */
    InfologMessageType(String v) {
        value = v;
    }

    /**
     * Returns the value.
     * 
     * @return The value.
     */
    public String value() {
        return value;
    }

    /**
     * Factory method.
     * 
     * @param v A string.
     * @return An instance of InfoMessageType.
     */
    public static InfologMessageType fromValue(String v) {
        for (InfologMessageType c : InfologMessageType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
