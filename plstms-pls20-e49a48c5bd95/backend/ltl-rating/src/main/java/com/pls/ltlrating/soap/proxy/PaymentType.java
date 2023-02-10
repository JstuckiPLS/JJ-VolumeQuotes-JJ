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
 * <p>Java class for PaymentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PaymentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown"/>
 *     &lt;enumeration value="Prepaid"/>
 *     &lt;enumeration value="CashPrepaid"/>
 *     &lt;enumeration value="Collect"/>
 *     &lt;enumeration value="CashCollect"/>
 *     &lt;enumeration value="ThirdParty"/>
 *     &lt;enumeration value="CreditCard"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PaymentType")
@XmlEnum
public enum PaymentType {

    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("Prepaid")
    PREPAID("Prepaid"),
    @XmlEnumValue("CashPrepaid")
    CASH_PREPAID("CashPrepaid"),
    @XmlEnumValue("Collect")
    COLLECT("Collect"),
    @XmlEnumValue("CashCollect")
    CASH_COLLECT("CashCollect"),
    @XmlEnumValue("ThirdParty")
    THIRD_PARTY("ThirdParty"),
    @XmlEnumValue("CreditCard")
    CREDIT_CARD("CreditCard");
    private final String value;

    PaymentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PaymentType fromValue(String v) {
        for (PaymentType c: PaymentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}