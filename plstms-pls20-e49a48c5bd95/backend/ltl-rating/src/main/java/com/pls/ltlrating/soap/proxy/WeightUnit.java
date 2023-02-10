//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.23 at 04:27:03 PM EEST 
//


package com.pls.ltlrating.soap.proxy;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WeightUnit.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WeightUnit">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Pounds"/>
 *     &lt;enumeration value="Kilograms"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WeightUnit")
@XmlEnum
public enum WeightUnit {

    @XmlEnumValue("Pounds")
    POUNDS("Pounds"),
    @XmlEnumValue("Kilograms")
    KILOGRAMS("Kilograms");
    private final String value;

    WeightUnit(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WeightUnit fromValue(String v) {
        for (WeightUnit c: WeightUnit.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
