//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.10.23 at 04:27:03 PM EEST 
//


package com.pls.ltlrating.soap.proxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetRate2Result" type="{http://dayrossgroup.com/web/public/webservices/shipmentServices}ArrayOfServiceLevels" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getRate2Result"
})
@XmlRootElement(name = "GetRate2Response")
public class GetRate2Response {

    @XmlElement(name = "GetRate2Result")
    protected ArrayOfServiceLevels getRate2Result;

    /**
     * Gets the value of the getRate2Result property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfServiceLevels }
     *     
     */
    public ArrayOfServiceLevels getGetRate2Result() {
        return getRate2Result;
    }

    /**
     * Sets the value of the getRate2Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfServiceLevels }
     *     
     */
    public void setGetRate2Result(ArrayOfServiceLevels value) {
        this.getRate2Result = value;
    }

}